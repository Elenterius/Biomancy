package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.util.FuelUtil;
import com.github.elenterius.biomancy.world.block.entity.DigesterBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.state.DigesterStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.slot.FuelSlot;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import com.github.elenterius.biomancy.world.inventory.slot.OutputSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

public class DigesterMenu extends PlayerContainerMenu {

	protected final Level level;
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;
	private final DigesterStateData stateData;

	protected DigesterMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, BehavioralInventory<?> inputInventory, BehavioralInventory<?> outputInventory, DigesterStateData stateData) {
		super(ModMenuTypes.DIGESTER.get(), id, playerInventory, 111, 169);
		level = playerInventory.player.level;

		this.fuelInventory = fuelInventory;
		this.inputInventory = inputInventory;
		this.outputInventory = outputInventory;
		this.stateData = stateData;

		addSlot(new FuelSlot(fuelInventory, 0, 46, 57));

		addSlot(new Slot(inputInventory, 0, 80, 24));

		addSlot(new OutputSlot(outputInventory, 0, 70, 68));
		addSlot(new OutputSlot(outputInventory, 1, 91, 68));

		addDataSlots(stateData);
	}

	public static DigesterMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> fuelInventory, BehavioralInventory<?> inputInventory, BehavioralInventory<?> outputInventory, DigesterStateData stateData) {
		return new DigesterMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static DigesterMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		BehavioralInventory<?> fuelInventory = BehavioralInventory.createClientContents(DigesterBlockEntity.FUEL_SLOTS);
		BehavioralInventory<?> inputInventory = BehavioralInventory.createClientContents(DigesterBlockEntity.INPUT_SLOTS);
		BehavioralInventory<?> outputInventory = BehavioralInventory.createClientContents(DigesterBlockEntity.OUTPUT_SLOTS);
		return new DigesterMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, new DigesterStateData());
	}

	@Override
	public boolean stillValid(Player player) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputInventory.stillValid(player);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return Mth.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public float getFuelAmountNormalized() {
		return Mth.clamp((float) stateData.getFuelAmount() / DigesterBlockEntity.MAX_FUEL, 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	public int getMAxFuelAmount() {
		return DigesterBlockEntity.MAX_FUEL;
	}

	public int getTotalFuelCost() {
		return stateData.timeForCompletion * DigesterBlockEntity.FUEL_COST;
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
		if (DigesterBlockEntity.RECIPE_TYPE.getRecipeForIngredient(level, stackInSlot).isPresent()) {
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
		FUEL_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, DigesterBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE.lastIndexPlus1, DigesterBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE.lastIndexPlus1, DigesterBlockEntity.OUTPUT_SLOTS);

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