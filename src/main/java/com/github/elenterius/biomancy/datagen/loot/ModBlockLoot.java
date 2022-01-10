package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;

import java.util.List;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLoot {

	private static final Marker logMarker = ModLootTableProvider.LOG_MARKER;

	@Override
	protected void addTables() {
		LOGGER.info(logMarker, "registering block loot...");

		dropSelf(ModBlocks.CREATOR.get());
		dropSelf(ModBlocks.DECOMPOSER.get());

		dropSelf(ModBlocks.FLESH_BLOCK.get());
		dropSelf(ModBlocks.FLESH_BLOCK_STAIRS.get());
		add(ModBlocks.FLESH_BLOCK_SLAB.get(), BlockLoot::createSlabItemTable);
		add(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createSingleItemTable(ModItems.NECROTIC_FLESH_LUMP.get(), BinomialDistributionGenerator.binomial(9, 0.5f)));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
		LOGGER.info(logMarker, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

}
