package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Predicate;

public class ItemStackFilter implements Predicate<ItemStack>, INBTSerializable<CompoundTag> {

	public static final String FILTER_KEY = "filter";
	public static final String STRICT_KEY = "strict";

	public static final ItemStackFilter ALLOW_ALL = of(ItemStack.EMPTY, false);

	private ItemStack filter;
	private boolean isStrict;

	protected ItemStackFilter(ItemStack filter, boolean isStrict) {
		this.filter = filter;
		this.isStrict = isStrict;
	}

	protected ItemStackFilter(CompoundTag tag) {
		deserializeNBT(tag);
	}

	public static ItemStackFilter of(CompoundTag tag) {
		return new ItemStackFilter(tag);
	}

	public static ItemStackFilter of(Item filter) {
		return of(filter.getDefaultInstance(), false);
	}

	public static ItemStackFilter of(ItemStack filter) {
		return of(filter, true);
	}

	public static ItemStackFilter of(ItemStack filter, boolean isStrict) {
		if (filter.hasTag()) {
			CompoundTag stackTag = filter.getTag();
			assert stackTag != null;
			stackTag.remove("Enchantments");
			stackTag.remove("AttributeModifiers");
		}
		return new ItemStackFilter(filter, isStrict);
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (filter.isEmpty()) return true;

		if (isStrict) {
			return ItemHandlerHelper.canItemStacksStack(filter, stack);
		}
		else {
			return filter.is(stack.getItem());
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.put(FILTER_KEY, filter.serializeNBT());
		tag.putBoolean(STRICT_KEY, isStrict);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		filter = ItemStack.of(tag.getCompound(FILTER_KEY));
		isStrict = tag.getBoolean(STRICT_KEY);
	}

}
