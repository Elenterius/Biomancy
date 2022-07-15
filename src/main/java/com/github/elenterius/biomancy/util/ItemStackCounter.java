package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.util.ItemStackCounter.HashKey.EntryKey;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.Objects;

public class ItemStackCounter {

	protected sealed interface HashKey {
		ItemStack stack();

		int hash();

		static HashKey forQuery(ItemStack stack) {
			return new DummyKey(stack);
		}

		static HashKey forModification(ItemStack stack) {
			return new EntryKey(stack);
		}

		record DummyKey(int hash) implements HashKey {

			DummyKey(ItemStack stack) {
				this(Objects.hash(stack.getItem().getRegistryName(), stack.getTag()));
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) return true;
				if (obj instanceof HashKey that) return hash == that.hash();
				return false;
			}

			@Override
			public ItemStack stack() {
				return ItemStack.EMPTY;
			}

			@Override
			public int hashCode() {
				return hash;
			}
		}

		/**
		 * @param stack copy of ItemStack
		 */
		record EntryKey(int hash, ItemStack stack) implements HashKey {

			public EntryKey(int hash, ItemStack stack) {
				this.hash = hash;
				this.stack = copyWithMinCount(stack);
			}

			EntryKey(ItemStack stack) {
				this(Objects.hash(stack.getItem().getRegistryName(), stack.getTag()), stack);
			}

			private static ItemStack copyWithMinCount(ItemStack stack) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				return copy;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) return true;
				if (obj instanceof HashKey that) return hash == that.hash();
				return false;
			}

			@Override
			public int hashCode() {
				return hash;
			}
		}
	}

	private final Object2IntMap<HashKey> countedItemStacks = new Object2IntOpenHashMap<>();

	public record CountedItem(ItemStack stack, int amount) {}

	public List<CountedItem> getItemCountSorted(int limit, boolean ascending) {
		IntComparator comparator = ascending ? IntComparators.NATURAL_COMPARATOR : IntComparators.OPPOSITE_COMPARATOR;
		return countedItemStacks.object2IntEntrySet().stream()
				.sorted((a, b) -> comparator.compare(a.getIntValue(), b.getIntValue()))
				.limit(limit)
				.map(entry -> new CountedItem(entry.getKey().stack(), entry.getIntValue()))
				.toList();
	}

	public void accountSimpleStack(ItemStack stack) {
		if (!stack.isDamaged() && !stack.isEnchanted() && !stack.hasCustomHoverName()) accountStack(stack);
	}

	public void accountStack(ItemStack stack) {
		accountStack(stack, stack.getCount());
	}

	public void accountStacks(Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			accountStack(container.getItem(i));
		}
	}

	public void accountStacks(IItemHandler handler) {
		for (int i = 0; i < handler.getSlots(); i++) {
			accountStack(handler.getStackInSlot(i));
		}
	}

	public void accountStack(ItemStack stack, int amount) {
		if (stack.isEmpty()) return;
		put(stack, amount);
	}

	private void put(ItemStack template, int amount) {
		HashKey key = HashKey.forQuery(template);
		if (countedItemStacks.containsKey(key)) {
			countedItemStacks.mergeInt(key, amount, Integer::sum);
		} else {
			EntryKey entryKey = new EntryKey(key.hash(), template);
			countedItemStacks.put(entryKey, amount);
		}
	}

	public boolean has(ItemStack template) {
		return countedItemStacks.getInt(HashKey.forQuery(template)) > 0;
	}

	public int getCount(ItemStack template) {
		return countedItemStacks.getInt(HashKey.forQuery(template));
	}

	public void clear() {
		countedItemStacks.clear();
	}

}
