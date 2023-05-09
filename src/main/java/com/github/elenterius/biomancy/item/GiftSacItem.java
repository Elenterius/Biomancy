package com.github.elenterius.biomancy.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class GiftSacItem extends SimpleItem {

	protected static final String ITEMS_KEY = "Items";

	public GiftSacItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (dropItems(stack.getOrCreateTag(), player)) {
			player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + level.getRandom().nextFloat() * 0.4f);
		}

		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	private static boolean dropItems(CompoundTag tag, Player player) {
		if (!tag.contains(ITEMS_KEY)) return false;

		if (player instanceof ServerPlayer) {
			ListTag list = tag.getList(ITEMS_KEY, Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				ItemStack stack = ItemStack.of(list.getCompound(i));
				if (!stack.isEmpty() && !player.addItem(stack)) {
					player.drop(stack, true);
				}
			}
		}

		return true;
	}

	public static ItemStack createFromItems(Item giftBag, List<ItemStack> items) {
		ItemStack stack = new ItemStack(giftBag);
		if (items.isEmpty()) return stack;

		CompoundTag tag = stack.getOrCreateTag();
		ListTag list = new ListTag();

		for (ItemStack item : items) {
			CompoundTag itemTag = new CompoundTag();
			item.save(itemTag);
			list.add(itemTag);
		}

		tag.put(ITEMS_KEY, list);
		return stack;
	}

	public static ItemStack createFromItemTags(Item giftBag, List<CompoundTag> items) {
		ItemStack stack = new ItemStack(giftBag);

		if (items.isEmpty()) return stack;

		CompoundTag tag = stack.getOrCreateTag();
		ListTag list = new ListTag();
		list.addAll(items);

		tag.put(ITEMS_KEY, list);
		return stack;
	}

	public static ItemStack createFromListTag(Item giftBag, ListTag contents) {
		ItemStack stack = new ItemStack(giftBag);

		if (contents.isEmpty()) return stack;
		if (contents.getElementType() != Tag.TAG_COMPOUND) return stack;

		CompoundTag tag = stack.getOrCreateTag();
		tag.put(ITEMS_KEY, contents.copy());
		return stack;
	}

}
