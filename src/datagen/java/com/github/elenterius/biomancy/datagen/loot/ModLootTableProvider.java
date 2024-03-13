package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModLoot;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("LootTableProvider");

	public ModLootTableProvider(PackOutput packOutput) {
		super(packOutput, Set.of(), create(packOutput).getTables());
	}

	private static LootTableProvider create(PackOutput output) {
		Set<ResourceLocation> requiredTables = ModLoot.Entity.all();

		List<SubProviderEntry> subProviders = List.of(
				new SubProviderEntry(ModEntityLoot::new, LootContextParamSets.ENTITY),
				new SubProviderEntry(ModDespoilLoot::new, LootContextParamSets.ENTITY),
				new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)
		);

		return new LootTableProvider(output, requiredTables, subProviders);
	}

}
