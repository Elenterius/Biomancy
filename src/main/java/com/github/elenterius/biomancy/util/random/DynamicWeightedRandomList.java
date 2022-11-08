package com.github.elenterius.biomancy.util.random;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DynamicWeightedRandomList<T extends DynamicWeightedRandomList.IWeightedEntry<?>> {

	private final LinkedList<T> items;
	private int totalWeight = 0;

	protected DynamicWeightedRandomList(List<? extends T> items) {
		this.items = new LinkedList<>(items);
		calcTotalWeight();
	}

	protected DynamicWeightedRandomList() {
		this.items = new LinkedList<>();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	protected void addEntry(T entry) {
		items.add(entry);
		calcTotalWeight();
	}

	protected void removeEntry(T entry) {
		items.remove(entry);
		calcTotalWeight();
	}

	protected Optional<T> getRandom(Random random) {
		if (totalWeight == 0) return Optional.empty();

		return getWeightedItem(random.nextInt(totalWeight));
	}

	protected Optional<T> getWeightedItem(int weightedIndex) {
		for (T entry : items) {
			weightedIndex -= entry.weight();
			if (weightedIndex < 0) {
				if (entry.shouldRemove()) {
					removeEntry(entry);
				}
				return Optional.of(entry);
			}
		}

		return Optional.empty();
	}

	protected void calcTotalWeight() {
		long sum = 0;
		for (T entry : items) {
			sum += entry.weight();
		}

		if (sum > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Sum of weights must be <= " + Integer.MAX_VALUE);
		}
		totalWeight = (int) sum;
	}

	public sealed interface IWeightedEntry<E> {
		E data();

		int weight();

		/**
		 * should the entry be removed after being drawn
		 */
		default boolean shouldRemove() {
			return false;
		}

		record Default<E>(E data, int weight) implements IWeightedEntry<E> {}

		/**
		 * this entry will be removed after being drawn
		 */
		record SelfRemoving<E>(E data, int weight) implements IWeightedEntry<E> {
			@Override
			public boolean shouldRemove() {
				return true;
			}
		}
	}

}
