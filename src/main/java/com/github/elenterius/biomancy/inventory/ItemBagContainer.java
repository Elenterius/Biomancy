package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.item.ItemStorageBagItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.MarkerManager;

public class ItemBagContainer extends ContainerWithPlayerInv {

	private final World world;
	protected final SimpleInventory bagInventory;
	protected final IInventory redirectedInput = new Inventory(1);
	protected final IInventory redirectedOutput = new Inventory(1);

	private final ItemStack heldStack;

	private ItemBagContainer(int id, PlayerInventory playerInventory, SimpleInventory inventoryIn, ItemStack heldStackIn) {
		super(ModContainerTypes.ITEM_BAG.get(), id, playerInventory);
		world = playerInventory.player.level;
		bagInventory = inventoryIn;
		heldStack = heldStackIn;

		addSlot(new Slot(bagInventory, 0, 80 + 18 + 18, 35 - 18));

		//we use input/output slots to manipulate the inv contents in order to avoid the headache of syncing ItemStacks with an item count larger than Byte.MAX_VALUE
		addSlot(new Slot(redirectedInput, 0, 80 - 18, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return bagInventory.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, bagInventory.getItem(0));
			}

			@Override
			public void set(ItemStack stack) {
				IItemHandler itemHandler = bagInventory.getItemHandler();
				ItemStack remainder = itemHandler.insertItem(0, stack, false);  //redirect stack into gulge inventory
				container.setItem(0, remainder);
				updateOutputSlot();
				setChanged();
				bagInventory.setChanged();
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
				if (!playerIn.level.isClientSide) {
					IItemHandler itemHandler = bagInventory.getItemHandler();
					ItemStack storedStack = itemHandler.getStackInSlot(0);
					if (!storedStack.isEmpty()) { // check if there still is something left to take
						itemHandler.extractItem(0, 1, false); //actually extract the item
						storedStack = itemHandler.getStackInSlot(0); // get updated stack (stack might be EMPTY now)
						updateOutputSlot(storedStack);
					}
					bagInventory.setChanged();
				}
				setChanged();
				return stack;
			}
		});
	}

	public static ItemBagContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInventory inventory, ItemStack itemBagStack) {
		return new ItemBagContainer(screenId, playerInventory, inventory, itemBagStack);
	}

	public static ItemBagContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		ItemStack stack = playerInventory.getSelected();
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.hasUUID(ItemStorageBagItem.UUID_NBT_KEY) && nbt.getUUID(ItemStorageBagItem.UUID_NBT_KEY).equals(extraData.readUUID())) {
			LargeSingleItemStackHandler itemHandler = ItemStorageBagItem.getItemHandler(stack);
			if (itemHandler != null) {
				System.out.println("success!!");
				return new ItemBagContainer(screenId, playerInventory, SimpleInventory.createClientContents(itemHandler), ItemStack.EMPTY);
			}
		}
		return new ItemBagContainer(screenId, playerInventory, SimpleInventory.createClientContents(new LargeSingleItemStackHandler(ItemStorageBagItem.SLOT_SIZE)), ItemStack.EMPTY);
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		if (bagInventory.stillValid(playerIn)) {
			ItemStack stackMain = playerIn.getMainHandItem();
			if (!stackMain.isEmpty() && stackMain == heldStack) return true;
			ItemStack stackOff = playerIn.getOffhandItem();
			return !stackOff.isEmpty() && stackOff == heldStack;
		}
		return false;
	}

	private void updateOutputSlot() {
		updateOutputSlot(bagInventory.getItem(0));
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
		if (!world.isClientSide) {
			updateOutputSlot();
			LargeSingleItemStackHandler itemHandler = (LargeSingleItemStackHandler) bagInventory.getItemHandler();
			if (itemHandler.isDirty()) {
				CompoundNBT nbt = heldStack.getOrCreateTag();
				nbt.putInt("DirtyFlag", nbt.getInt("DirtyFlag") + 1);
				heldStack.setTag(nbt);
			}
		}
		super.broadcastChanges();
	}

	@Override
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
		if (!playerIn.level.isClientSide()) {
			clearContainer(playerIn, playerIn.level, redirectedInput);
		}
	}

	public int getStoredItemCount() {
		LargeSingleItemStackHandler itemHandler = (LargeSingleItemStackHandler) bagInventory.getItemHandler();
		return itemHandler.getAmount();
	}

	public int getMaxItemCount() {
		LargeSingleItemStackHandler itemHandler = (LargeSingleItemStackHandler) bagInventory.getItemHandler();
		return itemHandler.getMaxAmount();
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = slots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case OUTPUT_ZONE:
			case INPUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, true) || mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, true);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (!mergeInto(SlotZone.INVENTORY_ZONE, sourceStack, false)) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
		else sourceSlot.setChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker(getClass().getSimpleName()), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeInto(SlotZone destinationZone, ItemStack sourceStack, boolean fillFromEnd) {
		return moveItemStackTo(sourceStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
	}

	private enum SlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		INVENTORY_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, 1),
		INPUT_ZONE(INVENTORY_ZONE.lastIndexPlus1, 1),
		OUTPUT_ZONE(INPUT_ZONE.lastIndexPlus1, 1);

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
