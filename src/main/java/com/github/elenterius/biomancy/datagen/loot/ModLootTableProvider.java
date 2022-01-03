package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("LootTableProvider");

	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = List.of(
			Pair.of(ModBlockLoot::new, LootContextParamSets.BLOCK),
			Pair.of(ModEntityLoot::new, LootContextParamSets.ENTITY)
	);

	public ModLootTableProvider(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		return lootTables;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
		map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationContext, resourceLocation, lootTable));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
	}

}
