package com.github.elenterius.biomancy.handler.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.capabilities.InventoryProviders;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.inventory.itemhandler.SingleItemStackHandler;
import com.github.elenterius.biomancy.item.ItemStorageBagItem;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventoryContainerHandler {

	private InventoryContainerHandler() {}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMouseReleased(final GuiScreenEvent.MouseReleasedEvent.Pre event) {
		if (!event.isCanceled() && event.getGui() instanceof ContainerScreen<?>) {
			if (event.getButton() == 0 && consumeLeftMouseBtnReleasedEvent((ContainerScreen<?>) event.getGui())) {
				event.setCanceled(true);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean consumeLeftMouseBtnReleasedEvent(ContainerScreen<?> screen) {
		Slot slot = screen.getSlotUnderMouse();
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (!Screen.hasShiftDown() && player != null && slot != null && !(slot instanceof CraftingResultSlot)) {
			if (Screen.hasControlDown()) {
				return sendExtractActionToServer(screen, slot, player);
			}
			return sendInsertActionToServer(screen, slot, player);
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean sendExtractActionToServer(ContainerScreen<?> screen, Slot slot, ClientPlayerEntity player) {
		ItemStack carriedStack = player.inventory.getCarried();
		if (carriedStack.getItem() instanceof ItemStorageBagItem) {
			if (slot.isActive() && slot.mayPickup(player) && slot.mayPlace(carriedStack) && slot.getItem().getCount() < slot.getMaxStackSize()) {
				int slotIndex = getSlotIndex(slot, player, screen);
				if (slotIndex > -1) {
					LargeSingleItemStackHandler itemHandler = InventoryProviders.LargeSingleItemHandlerProvider.getItemHandler(carriedStack);
					if (!itemHandler.isEmpty()) {
						ItemStack slotStack = slot.getItem();
						if (slotStack.isEmpty() || ItemHandlerHelper.canItemStacksStack(itemHandler.getStack(), slotStack)) {
							ModNetworkHandler.sendCarriedItemToServer(screen, player, carriedStack, slotIndex, Flags.EXTRACT_FROM_BAG.getValue());
							return true;
						}
					}
				}
			}
		}
		else if (carriedStack.isEmpty()) {
			if (slot.getItem().getItem() instanceof ItemStorageBagItem && slot.isActive() && slot.mayPickup(player) && slot.mayPlace(carriedStack)) {
				int slotIndex = getSlotIndex(slot, player, screen);
				if (slotIndex > -1) {
					LargeSingleItemStackHandler itemHandler = InventoryProviders.LargeSingleItemHandlerProvider.getItemHandler(carriedStack);
					if (!itemHandler.isEmpty()) {
						ModNetworkHandler.sendCarriedItemToServer(screen, player, carriedStack, slotIndex, Flags.EXTRACT_FROM_BAG.getValue());
						return true;
					}
				}
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean sendInsertActionToServer(ContainerScreen<?> screen, Slot slot, ClientPlayerEntity player) {
		ItemStack carriedStack = player.inventory.getCarried();
		if (!carriedStack.isEmpty() && slot.hasItem() && slot.isActive() && slot.mayPickup(player) && slot.mayPlace(carriedStack)) {
			int slotIndex = getSlotIndex(slot, player, screen);
			if (slotIndex > -1) {
				if (carriedStack.getItem() instanceof ItemStorageBagItem) {
					LargeSingleItemStackHandler itemHandler = InventoryProviders.LargeSingleItemHandlerProvider.getItemHandler(carriedStack);
					if (canInsertIntoItemHandler(itemHandler, slot.getItem())) {
						ModNetworkHandler.sendCarriedItemToServer(screen, player, carriedStack, slotIndex, Flags.INSERT_INTO_BAG.getValue());
						return true; //cancel mouse click event
					}
				}
//				else {
//					//if slot has ItemStorageBag, store carriedStack in ItemStorageBag
//					ItemStack slotStack = slot.getItem();
//					if (slotStack.getItem() instanceof ItemStorageBagItem) {
//						LargeSingleItemStackHandler itemHandler = ItemStorageBagItem.getItemHandler(slotStack);
//						if (itemHandler != null && canInsertIntoItemHandler(itemHandler, carriedStack)) {
//							ModNetworkHandler.sendCarriedItemToServer(screen, player, carriedStack, slotIndex, Flags.INSERT_INTO_BAG.getValue());
//							return true; //but we still cancel the left mouse release for everybody else to prevent normal slot interaction behavior
//						}
//					}
//				}
			}
		}

		return false;
	}

	private static boolean canInsertIntoItemHandler(SingleItemStackHandler itemHandler, ItemStack stackIn) {
		return itemHandler.getAmount() < itemHandler.getMaxAmount() && HandlerBehaviors.EMPTY_ITEM_INVENTORY_PREDICATE.test(stackIn)
				&& (itemHandler.getStack().isEmpty() || ItemHandlerHelper.canItemStacksStack(stackIn, itemHandler.getStack()));
	}

	@OnlyIn(Dist.CLIENT)
	private static int getSlotIndex(Slot slot, PlayerEntity player, ContainerScreen<?> screen) {
		if (player.isCreative() && screen instanceof CreativeScreen) {
			if (((CreativeScreen) screen).getSelectedTab() == ItemGroup.TAB_INVENTORY.getId()) return slot.getSlotIndex();
			return slot.index > 44 ? slot.getSlotIndex() : -1; //don't interact with Creative LockedSlots (indices 0 to 44)
		}
		return slot.index;
	}

	public static void onServerReceiveSlotInteraction(Container container, ServerPlayerEntity player, ItemStack carriedStack, int slotIndex, int flags) {
		if (Flags.INSERT_INTO_BAG.isSet(flags)) {
			handleInsertIntoBag(container, player, carriedStack, slotIndex);
		}
		else if (Flags.EXTRACT_FROM_BAG.isSet(flags)) {
			handleExtractFromBag(container, player, carriedStack, slotIndex);
		}
	}

	private static void handleExtractFromBag(Container container, ServerPlayerEntity player, ItemStack carriedStack, int slotIndex) {
		if (carriedStack.getItem() instanceof ItemStorageBagItem) {
			ItemStorageBagItem itemBag = (ItemStorageBagItem) carriedStack.getItem();
			if (player.isCreative() && container instanceof PlayerContainer) {
				ItemStack slotStack = player.inventory.items.get(slotIndex);
				ItemStack resultStack = itemBag.extractItemStack(carriedStack, slotStack);
				player.inventory.items.set(slotIndex, resultStack);
				player.inventory.setChanged();
			}
			else {
				Slot slot = container.getSlot(slotIndex);
				ItemStack resultStack = itemBag.extractItemStack(carriedStack, slot.getItem());
				slot.set(resultStack);
				player.inventory.setCarried(carriedStack);
			}

			ModNetworkHandler.sendCarriedItemToClient(player, carriedStack, Flags.EXTRACT_FROM_BAG.getValue());
			player.refreshContainer(container); //updates carried item
			player.inventoryMenu.broadcastChanges();
		}
		else if (carriedStack.isEmpty()) {
			if (player.isCreative() && container instanceof PlayerContainer) {
				ItemStack slotStack = player.inventory.items.get(slotIndex);
				if (slotStack.getItem() instanceof ItemStorageBagItem) {
					ItemStorageBagItem itemBag = (ItemStorageBagItem) slotStack.getItem();
					carriedStack = itemBag.extractItemStack(slotStack, carriedStack);
					player.inventory.setChanged();

					ModNetworkHandler.sendCarriedItemToClient(player, carriedStack, Flags.EXTRACT_FROM_BAG.getValue());
					player.refreshContainer(container); //updates carried item
					player.inventoryMenu.broadcastChanges();
				}
			}
			else {
				Slot slot = container.getSlot(slotIndex);
				ItemStack slotStack = slot.getItem();
				if (slotStack.getItem() instanceof ItemStorageBagItem) {
					ItemStorageBagItem itemBag = (ItemStorageBagItem) slotStack.getItem();
					ItemStack resultStack = itemBag.extractItemStack(slotStack, carriedStack);
					player.inventory.setCarried(resultStack);
					slot.setChanged();

					ModNetworkHandler.sendCarriedItemToClient(player, resultStack, Flags.EXTRACT_FROM_BAG.getValue());
					player.refreshContainer(container); //updates carried item
					player.inventoryMenu.broadcastChanges();
				}
			}
		}
	}

	private static void handleInsertIntoBag(Container container, ServerPlayerEntity player, ItemStack carriedStack, int slotIndex) {
		if (carriedStack.getItem() instanceof ItemStorageBagItem) {
			ItemStorageBagItem itemBag = (ItemStorageBagItem) carriedStack.getItem();
			if (player.isCreative() && container instanceof PlayerContainer) {
				ItemStack slotStack = player.inventory.items.get(slotIndex);
				itemBag.storeItemStack(carriedStack, slotStack); //modifies slotStack
				player.inventory.setChanged();
			}
			else {
				Slot slot = container.getSlot(slotIndex);
				itemBag.storeItemStack(carriedStack, slot.getItem()); //modifies slotStack
				player.inventory.setCarried(carriedStack);
				slot.setChanged();
			}

			ModNetworkHandler.sendCarriedItemToClient(player, carriedStack, Flags.INSERT_INTO_BAG.getValue());
			player.refreshContainer(container); //updates carried item
			player.inventoryMenu.broadcastChanges();
		}
		else if (!carriedStack.isEmpty()) {
			if (player.isCreative() && container instanceof PlayerContainer) {
				ItemStack slotStack = player.inventory.items.get(slotIndex);
				if (slotStack.getItem() instanceof ItemStorageBagItem) {
					ItemStorageBagItem itemBag = (ItemStorageBagItem) slotStack.getItem();
					itemBag.storeItemStack(slotStack, carriedStack); //modifies carriedStack
					player.inventory.setChanged();

					ModNetworkHandler.sendCarriedItemToClient(player, carriedStack, Flags.INSERT_INTO_BAG.getValue());
					player.refreshContainer(container); //updates carried item
					player.inventoryMenu.broadcastChanges();
				}
			}
			else {
				Slot slot = container.getSlot(slotIndex);
				ItemStack slotStack = slot.getItem();
				if (slotStack.getItem() instanceof ItemStorageBagItem) {
					ItemStorageBagItem itemBag = (ItemStorageBagItem) slotStack.getItem();
					itemBag.storeItemStack(slotStack, carriedStack); //modifies carriedStack
					player.inventory.setCarried(carriedStack);
					slot.setChanged();

					ModNetworkHandler.sendCarriedItemToClient(player, carriedStack, Flags.INSERT_INTO_BAG.getValue());
					player.refreshContainer(container); //updates carried item
					player.inventoryMenu.broadcastChanges();
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void onClientReceiveSlotInteraction(ClientPlayerEntity player, ItemStack carriedStack, int flags) {
		if (Flags.INSERT_INTO_BAG.isSet(flags)) ItemStorageBagItem.playInsertSound(player);
		else if (Flags.EXTRACT_FROM_BAG.isSet(flags)) ItemStorageBagItem.playExtractSound(player);

		if (player.isCreative()) {
			player.inventory.setCarried(carriedStack);
		}
	}

	public enum Flags {
		NONE,
		INSERT_INTO_BAG,
		EXTRACT_FROM_BAG;

		static {
			//is valid? --> 2^8 - 1 = 255
			if (Flags.values().length > 8) throw new RuntimeException("max flags value is larger than max value of unsigned byte");
		}

		private final int bitPosition = 1 << ordinal();

		public static int getMaxValue() {
			return (int) Math.pow(2, Flags.values().length) - 1;
		}

		public boolean isSet(int value) {
			return (value & bitPosition) != 0;
		}

		public int set(int value) {
			return value | bitPosition;
		}

		public int getValue() {
			return bitPosition;
		}

	}

}
