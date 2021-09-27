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
	protected final GulgeInventory gulgeInventory;
	protected final IInventory redirectedInput = new Inventory(1);
	protected final IInventory redirectedOutput = new Inventory(1);

	private GulgeContainer(int screenId, PlayerInventory playerInventory, GulgeInventory gulgeInventory) {
		super(ModContainerTypes.GULGE.get(), screenId);
		this.gulgeInventory = gulgeInventory;

		// track item count and sync to client.
		// We do this because ItemStack size is serialized using a signed byte and thus can't store values larger than 127
		addDataSlots(gulgeInventory);

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

		//we use input/output slots to manipulate the inv contents in order to avoid the headache of syncing ItemStacks with an item count larger than Byte.MAX_VALUE
		addSlot(new Slot(redirectedInput, 0, 80 - 18, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return gulgeInventory.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, gulgeInventory.getItem(0));
			}

			@Override
			public void set(ItemStack stack) {
				if (gulgeInventory.getOptionalItemStackHandler().isPresent()) {
					gulgeInventory.getOptionalItemStackHandler().ifPresent(itemHandler -> {
						ItemStack remainder = itemHandler.insertItem(0, stack, false);  //redirect stack into gulge inventory
						container.setItem(0, remainder);
					});
					gulgeInventory.setChanged();
					updateOutputSlot();
				}
				else {
					container.setItem(0, stack);
				}
				setChanged();
			}
		});
		addSlot(new Slot(redirectedOutput, 0, 80 + 18, 35) {

			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; //prevent insertion
			}

			@Override
			public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
				//server & client side

				if (gulgeInventory.getOptionalItemStackHandler().isPresent()) {
					gulgeInventory.getOptionalItemStackHandler().ifPresent(itemHandler -> {
						ItemStack gulgeStack = itemHandler.getStackInSlot(0);
						if (!gulgeStack.isEmpty()) { // check if there still is something left to take
							itemHandler.extractItem(0, 1, false); //actually extract the item

							gulgeStack = itemHandler.getStackInSlot(0); // get updated stack (stack might be EMPTY now)
							updateOutputSlot(gulgeStack);
						}
					});
					gulgeInventory.setChanged();
				}

				setChanged();
				return stack;
			}
		});
	}

	public static GulgeContainer createServerContainer(int screenId, PlayerInventory playerInventory, GulgeInventory contents) {
		return new GulgeContainer(screenId, playerInventory, contents);
	}

	public static GulgeContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		GulgeInventory contents = GulgeInventory.createClientContents(GulgeTileEntity.MAX_ITEM_AMOUNT);
		return new GulgeContainer(screenId, playerInventory, contents);
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return gulgeInventory.stillValid(playerIn);
	}

	private void updateOutputSlot() {
		updateOutputSlot(gulgeInventory.getItem(0));
	}

	private void updateOutputSlot(ItemStack storedStack) {
		//prime cheese ;)
		//this avoids the need of syncing "Big-ItemStacks" (item count is larger than Byte.MAX_VALUE) to the client
		//instead we only sync an ItemStack copy with an item count of 1

		ItemStack outputStack = redirectedOutput.getItem(0);
		if (storedStack.isEmpty()) {
			if (!outputStack.isEmpty()) redirectedOutput.setItem(0, ItemStack.EMPTY);
		}
		else if (outputStack.isEmpty()) {
			redirectedOutput.setItem(0, ItemHandlerHelper.copyStackWithSize(storedStack, 1));
		}
	}

	@Override
	public void broadcastChanges() {
		updateOutputSlot();
		super.broadcastChanges();
	}

	@Override
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
		if (!playerIn.level.isClientSide()) {
			clearContainer(playerIn, playerIn.level, redirectedInput);
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = slots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex < INPUT_SLOT_INDEX) {  //vanilla container
			if (!moveItemStackTo(sourceStack, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
				return ItemStack.EMPTY;
			}
		}
		else if (sourceSlotIndex == 37 || sourceSlotIndex == 36) { //virtual input & output slot
			if (!moveItemStackTo(sourceStack, 0, INPUT_SLOT_INDEX, false)) { //skip input slot
				return ItemStack.EMPTY;
			}
		}
		else {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("GULGE_CONTAINER"), "Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		}
		else {
			sourceSlot.setChanged();
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	public int getStoredItemCount() {
		return gulgeInventory.get(0);
	}

	public int getMaxItemCount() {
		return gulgeInventory.getMaxStackSize();
	}

	public ItemStack getStoredItemStack() {
		return redirectedOutput.getItem(0); //reflects the stored ItemStack but clamped to a count size of 0 or 1
	}
}
