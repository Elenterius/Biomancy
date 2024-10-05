package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ItemStackFilterList extends AbstractList<ItemStackFilter> implements INBTSerializable<CompoundTag> {

	private List<ItemStackFilter> filters;

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

	public void setAllFilters(List<ItemStackFilter> filters) {
		this.filters = filters;
	}

	public void setAllFilters(ItemStackFilter filter) {
		filters = IntStream.range(0, filters.size()).mapToObj(x -> filter).toList();
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
	public void deserializeNBT(CompoundTag tag) {
		ListTag listTag = tag.getList("filters", Tag.TAG_COMPOUND);

		List<ItemStackFilter> newFilters = new ArrayList<>();
		for (int i = 0; i < listTag.size(); i++) {
			newFilters.add(ItemStackFilter.of(listTag.getCompound(i)));
		}

		filters = List.copyOf(newFilters);
	}

	@Override
	public CompoundTag serializeNBT() {

		ListTag listTag = new ListTag();
		for (ItemStackFilter filter : filters) {
			listTag.add(filter.serializeNBT());
		}

		CompoundTag tag = new CompoundTag();
		tag.put("filters", listTag);

		return tag;
	}

}
