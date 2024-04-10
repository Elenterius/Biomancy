package com.github.elenterius.biomancy.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.block.biolab.BioLabStateData;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.menu.slot.FuelSlot;
import com.github.elenterius.biomancy.menu.slot.ISlotZone;
import com.github.elenterius.biomancy.menu.slot.OutputSlot;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Predicate;

public class BioLabMenu extends PlayerContainerMenu {

	protected final Level level;
	private final Predicate<Player> isMenuValidPredicate;
	private final BioLabStateData stateData;

	protected BioLabMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioLabStateData stateData) {
		super(ModMenuTypes.BIO_LAB.get(), id, playerInventory, 137, 195);
		level = playerInventory.player.level();

		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		isMenuValidPredicate = inputInventory::stillValid;

		this.stateData = stateData;

		addSlot(new FuelSlot(fuelInventory, 0, 31, 88));

		addSlot(new Slot(inputInventory, 0, 50, 28));
		addSlot(new Slot(inputInventory, 1, 70, 28));
		addSlot(new Slot(inputInventory, 2, 90, 28));
		addSlot(new Slot(inputInventory, 3, 110, 28));

		addSlot(new Slot(inputInventory, 4, 80, 62)); // reactant/vial slot

		addSlot(new OutputSlot(outputInventory, 0, 80, 88));

		addDataSlots(stateData);
	}

	public static BioLabMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioLabStateData stateData) {
		return new BioLabMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static BioLabMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		BehavioralInventory<?> fuelInventory = BehavioralInventory.createClientContents(BioLabBlockEntity.FUEL_SLOTS);
		SimpleInventory inputInventory = SimpleInventory.createClientContents(BioLabBlockEntity.INPUT_SLOTS);
		BehavioralInventory<?> outputInventory = BehavioralInventory.createClientContents(BioLabBlockEntity.OUTPUT_SLOTS);
		FuelHandler fuelHandler = FuelHandler.createNutrientFuelHandler(BioLabBlockEntity.MAX_FUEL, () -> {});
		BioLabStateData state = new BioLabStateData(fuelHandler);
		return new BioLabMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, state);
	}

	@Override
	public boolean stillValid(Player player) {
		return isMenuValidPredicate.test(player);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return Mth.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public int getFuelCost() {
		return stateData.getFuelCost();
	}

	public float getFuelAmountNormalized() {
		return Mth.clamp((float) stateData.fuelHandler.getFuelAmount() / stateData.fuelHandler.getMaxFuelAmount(), 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.fuelHandler.getFuelAmount();
	}

	public int getMaxFuelAmount() {
		return stateData.fuelHandler.getMaxFuelAmount();
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
		if (BioLabBlockEntity.RECIPE_TYPE.get().getRecipeForIngredient(level, stackInSlot).isPresent()) {
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
		FUEL_ZONE(PLAYER_MAIN_INVENTORY, BioLabBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE, BioLabBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE, BioLabBlockEntity.OUTPUT_SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(SlotZone prevSlotZone, int numberOfSlots) {
			this(prevSlotZone.lastIndexPlus1, numberOfSlots);
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
