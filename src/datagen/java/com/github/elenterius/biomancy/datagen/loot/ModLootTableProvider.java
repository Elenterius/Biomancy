package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider {
	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(
			//            Pair.of(FishingLootTables::new, LootParameterSets.FISHING),
			//            Pair.of(ChestLootTables::new, LootParameterSets.CHEST),
			Pair.of(ModEntityLootTables::new, LootParameterSets.ENTITY),
			Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK)
			//            Pair.of(PiglinBarteringAddition::new, LootParameterSets.PIGLIN_BARTER),
			//            Pair.of(GiftLootTables::new, LootParameterSets.GIFT)
	);

	public ModLootTableProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
		return lootTables;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
		// do nothing, we can remove the override when all lootTables are present
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
	}
}
