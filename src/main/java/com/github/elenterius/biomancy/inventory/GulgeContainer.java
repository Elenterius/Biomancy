package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.tileentity.GulgeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class GulgeContainer extends Container {

	private static final int VANILLA_SLOT_COUNT = 9 + 3 * 9;
	private static final int INPUT_SLOT_INDEX = VANILLA_SLOT_COUNT;
	protected final GulgeContents gulgeContents;
	protected final IInventory redirectedInput = new Inventory(1);
	protected final IInventory redirectedOutput = new Inventory(1);

	private GulgeContainer(int screenId, PlayerInventory playerInventory, GulgeContents gulgeContents) {
		super(ModContainerTypes.GULGE.get(), screenId);
		this.gulgeContents = gulgeContents;

		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int HOT_BAR_SIZE = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players inventory to the gui
		for (int idx = 0; idx < HOT_BAR_SIZE; idx++) { //hotbar
			addSlot(new SlotItemHandler(playerInventoryForge, idx, 8 + SLOT_X_SPACING * idx, 142));
		}

		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 84;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = HOT_BAR_SIZE + y * 9 + x;
				int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, xpos, ypos));
			}
		}

		addSlot(new Slot(redirectedInput, 0, 80 - 18, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return gulgeContents.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, gulgeContents.getStackInSlot(0));
			}

			@Override
			public void putStack(ItemStack stack) {
				if (gulgeContents.getOptionalItemStackHandler().isPresent()) {
					gulgeContents.getOptionalItemStackHandler().ifPresent(itemHandler -> {
						ItemStack remainder = itemHandler.insertItem(0, stack, false);  //redirect stack into gulge inventory
						inventory.setInventorySlotContents(0, remainder);
					});
					gulgeContents.markDirty();
					updateOutputSlot();
				}
				else {
					inventory.setInventorySlotContents(0, stack);
				}
				onSlotChanged();
			}
		});
		addSlot(new Slot(redirectedOutput, 0, 80 + 18, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false; //prevent insertion
			}

			@Override
			public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
				//server & client side

				if (gulgeContents.getOptionalItemStackHandler().isPresent()) {
					gulgeContents.getOptionalItemStackHandler().ifPresent(itemHandler -> {
						ItemStack gulgeStack = itemHandler.getStackInSlot(0);
						if (!gulgeStack.isEmpty()) { // check if there still is something left to take
							itemHandler.extractItem(0, 1, false); //actually extract the item

							gulgeStack = itemHandler.getStackInSlot(0); // get updated stack (stack might be EMPTY now)
							updateOutputSlot(gulgeStack);
						}
					});
					gulgeContents.markDirty();
				}

				onSlotChanged();
				return stack;
			}
		});
		addSlot(new Slot(gulgeContents, 0, 176 - 34, 24 - 12) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

			@Override
			public boolean canTakeStack(PlayerEntity playerIn) {
				return false;
			}
		});
	}

	public static GulgeContainer createServerContainer(int screenId, PlayerInventory playerInventory, GulgeContents contents) {
		return new GulgeContainer(screenId, playerInventory, contents);
	}

	public static GulgeContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		GulgeContents contents = GulgeContents.createClientContents(GulgeTileEntity.MAX_ITEM_AMOUNT);
		return new GulgeContainer(screenId, playerInventory, contents);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return gulgeContents.isUsableByPlayer(playerIn);
	}

	private void updateOutputSlot() {
		updateOutputSlot(gulgeContents.getStackInSlot(0));
	}

	private void updateOutputSlot(ItemStack storedStack) {
		ItemStack outputStack = redirectedOutput.getStackInSlot(0);
		if (storedStack.isEmpty()) {
			if (!outputStack.isEmpty()) redirectedOutput.setInventorySlotContents(0, ItemStack.EMPTY);
		}
		else if (outputStack.isEmpty()) {
			redirectedOutput.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(storedStack, 1));
		}
	}

	@Override
	public void detectAndSendChanges() {
		updateOutputSlot();
		super.detectAndSendChanges();
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		if (!playerIn.world.isRemote()) {
			clearContainer(playerIn, playerIn.world, redirectedInput);
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = inventorySlots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex < INPUT_SLOT_INDEX) {  //vanilla container
			if (!mergeItemStack(sourceStack, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
				return ItemStack.EMPTY;
			}
		}
		else if (sourceSlotIndex == 37 || sourceSlotIndex == 36) { //virtual input & output slot
			if (!mergeItemStack(sourceStack, 0, INPUT_SLOT_INDEX, false)) { //skip input slot
				return ItemStack.EMPTY;
			}
		}
		else {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("GULGE_CONTAINER"), "Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.putStack(ItemStack.EMPTY);
		}
		else {
			sourceSlot.onSlotChanged();
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

}
