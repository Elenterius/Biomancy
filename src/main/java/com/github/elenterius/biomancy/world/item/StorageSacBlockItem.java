package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.world.block.entity.StorageSacBlockEntity;
import com.github.elenterius.biomancy.world.inventory.ItemStackInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.EnhancedItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class StorageSacBlockItem extends BlockItem implements IBiomancyItem {

	public StorageSacBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	public static Optional<EnhancedItemHandler> getItemHandler(ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(EnhancedItemHandler::new);
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

		final ItemStack stackInSlot = slot.getItem();
		if (stackInSlot.isEmpty()) {
			Optional<EnhancedItemHandler> itemHandler = getItemHandler(stack);
			ItemStack stackFromInv = itemHandler.map(h -> h.extractItemFirstFound(slot.getMaxStackSize(), false)).orElse(ItemStack.EMPTY);
			int insertAmount = stackFromInv.getCount();
			ItemStack remainder = slot.safeInsert(stackFromInv);
			if (remainder.getCount() < insertAmount) playRemoveFromSacSound(player);
			itemHandler.ifPresent(h -> h.insertItem(remainder));
		} else if (stackInSlot.getItem().canFitInsideContainerItems()) {
			final int prevCount = stackInSlot.getCount();
			ItemStack remainder = getItemHandler(stack).map(h -> h.insertItem(slot.safeTake(prevCount, Integer.MAX_VALUE, player))).orElse(ItemStack.EMPTY);
			slot.safeInsert(remainder);
			if (prevCount - remainder.getCount() > 0) {
				playInsertIntoSacSound(player);
			}
		}

		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (stack.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (other.isEmpty()) {
			ItemStack stackFromInv = getItemHandler(stack).map(EnhancedItemHandler::extractItemFirstFound).orElse(ItemStack.EMPTY);
			if (!stackFromInv.isEmpty()) {
				playRemoveFromSacSound(player);
				access.set(stackFromInv);
			}
		} else {
			ItemStack remainder = getItemHandler(stack).map(h -> h.insertItem(other)).orElse(ItemStack.EMPTY);
			final int insertedAmount = other.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				playInsertIntoSacSound(player);
				other.shrink(insertedAmount);
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
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		var ref = new Object() {
			int sum = 0;
		};
		getItemHandler(stack).ifPresent(itemHandler -> {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			int count = 0;
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stackInSlot = itemHandler.getStackInSlot(i);
				if (!stackInSlot.isEmpty()) {
					ref.sum++;
					if (count < 5) {
						MutableComponent component = stackInSlot.getHoverName().copy();
						component.append(" x").append(String.valueOf(stackInSlot.getCount())).withStyle(ChatFormatting.GRAY);
						tooltip.add(component);
						count++;
					}
				}
			}

			if (ref.sum - count > 0) {
				tooltip.add((new TranslatableComponent("container.shulkerBox.more", ref.sum - count)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			}
		});

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(new TextComponent(String.format("%d/%d ", ref.sum, StorageSacBlockEntity.SLOTS)).append(new TranslatableComponent("tooltip.biomancy.slots")).withStyle(ChatFormatting.GRAY));
	}

	private void playRemoveFromSacSound(Player player) {
		playSound(player, SoundEvents.BUNDLE_REMOVE_ONE);
	}

	private void playInsertIntoSacSound(Player player) {
		playSound(player, SoundEvents.BUNDLE_INSERT);
	}

	private void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.getLevel().getRandom().nextFloat() * 0.4f);
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
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}