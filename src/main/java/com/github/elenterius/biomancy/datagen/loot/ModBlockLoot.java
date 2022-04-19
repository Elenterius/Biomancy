package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;

import java.util.List;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLoot {

	private static final Marker logMarker = ModLootTableProvider.LOG_MARKER;

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
		LOGGER.info(logMarker, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

	protected static LootTable.Builder dropWithInventory(Block container) {
		return LootTable.lootTable().withPool(applyExplosionCondition(container, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(container)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
				)));
	}

	@Override
	protected void addTables() {
		LOGGER.info(logMarker, "registering block loot...");

		dropSelf(ModBlocks.CREATOR.get());
		dropSelf(ModBlocks.TONGUE.get());
		dropSelf(ModBlocks.VOICE_BOX.get());

		add(ModBlocks.BIO_FORGE.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.BIO_LAB.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.DIGESTER.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.DECOMPOSER.get(), BlockLoot::createNameableBlockEntityTable);
//		add(ModBlocks.GLAND.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.SAC.get(), BlockLoot::createNameableBlockEntityTable);

		add(ModBlocks.GULGE.get(), ModBlockLoot::dropWithInventory);
		add(ModBlocks.FLESHKIN_CHEST.get(), ModBlockLoot::dropWithInventory);

		dropSelf(ModBlocks.FLESH_BLOCK.get());
		dropSelf(ModBlocks.FLESH_BLOCK_STAIRS.get());
		add(ModBlocks.FLESH_BLOCK_SLAB.get(), BlockLoot::createSlabItemTable);
//		add(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createSingleItemTable(ModItems.NECROTIC_FLESH_LUMP.get(), BinomialDistributionGenerator.binomial(9, 0.5f)));
		dropSelf(ModBlocks.NECROTIC_FLESH_BLOCK.get());
	}

}
