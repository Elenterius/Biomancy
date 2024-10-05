package com.github.elenterius.biomancy.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public class DecomposerMenu extends PlayerContainerMenu {

	protected final Level level;
	private final DecomposerBlockEntity decomposer;

	protected DecomposerMenu(int id, Inventory playerInventory, @Nullable DecomposerBlockEntity decomposer) {
		super(ModMenuTypes.DECOMPOSER.get(), id, playerInventory, 111, 169);
		level = playerInventory.player.level();

		this.decomposer = decomposer;

		if (decomposer != null) {
			addSlot(new SlotItemHandler(decomposer.getFuelInventory(), 0, 39, 66));
			addSlot(new SlotItemHandler(decomposer.getInputInventory(), 0, 66, 26));

			int posY = 48;
			int posX = 88;
			IItemHandler itemHandler = decomposer.getOutputInventory();
			addSlot(new SlotItemHandler(itemHandler, 0, posX, posY));
			addSlot(new SlotItemHandler(itemHandler, 1, posX, posY + 18));
			addSlot(new SlotItemHandler(itemHandler, 2, posX + 18, posY));
			addSlot(new SlotItemHandler(itemHandler, 3, posX + 18, posY + 18));
			addSlot(new SlotItemHandler(itemHandler, 4, posX + 18 * 2, posY));
			addSlot(new SlotItemHandler(itemHandler, 5, posX + 18 * 2, posY + 18));

			addDataSlots(decomposer.getStateData());
		}
	}

	public static DecomposerMenu createServerMenu(int screenId, Inventory playerInventory, DecomposerBlockEntity decomposer) {
		return new DecomposerMenu(screenId, playerInventory, decomposer);
	}

	public static DecomposerMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		DecomposerBlockEntity decomposer = playerInventory.player.level().getBlockEntity(extraData.readBlockPos()) instanceof DecomposerBlockEntity be ? be : null;
		return new DecomposerMenu(screenId, playerInventory, decomposer);
	}

	@Override
	public boolean stillValid(Player player) {
		return decomposer != null && decomposer.canPlayerInteract(player);
	}

	public float getCraftingProgressNormalized() {
		if (decomposer.getStateData().timeForCompletion == 0) return 0f;
		return Mth.clamp(decomposer.getStateData().timeElapsed / (float) decomposer.getStateData().timeForCompletion, 0f, 1f);
	}

	public float getFuelAmountNormalized() {
		return Mth.clamp((float) decomposer.getStateData().fuelHandler.getFuelAmount() / decomposer.getStateData().fuelHandler.getMaxFuelAmount(), 0f, 1f);
	}

	public int getFuelAmount() {
		return decomposer.getStateData().fuelHandler.getFuelAmount();
	}

	public int getMaxFuelAmount() {
		return decomposer.getStateData().fuelHandler.getMaxFuelAmount();
	}

	public int getFuelCost() {
		return decomposer.getStateData().getFuelCost();
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
		if (DecomposerBlockEntity.RECIPE_TYPE.get().getRecipeForIngredient(level, stackInSlot).isPresent()) {
			return mergeInto(SlotZone.INPUT_ZONE, stackInSlot, false);
		}
		return false;
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

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY, DecomposerBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE, DecomposerBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE, DecomposerBlockEntity.OUTPUT_SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(SlotZone slotZone, int numberOfSlots) {
			this(slotZone.lastIndexPlus1, numberOfSlots);
		}

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
