package com.github.elenterius.biomancy.util.random;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

public class DynamicLootTable extends DynamicWeightedRandomList<DynamicWeightedRandomList.IWeightedEntry<DynamicLootTable.ItemLoot>> {

	public static final ToIntBiFunction<RandomSource, Integer> CONSTANT_ITEM_AMOUNT_FUNC = (random, lootingLevel) -> 1;
	public static final ToIntBiFunction<RandomSource, Integer> RANDOM_ITEM_AMOUNT_FUNC_1 = (random, lootingLevel) -> Mth.nextInt(random, 1, 1 + lootingLevel); //maximum is inclusive
	public static final ToIntBiFunction<RandomSource, Integer> RANDOM_ITEM_AMOUNT_FUNC_2 = (random, lootingLevel) -> Mth.nextInt(random, 1, 2 + lootingLevel);

	public DynamicLootTable() {}

	/**
	 * this loot can be drawn multiple times
	 */
	public void add(ItemLoot loot, int weight) {
		super.addEntry(new IWeightedEntry.Default<>(loot, weight));
	}

	/**
	 * this loot can only be drawn once and will be removed from the loot table
	 */
	public void addSelfRemoving(ItemLoot loot, int weight) {
		super.addEntry(new IWeightedEntry.SelfRemoving<>(loot, weight));
	}

	public Optional<ItemLoot> getRandomItem(RandomSource random) {
		return getRandom(random).map(IWeightedEntry::data);
	}

	public Optional<ItemLoot> getAndRemoveRandomItem(RandomSource random) {
		Optional<IWeightedEntry<ItemLoot>> weightedEntry = getRandom(random);
		weightedEntry.ifPresent(this::removeEntry);
		return weightedEntry.map(IWeightedEntry::data);
	}

	public Optional<ItemStack> getRandomItemStack(RandomSource random, int lootingLevel) {
		return getRandomItem(random).map(itemLoot -> itemLoot.getItemStack(random, lootingLevel));
	}

	public record ItemLoot(Supplier<? extends Item> itemSupplier, ToIntBiFunction<RandomSource, Integer> itemCountFunc) {
		public int getItemAmount(RandomSource random, int lootingLevel) {
			return itemCountFunc.applyAsInt(random, lootingLevel);
		}

		public ItemStack getItemStack(RandomSource random, int lootingLevel) {
			return new ItemStack(itemSupplier.get(), getItemAmount(random, lootingLevel));
		}
	}

}
