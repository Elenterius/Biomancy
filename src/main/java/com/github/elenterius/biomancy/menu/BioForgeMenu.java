package com.github.elenterius.biomancy.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.block.bioforge.BioForgeBlockEntity;
import com.github.elenterius.biomancy.block.bioforge.BioForgeStateData;
import com.github.elenterius.biomancy.crafting.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.menu.slot.FuelSlot;
import com.github.elenterius.biomancy.menu.slot.ISlotZone;
import com.github.elenterius.biomancy.menu.slot.OutputSlot;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.List;

public class BioForgeMenu extends PlayerContainerMenu {

	final ResultContainer resultContainer = new ResultContainer();
	final int resultSlotIndex;
	private final BehavioralInventory<?> fuelInventory;
	private final BioForgeStateData stateData;
	private int playerInvChanges;
	private final StackedContents itemCounter = new StackedContents();
	@Nullable
	private BioForgeRecipe selectedRecipe;

	protected BioForgeMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, BioForgeStateData stateData) {
		super(ModMenuTypes.BIO_FORGE.get(), id, playerInventory, 124, 137, 195);

		this.fuelInventory = fuelInventory;
		this.fuelInventory.startOpen(playerInventory.player);
		this.stateData = stateData;

		addSlot(new FuelSlot(fuelInventory, 0, 139, 53));
		resultSlotIndex = addSlot(new CustomResultSlot(playerInventory.player, resultContainer, 0, 194 + 2, 33 + 2)).index;

		addDataSlots(stateData);
	}

	private ContainerLevelAccess containerLevelAccess = ContainerLevelAccess.NULL; //only exists on the logical server side
	private long prevSoundTime;

	public BioForgeStateData getStateData() {
		return stateData;
	}

	public BioForgeMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, BioForgeStateData stateData, ContainerLevelAccess containerLevelAccess) {
		this(id, playerInventory, fuelInventory, stateData);
		this.containerLevelAccess = containerLevelAccess;
	}

	public static BioForgeMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf buffer) {
		BehavioralInventory<?> fuelInventory = BehavioralInventory.createClientContents(BioForgeBlockEntity.FUEL_SLOTS);

		BioForgeStateData stateData;
		if (playerInventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof BioForgeBlockEntity bioForge) {
			stateData = bioForge.getStateData();
		}
		else {
			FuelHandler fuelHandler = FuelHandler.createNutrientFuelHandler(BioForgeBlockEntity.MAX_FUEL, () -> {});
			stateData = new BioForgeStateData(fuelHandler);
		}

		return new BioForgeMenu(screenId, playerInventory, fuelInventory, stateData);
	}

	@Override
	protected void onPlayerMainInventoryChanged(Inventory inventory) {
		if (!inventory.player.level().isClientSide && inventory.player instanceof ServerPlayer serverPlayer) {
			trackPlayerInvChanges(serverPlayer, inventory);
		}
	}

	private void trackPlayerInvChanges(ServerPlayer serverPlayer, Inventory inventory) {
		if (playerInvChanges != inventory.getTimesChanged()) {
			countPlayerInvItems(serverPlayer, inventory);
			playerInvChanges = inventory.getTimesChanged();
		}
	}

	private void countPlayerInvItems(ServerPlayer serverPlayer, Inventory inventory) {
		itemCounter.clear();
		inventory.fillStackedContents(itemCounter);
		updateResultSlot(serverPlayer);
	}

	private void updateResultSlot(ServerPlayer serverPlayer) {
		ItemStack resultStack = ItemStack.EMPTY;

		BioForgeRecipe recipe = getSelectedRecipe();
		if (recipe != null && resultContainer.setRecipeUsed(serverPlayer.level(), serverPlayer, recipe) && canCraft(serverPlayer, recipe)) {
			resultStack = recipe.getResultItem(serverPlayer.level().registryAccess()).copy();
		}

		resultContainer.setItem(0, resultStack);
		setRemoteSlot(0, resultStack);
		//serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), resultSlotIndex, resultStack));
		broadcastChanges();
	}

	@Nullable
	public BioForgeRecipe getSelectedRecipe() {
		return selectedRecipe;
	}

	public void setSelectedRecipe(@Nullable BioForgeRecipe recipe, ServerPlayer serverPlayer) {
		selectedRecipe = recipe;
		countPlayerInvItems(serverPlayer, serverPlayer.getInventory());
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		resultContainer.clearContent();
		fuelInventory.stopOpen(player);
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != resultContainer && super.canTakeItemForPickAll(stack, slot);
	}

	@Override
	public boolean stillValid(Player player) {
		return fuelInventory.stillValid(player);
	}
	public float getFuelAmountNormalized() {
		return Mth.clamp((float) getFuelAmount() / BioForgeBlockEntity.MAX_FUEL, 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.fuelHandler.getFuelAmount();
	}

	public int getMaxFuelAmount() {
		return BioForgeBlockEntity.MAX_FUEL;
	}

	public boolean isResultEmpty() {
		return resultContainer.isEmpty();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		ItemStack copyOfStack = stackInSlot.copy();

		SlotZone slotZone = SlotZone.getZoneFromIndex(index);
		boolean successfulTransfer = switch (slotZone) {
			case OUTPUT_ZONE -> mergeIntoPlayerMainInventory(stackInSlot);
			case FUEL_ZONE -> mergeIntoEither(SlotZone.PLAYER_MAIN_INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
			case PLAYER_HOTBAR, PLAYER_MAIN_INVENTORY -> mergeIntoFuelZone(stackInSlot) || mergeIntoPlayerZone(slotZone, stackInSlot);
		};

		if (!successfulTransfer) return ItemStack.EMPTY;
		if (slotZone == SlotZone.OUTPUT_ZONE) {
			stackInSlot.getItem().onCraftedBy(stackInSlot, player.level(), player);
			slot.onQuickCraft(stackInSlot, copyOfStack);
		}

		if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		if (stackInSlot.getCount() == copyOfStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("BioForgeMenu"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		slot.onTake(player, stackInSlot);
		return copyOfStack;
	}

	private boolean mergeIntoFuelZone(ItemStack stackInSlot) {
		if (Nutrients.isValidFuel(stackInSlot)) {
			return mergeInto(SlotZone.FUEL_ZONE, stackInSlot, true);
		}
		return false;
	}

	private boolean mergeIntoPlayerZone(SlotZone slotZone, ItemStack stackInSlot) {
		if (slotZone == SlotZone.PLAYER_HOTBAR) {
			return mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
		}
		return mergeInto(SlotZone.PLAYER_HOTBAR, stackInSlot, false);
	}

	private boolean mergeIntoPlayerMainInventory(ItemStack stackInSlot) {
		return moveItemStackTo(stackInSlot, SlotZone.PLAYER_HOTBAR.getFirstIndex(), SlotZone.PLAYER_MAIN_INVENTORY.getLastIndexPlusOne(), true);
	}

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, BioForgeBlockEntity.FUEL_SLOTS),
		OUTPUT_ZONE(FUEL_ZONE.lastIndexPlus1, 1);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			slotCount = numberOfSlots;
			lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}

		@Override
		public int getFirstIndex() {
			return firstIndex;
		}

		@Override
		public int getLastIndexPlusOne() {
			return lastIndexPlus1;
		}

		@Override
		public int getSlotCount() {
			return slotCount;
		}

	}

	public static BioForgeMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> fuelInventory, BioForgeStateData stateData, ContainerLevelAccess containerLevelAccess) {
		return new BioForgeMenu(screenId, playerInventory, fuelInventory, stateData, containerLevelAccess);
	}

	private class CustomResultSlot extends OutputSlot {

		private final Player player;
		private int removeCount;

		public CustomResultSlot(Player player, ResultContainer container, int index, int x, int y) {
			super(container, index, x, y);
			this.player = player;
		}

		@Override
		public ItemStack remove(int amount) {
			if (hasItem()) {
				removeCount += Math.min(amount, getItem().getCount());
			}
			return super.remove(amount);
		}

		@Override
		protected void onQuickCraft(ItemStack stack, int amount) {
			removeCount += amount;
			checkTakeAchievements(stack);
		}

		@Override
		protected void onSwapCraft(int craftedItems) {
			removeCount += craftedItems;
		}

		@Override
		protected void checkTakeAchievements(ItemStack stack) {
			if (removeCount > 0) stack.onCraftedBy(player.level(), player, removeCount);
			//((ResultContainer) container).awardUsedRecipes(player, List.of());
			removeCount = 0;
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			if (player.level().isClientSide) {
				setChanged();
				return;
			}

			BioForgeRecipe recipe = getSelectedRecipe();
			if (recipe != null) consumeCraftingIngredients(player, recipe);
			checkTakeAchievements(stack);
			onPlayerMainInventoryChanged(player.getInventory()); //ensures the recipe output slot is filled again if possible, integral for quick crafting to work

			containerLevelAccess.execute((level, pos) -> {
				long time = level.getGameTime();
				if (prevSoundTime != time) {
					SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_BIO_FORGE_TAKE_RESULT);
					prevSoundTime = time;
				}
			});

			setChanged();
		}

	}

	private boolean canCraft(Player player, @Nullable BioForgeRecipe recipe) {
		if (recipe == null || getFuelAmount() < recipe.getCraftingCostNutrients() || !recipe.isCraftable(itemCounter)) return false;

		Inventory inventory = player.getInventory();

		//count available ingredients
		List<IngredientStack> ingredients = recipe.getIngredientQuantities();
		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < inventory.items.size(); idx++) {
			ItemStack foundStack = inventory.items.get(idx);
			if (!foundStack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					if (ingredients.get(i).testItem(foundStack)) {
						countedIngredients[i] += foundStack.getCount();
						break;
					}
				}
			}
		}

		//check that all requirements are fulfilled
		for (int i = 0; i < ingredients.size(); i++) {
			int requiredCount = ingredients.get(i).count();
			if (countedIngredients[i] < requiredCount) return false;
		}

		return true;
	}

	private void consumeCraftingIngredients(Player player, BioForgeRecipe recipe) {

		List<IngredientStack> ingredients = recipe.getIngredientQuantities();
		int[] ingredientCost = new int[ingredients.size()];
		for (int i = 0; i < ingredients.size(); i++) {
			ingredientCost[i] = ingredients.get(i).count();
		}

		Inventory inventory = player.getInventory();

		//consume ingredients
		for (int idx = 0; idx < inventory.items.size(); idx++) {
			ItemStack foundStack = inventory.items.get(idx);
			if (!foundStack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					int remainingCost = ingredientCost[i];
					if (remainingCost > 0 && ingredients.get(i).testItem(foundStack)) {
						int amount = Math.min(remainingCost, foundStack.getCount());
						foundStack.shrink(amount);
						ingredientCost[i] -= amount;
						break;
					}
				}
			}
		}

		inventory.setChanged();

		//consume nutrients
		stateData.fuelHandler.addFuelAmount(-recipe.getCraftingCostNutrients());
	}

}
