package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.util.FuelUtil;
import com.github.elenterius.biomancy.world.block.entity.BioLabBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.state.BioLabStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

public class BioLabMenu extends PlayerContainerMenu {

	private final BehavioralInventory<?> fuelInventory;
	private final SimpleInventory inputInventory;
	private final BehavioralInventory<?> outputInventory;
	private final BioLabStateData stateData;
	protected final Level level;

	protected BioLabMenu(int id, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioLabStateData stateData) {
		super(ModMenuTypes.BIO_LAB.get(), id, playerInventory);
		level = playerInventory.player.level;

		this.fuelInventory = fuelInventory;
		this.inputInventory = inputInventory;
		this.outputInventory = outputInventory;
		this.stateData = stateData;

		int posY = 17;
		addSlot(new Slot(fuelInventory, 0, 17, posY) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return BioLabBlockEntity.VALID_FUEL_ITEM.test(stack);
			}
		});

		int posX = 62;
		addSlot(new Slot(inputInventory, 0, posX, posY));
		addSlot(new Slot(inputInventory, 1, posX + 18, posY));
		addSlot(new Slot(inputInventory, 2, posX + 18 * 2, posY));
		addSlot(new Slot(inputInventory, 3, posX + 18 * 3, posY));
		addSlot(new Slot(inputInventory, 4, posX + 18 * 4, posY));

		posY += 18 * 2;
		addSlot(new Slot(outputInventory, 0, posX, posY));
		addSlot(new Slot(outputInventory, 1, posX + 18, posY));
		addSlot(new Slot(outputInventory, 2, posX + 18 * 2, posY));

		addDataSlots(stateData);
	}

	public static BioLabMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> fuelInventory, SimpleInventory inputInventory, BehavioralInventory<?> outputInventory, BioLabStateData stateData) {
		return new BioLabMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static BioLabMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		BehavioralInventory<?> fuelInventory = BehavioralInventory.createClientContents(BioLabBlockEntity.FUEL_SLOTS);
		SimpleInventory inputInventory = SimpleInventory.createClientContents(BioLabBlockEntity.INPUT_SLOTS);
		BehavioralInventory<?> outputInventory = BehavioralInventory.createClientContents(BioLabBlockEntity.OUTPUT_SLOTS);
		return new BioLabMenu(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, new BioLabStateData());
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
		return Mth.clamp((float) stateData.getFuelAmount() / BioLabBlockEntity.MAX_FUEL, 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		ItemStack copyOfStack = stackInSlot.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(index);

		switch (sourceZone) {
			case OUTPUT_ZONE -> {
				successfulTransfer = mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
				if (successfulTransfer) slot.onQuickCraft(stackInSlot, copyOfStack);
			}
			case INPUT_ZONE, FUEL_ZONE -> {
				successfulTransfer = mergeIntoEither(SlotZone.PLAYER_MAIN_INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
			}
			case PLAYER_HOTBAR, PLAYER_MAIN_INVENTORY -> {
				if (BioLabBlockEntity.RECIPE_TYPE.getRecipeForIngredient(level, stackInSlot).isPresent()) {
					successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, stackInSlot, false);
				}
				if (!successfulTransfer && FuelUtil.isItemValidFuel(stackInSlot)) {
					successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, stackInSlot, true);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, stackInSlot, false);
				}
			}
			default -> throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		if (stackInSlot.getCount() == copyOfStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("DecomposerMenu"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		slot.onTake(player, stackInSlot);
		return copyOfStack;
	}

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, BioLabBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE.lastIndexPlus1, BioLabBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE.lastIndexPlus1, BioLabBlockEntity.OUTPUT_SLOTS);

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