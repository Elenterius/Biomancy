package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.util.FuelUtil;
import com.github.elenterius.biomancy.world.block.entity.BioForgeBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.state.BioForgeStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.slot.FuelSlot;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import com.github.elenterius.biomancy.world.inventory.slot.OutputSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BioForgeMenu extends PlayerContainerMenu {

	public static final int X_OFFSET = 176;

	protected final Level level;
	private final BehavioralInventory<?> fuelInventory;
	private final SimpleInventory inputInventory;
	private final BehavioralInventory<?> outputInventory;
	private final BioForgeStateData stateData;

	public final Consumer<BioForgeRecipe> selectedRecipeConsumer;

	protected BioForgeMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioForgeStateData stateData, Consumer<BioForgeRecipe> selectedRecipeConsumer) {
		super(ModMenuTypes.BIO_FORGE.get(), id, playerInventory, 8 + X_OFFSET, 137, 195);
		level = playerInventory.player.level;

		this.selectedRecipeConsumer = selectedRecipeConsumer;

		this.fuelInventory = fuelInventory;
		this.inputInventory = inputInventory;
		this.outputInventory = outputInventory;
		this.stateData = stateData;

		addSlot(new FuelSlot(fuelInventory, 0, 27 + X_OFFSET, 56));

		addSlot(new Slot(inputInventory, 0, 52 + X_OFFSET, 67));
		addSlot(new Slot(inputInventory, 1, 58 + X_OFFSET, 89));
		addSlot(new Slot(inputInventory, 2, 80 + X_OFFSET, 97));
		addSlot(new Slot(inputInventory, 3, 102 + X_OFFSET, 89));
		addSlot(new Slot(inputInventory, 4, 108 + X_OFFSET, 67));

		addSlot(new Slot(inputInventory, 5, 80 + X_OFFSET, 67)); //reactant slot

		addSlot(new OutputSlot(outputInventory, 0, 80 + X_OFFSET, 19));

		addDataSlots(stateData);
	}

	public static BioForgeMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioForgeStateData stateData, Consumer<BioForgeRecipe> selectedRecipeConsumer) {
		return new BioForgeMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData, selectedRecipeConsumer);
	}

	public static BioForgeMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf buffer) {
		BehavioralInventory<?> fuelInventory = BehavioralInventory.createClientContents(BioForgeBlockEntity.FUEL_SLOTS);
		SimpleInventory inputInventory = SimpleInventory.createClientContents(BioForgeBlockEntity.INPUT_SLOTS);
		BehavioralInventory<?> outputInventory = BehavioralInventory.createClientContents(BioForgeBlockEntity.OUTPUT_SLOTS);

		BioForgeStateData stateData;
		if (playerInventory.player.level.getBlockEntity(buffer.readBlockPos()) instanceof BioForgeBlockEntity bioForge) {
			stateData = bioForge.getStateData();
		}
		else stateData = new BioForgeStateData();

		return new BioForgeMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData, recipeId -> {});
	}

	@Override
	public boolean stillValid(Player player) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputInventory.stillValid(player);
	}

	@Nullable
	public ResourceLocation getSelectedRecipeId() {
		return stateData.selectedRecipeId;
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return Mth.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public int getTotalFuelCost() {
		return stateData.timeForCompletion * BioForgeBlockEntity.FUEL_COST;
	}

	public float getFuelAmountNormalized() {
		return Mth.clamp((float) stateData.getFuelAmount() / BioForgeBlockEntity.MAX_FUEL, 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	public boolean isOutputEmpty() {
		return outputInventory.isEmpty();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		ItemStack copyOfStack = stackInSlot.copy();

		SlotZone slotZone = SlotZone.getZoneFromIndex(index);
		boolean successfulTransfer = switch (slotZone) {
			case OUTPUT_ZONE -> mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
			case INPUT_ZONE, FUEL_ZONE -> mergeIntoEither(SlotZone.PLAYER_MAIN_INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
			case PLAYER_HOTBAR, PLAYER_MAIN_INVENTORY -> mergeIntoInputZone(stackInSlot) || mergeIntoFuelZone(stackInSlot) || mergeIntoPlayerZone(slotZone, stackInSlot);
		};

		if (!successfulTransfer) return ItemStack.EMPTY;
		if (slotZone == SlotZone.OUTPUT_ZONE) slot.onQuickCraft(stackInSlot, copyOfStack);

		if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		if (stackInSlot.getCount() == copyOfStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("DecomposerMenu"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		slot.onTake(player, stackInSlot);
		return copyOfStack;
	}

	private boolean mergeIntoInputZone(ItemStack stackInSlot) {
		if (BioForgeBlockEntity.RECIPE_TYPE.getRecipeForIngredient(level, stackInSlot).isPresent()) {
			return mergeInto(SlotZone.INPUT_ZONE, stackInSlot, false);
		}
		return false;
	}

	private boolean mergeIntoFuelZone(ItemStack stackInSlot) {
		if (FuelUtil.isItemValidFuel(stackInSlot)) {
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

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, BioForgeBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE.lastIndexPlus1, BioForgeBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE.lastIndexPlus1, BioForgeBlockEntity.OUTPUT_SLOTS);

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

}
