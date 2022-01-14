package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.world.block.entity.GulgeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.MarkerManager;

public class GulgeMenu extends PlayerContainerMenu {

	private static final int LAST_PLAYER_SLOT_INDEX = 9 + 3 * 9;
	protected final GulgeInventory gulgeInventory;
	protected final SimpleContainer redirectedInput = new SimpleContainer(1);
	protected final SimpleContainer redirectedOutput = new SimpleContainer(1);

	private GulgeMenu(int containerId, Inventory playerInventory, GulgeInventory gulgeInventory) {
		super(ModMenuTypes.GULGE.get(), containerId, playerInventory);
		this.gulgeInventory = gulgeInventory;

		// track item count and sync to client.
		// We do this because ItemStack size is serialized using a signed byte and thus can't store values larger than 127
		addDataSlots(gulgeInventory.getBigItemData());

		//we use input/output slots to manipulate the inv contents in order to avoid the headache of syncing ItemStacks with an item count larger than Byte.MAX_VALUE
		addSlot(new Slot(redirectedInput, 0, 80 - 18, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return gulgeInventory.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, gulgeInventory.getItem(0));
			}

			@Override
			public void set(ItemStack stack) {
				if (gulgeInventory.getOptionalItemHandler().isPresent()) {
					gulgeInventory.getOptionalItemHandler().ifPresent(itemHandler -> {
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
			public void onTake(Player playerIn, ItemStack stack) {
				//server & client side

				if (gulgeInventory.getOptionalItemHandler().isPresent()) {
					gulgeInventory.getOptionalItemHandler().ifPresent(itemHandler -> {
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
			}
		});
	}

	public static GulgeMenu createServerMenu(int containerId, Inventory playerInventory, GulgeInventory inventory) {
		return new GulgeMenu(containerId, playerInventory, inventory);
	}

	public static GulgeMenu createClientMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
		GulgeInventory inventory = GulgeInventory.createClientContents(GulgeBlockEntity.MAX_ITEM_AMOUNT);
		return new GulgeMenu(containerId, playerInventory, inventory);
	}

	@Override
	public boolean stillValid(Player player) {
		return gulgeInventory.stillValid(player);
	}

	public int getStoredItemCount() {
		return gulgeInventory.getBigItemData().getItemCount();
	}

	public int getMaxItemCount() {
		return gulgeInventory.getMaxStackSize();
	}

	public ItemStack getStoredItemStack() {
		return redirectedOutput.getItem(0); //reflects the stored ItemStack but clamped to a count size of 0 or 1
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
	public void broadcastFullState() {
		updateOutputSlot();
		super.broadcastFullState();
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level.isClientSide()) {
			clearContainer(player, redirectedInput);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack stackCopy = stack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (index < LAST_PLAYER_SLOT_INDEX) {  //vanilla container
			if (!moveItemStackTo(stack, LAST_PLAYER_SLOT_INDEX, LAST_PLAYER_SLOT_INDEX + 1, false)) return ItemStack.EMPTY;
		}
		else if (index == 37 || index == 36) { //virtual input & output slot
			//skip input slot
			if (!moveItemStackTo(stack, 0, LAST_PLAYER_SLOT_INDEX, false)) return ItemStack.EMPTY;
		}
		else {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("GulgeMenu"), "Invalid slotIndex: {}", index);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (stack.getCount() == 0) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		slot.onTake(player, stack);
		return stackCopy;
	}

}
