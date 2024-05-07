package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.inventory.ItemStackInventory;
import com.github.elenterius.biomancy.inventory.itemhandler.EnhancedItemHandler;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class StorageSacBlockItem extends BlockItem implements ItemTooltipStyleProvider {

	public StorageSacBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	public static Optional<EnhancedItemHandler> getItemHandler(ItemStack stack) {
		return stack.getCapability(ModCapabilities.ITEM_HANDLER).map(EnhancedItemHandler::new);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new InventoryCapability(stack);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (stack.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY) return false;

		final ItemStack otherStack = slot.getItem();
		if (otherStack.isEmpty()) {
			Optional<EnhancedItemHandler> itemHandler = getItemHandler(stack);
			ItemStack stackFromInv = itemHandler.map(h -> h.extractItemFirstFound(slot.getMaxStackSize(), false)).orElse(ItemStack.EMPTY);
			int insertAmount = stackFromInv.getCount();
			ItemStack remainder = slot.safeInsert(stackFromInv);
			if (remainder.getCount() < insertAmount) playRemoveFromSacSound(player);
			itemHandler.ifPresent(h -> h.insertItemOnExistingFirst(remainder));
		}
		else if (otherStack.getItem().canFitInsideContainerItems() && !(otherStack.getItem() instanceof BundleItem)) {
			final int prevCount = otherStack.getCount();
			ItemStack remainder = getItemHandler(stack).map(h -> h.insertItemOnExistingFirst(slot.safeTake(prevCount, Integer.MAX_VALUE, player))).orElse(ItemStack.EMPTY);
			slot.safeInsert(remainder);
			if (prevCount - remainder.getCount() > 0) {
				playInsertIntoSacSound(player);
			}
		}

		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (stack.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (otherStack.isEmpty()) {
			ItemStack stackFromInv = getItemHandler(stack).map(EnhancedItemHandler::extractItemFirstFound).orElse(ItemStack.EMPTY);
			if (!stackFromInv.isEmpty()) {
				playRemoveFromSacSound(player);
				access.set(stackFromInv);
			}
		}
		else if (otherStack.getItem().canFitInsideContainerItems() && !(otherStack.getItem() instanceof BundleItem)) {
			ItemStack remainder = getItemHandler(stack).map(h -> h.insertItemOnExistingFirst(otherStack)).orElse(ItemStack.EMPTY);
			final int insertedAmount = otherStack.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				playInsertIntoSacSound(player);
				otherStack.shrink(insertedAmount);
			}
		}

		return true;
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		super.onDestroyed(itemEntity);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.tooltip(new StorageSacTooltipComponent(getItemHandler(stack).orElse(null))));
		super.appendHoverText(stack, level, tooltip, flag);
	}

	private void playRemoveFromSacSound(Player player) {
		playSound(player, SoundEvents.BUNDLE_REMOVE_ONE);
	}

	private void playInsertIntoSacSound(Player player) {
		playSound(player, SoundEvents.BUNDLE_INSERT);
	}

	private void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
	}

	private static class InventoryCapability implements ICapabilityProvider {
		public static final ItemStackInventory.InventorySerializer SERIALIZER = ItemStackInventory.InventorySerializer.BLOCK_ENTITY_TAG;
		private final ItemStackInventory itemHandler;

		public InventoryCapability(ItemStack stack) {
			itemHandler = ItemStackInventory.createServerContents(StorageSacBlockEntity.SLOTS, 64, stack, SERIALIZER);
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			return ModCapabilities.ITEM_HANDLER.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}
