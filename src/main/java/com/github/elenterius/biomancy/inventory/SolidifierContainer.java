package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.inventory.itemhandler.behavior.ItemHandlerBehavior;
import com.github.elenterius.biomancy.tileentity.SolidifierTileEntity;
import com.github.elenterius.biomancy.tileentity.state.SolidifierStateData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class SolidifierContainer extends Container {

	protected final SimpleInvContents filledBucketInventory;
	protected final SimpleInvContents emptyBucketInventory;
	protected final SimpleInvContents outputInventory;
	private final SolidifierStateData stateData;

	private SolidifierContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents filledBucketInventory, SimpleInvContents emptyBucketInventory, SimpleInvContents outputInventory, SolidifierStateData stateData) {
		super(ModContainerTypes.SOLIDIFIER.get(), screenId);
		this.filledBucketInventory = filledBucketInventory;
		this.emptyBucketInventory = emptyBucketInventory;
		this.outputInventory = outputInventory;
		this.stateData = stateData;

		trackIntArray(stateData);

		initPlayerInvSlots(playerInventory);

		addSlot(new Slot(filledBucketInventory, 0, 51, 17) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return ItemHandlerBehavior.FILLED_FLUID_ITEM_PREDICATE.test(stack);
			}
		});
		addSlot(new OutputSlot(emptyBucketInventory, 0, 51, 44));

		addSlot(new OutputSlot(outputInventory, 0, 98, 30));
	}

	private void initPlayerInvSlots(PlayerInventory playerInventory) {
		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int POS_X = 8;
		final int INV_POS_Y = 84;
		final int HOTBAR_POS_Y = 142;
		final int SLOTS_PER_ROW = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < SLOTS_PER_ROW; idx++) {
			addSlot(new SlotItemHandler(playerInventoryForge, idx, POS_X + SLOT_X_SPACING * idx, HOTBAR_POS_Y));
		}

		// Add the players main inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < SLOTS_PER_ROW; x++) {
				int slotNumber = SLOTS_PER_ROW + y * SLOTS_PER_ROW + x;
				int posX = POS_X + x * SLOT_X_SPACING;
				int posY = INV_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}
	}

	public static SolidifierContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents filledBucketInventory, SimpleInvContents emptyBucketInventory, SimpleInvContents outputInventory, SolidifierStateData stateData) {
		return new SolidifierContainer(screenId, playerInventory, filledBucketInventory, emptyBucketInventory, outputInventory, stateData);
	}

	public static SolidifierContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInvContents filledBucketInventory = SimpleInvContents.createClientContents(SolidifierTileEntity.FILLED_BUCKET_SLOTS);
		SimpleInvContents emptyBucketInventory = SimpleInvContents.createClientContents(SolidifierTileEntity.EMPTY_BUCKET_SLOTS);
		SimpleInvContents outputInventory = SimpleInvContents.createClientContents(SolidifierTileEntity.OUTPUT_SLOTS);
		SolidifierStateData stateData = new SolidifierStateData();
		return new SolidifierContainer(screenId, playerInventory, filledBucketInventory, emptyBucketInventory, outputInventory, stateData);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		//we don't check all three inventories because they all call the same method in the tile entity
		return filledBucketInventory.isUsableByPlayer(playerIn);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return MathHelper.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public FluidTank getInputTank() {
		return stateData.inputTank;
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

			case EMPTY_BUCKET_ZONE:
			case FILLED_BUCKET_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (ItemHandlerBehavior.FILLED_FLUID_ITEM_PREDICATE.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.FILLED_BUCKET_ZONE, sourceStack, true);
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
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(9, 27), // 3 * 9
		FILLED_BUCKET_ZONE(9 + 27, SolidifierTileEntity.FILLED_BUCKET_SLOTS),
		EMPTY_BUCKET_ZONE(9 + 27 + SolidifierTileEntity.FILLED_BUCKET_SLOTS, SolidifierTileEntity.EMPTY_BUCKET_SLOTS),
		OUTPUT_ZONE(9 + 27 + SolidifierTileEntity.FILLED_BUCKET_SLOTS + SolidifierTileEntity.EMPTY_BUCKET_SLOTS, SolidifierTileEntity.OUTPUT_SLOTS);

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
