package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class DigesterContainer extends Container {

	protected final SimpleInvContents fuelContents;
	protected final SimpleInvContents fuelOutContents;
	protected final SimpleInvContents inputContents;
	protected final SimpleInvContents outputContents;
	private final DigesterStateData stateData;
	private final World world;

	private DigesterContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents fuelContents, SimpleInvContents fuelOutContents, SimpleInvContents inputContents, SimpleInvContents outputContents, DigesterStateData stateData) {
		super(ModContainerTypes.DIGESTER.get(), screenId);
		this.fuelContents = fuelContents;
		this.fuelOutContents = fuelOutContents;
		this.inputContents = inputContents;
		this.outputContents = outputContents;
		this.stateData = stateData;
		world = playerInventory.player.world;

		trackIntArray(stateData);

		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int HOT_BAR_SIZE = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < HOT_BAR_SIZE; idx++) { //hotbar
			addSlot(new SlotItemHandler(playerInventoryForge, idx, 8 + SLOT_X_SPACING * idx, 142));
		}

		// Add the players main inventory
		final int PLAYER_INVENTORY_POS_X = 8;
		final int PLAYER_INVENTORY_POS_Y = 84;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = HOT_BAR_SIZE + y * 9 + x;
				int posX = PLAYER_INVENTORY_POS_X + x * SLOT_X_SPACING;
				int posY = PLAYER_INVENTORY_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}

		addSlot(new Slot(fuelContents, 0, 17, 17) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return DigesterTileEntity.VALID_FUEL_ITEM.test(stack);
			}
		});

		addSlot(new Slot(inputContents, 0, 71, 26));

		addSlot(new OutputSlot(outputContents, 0, 107, 26));

		addSlot(new OutputSlot(fuelOutContents, 0, 17, 44));
	}

	public static DigesterContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents fuelContents, SimpleInvContents fuelOutContents, SimpleInvContents inputContents, SimpleInvContents outputContents, DigesterStateData stateData) {
		return new DigesterContainer(screenId, playerInventory, fuelContents, fuelOutContents, inputContents, outputContents, stateData);
	}

	public static DigesterContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInvContents fuelContents = SimpleInvContents.createClientContents(DigesterTileEntity.FUEL_SLOTS_COUNT);
		SimpleInvContents fuelOutContents = SimpleInvContents.createClientContents(DigesterTileEntity.FUEL_OUT_SLOTS_COUNT);
		SimpleInvContents inputContents = SimpleInvContents.createClientContents(DigesterTileEntity.INPUT_SLOTS_COUNT);
		SimpleInvContents outputContents = SimpleInvContents.createClientContents(DigesterTileEntity.OUTPUT_SLOTS_COUNT);
		DigesterStateData stateData = new DigesterStateData();
		return new DigesterContainer(screenId, playerInventory, fuelContents, fuelOutContents, inputContents, outputContents, stateData);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputContents.isUsableByPlayer(playerIn);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return MathHelper.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public int getFuelAmount() {
		return stateData.waterTank.getFluidAmount();
	}

	public float getFuelNormalized() {
		return MathHelper.clamp(stateData.waterTank.getFluidAmount() / (float) stateData.waterTank.getCapacity(), 0f, 1f);
	}

	public String getFuelTranslationKey() {
//		return stateData.fuel.getFluid().getTranslationKey();
		return "fluid.minecraft.water";
	}

	public FluidTank getOutputTank() {
		return stateData.outputTank;
	}

	/**
	 * copied from: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/ContainerFurnace.java
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = inventorySlots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case OUTPUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, true);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, true);
				if (successfulTransfer) sourceSlot.onSlotChange(sourceStack, copyOfSourceStack);
				break;

			case INPUT_ZONE:
			case FUEL_ZONE:
			case FUEL_OUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (DigesterTileEntity.RECIPE_TYPE.getRecipeForItem(world, sourceStack).isPresent()) {
					successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceStack, false);
				}
				if (!successfulTransfer && DigesterTileEntity.VALID_FUEL_ITEM.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceStack, true);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.putStack(ItemStack.EMPTY);
		else sourceSlot.onSlotChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker(getClass().getSimpleName()), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeInto(SlotZone destinationZone, ItemStack sourceStack, boolean fillFromEnd) {
		return mergeItemStack(sourceStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
	}

	private enum SlotZone {
		//FIXME: brittle system, dependant on correct order of added slots
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(9, 3 * 9),
		FUEL_ZONE(9 + 3 * 9, DigesterTileEntity.FUEL_SLOTS_COUNT),
		INPUT_ZONE(9 + 3 * 9 + DigesterTileEntity.FUEL_SLOTS_COUNT, DigesterTileEntity.INPUT_SLOTS_COUNT),
		OUTPUT_ZONE(9 + 3 * 9 + DigesterTileEntity.FUEL_SLOTS_COUNT + DigesterTileEntity.INPUT_SLOTS_COUNT, DigesterTileEntity.OUTPUT_SLOTS_COUNT),
		FUEL_OUT_ZONE(9 + 3 * 9 + DigesterTileEntity.FUEL_SLOTS_COUNT + DigesterTileEntity.INPUT_SLOTS_COUNT + DigesterTileEntity.OUTPUT_SLOTS_COUNT, DigesterTileEntity.FUEL_OUT_SLOTS_COUNT);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			this.slotCount = numberOfSlots;
			this.lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}
	}
}
