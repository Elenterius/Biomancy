package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ItemStackFilter implements Predicate<ItemStack>, INBTSerializable<CompoundTag> {

	public static final String FILTER_KEY = "Filter";
	public static final String STRICT_KEY = "Strict";

	public static final ItemStackFilter ALLOW_ANY = new ItemStackFilter(null, false);
	public static final ItemStackFilter ALLOW_NONE = new ItemStackFilter(ItemStack.EMPTY, false);

	@Nullable
	private ItemStack filter;
	private boolean isStrict;

	protected ItemStackFilter(@Nullable ItemStack filter, boolean isStrict) {
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
		if (filter.isEmpty()) return ALLOW_NONE;

		filter = filter.copyWithCount(1);

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

		if (filter == null) return true;
		if (filter.isEmpty()) return false;

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
		if (filter != null) {
			tag.put(FILTER_KEY, filter.serializeNBT());
			tag.putBoolean(STRICT_KEY, isStrict);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		filter = tag.contains(FILTER_KEY) ? ItemStack.of(tag.getCompound(FILTER_KEY)) : null;
		isStrict = tag.getBoolean(STRICT_KEY);
	}

	public boolean allowsAny() {
		return filter == null;
	}

}
