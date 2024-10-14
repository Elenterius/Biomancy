package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ItemStackFilterList extends AbstractList<ItemStackFilter> implements INBTSerializable<ListTag> {

	private final List<ItemStackFilter> filters;

	protected ItemStackFilterList(List<ItemStackFilter> filters) {
		this.filters = new ArrayList<>(filters);
	}

	public static ItemStackFilterList of(ItemStackFilter filter) {
		return new ItemStackFilterList(List.of(filter));
	}

	public static ItemStackFilterList of(ItemStackFilter filter, int size) {
		return new ItemStackFilterList(IntStream.range(0, size).mapToObj(x -> filter).toList());
	}

	public static ItemStackFilterList of(List<ItemStackFilter> filters) {
		return new ItemStackFilterList(filters);
	}

	public void setAllFilters(ItemStackFilter filter) {
		filters.replaceAll(ignored -> filter);
	}

	public void setFilter(int index, ItemStackFilter filter) {
		filters.set(index, filter);
	}

	@Override
	public ItemStackFilter set(int index, ItemStackFilter filter) {
		return filters.set(index, filter);
	}

	@Override
	public ItemStackFilter get(int index) {
		return filters.get(index);
	}

	@Override
	public int size() {
		return filters.size();
	}

	public boolean test(int index, ItemStack stack) {
		if (index <= 0 || index > size()) return false;
		return filters.get(index).test(stack);
	}

	@Override
	public void deserializeNBT(ListTag listTag) {
		filters.clear();
		for (int i = 0; i < listTag.size(); i++) {
			filters.add(ItemStackFilter.of(listTag.getCompound(i)));
		}
	}

	@Override
	public ListTag serializeNBT() {
		ListTag listTag = new ListTag();
		for (ItemStackFilter filter : filters) {
			listTag.add(filter.serializeNBT());
		}

		return listTag;
	}

}
