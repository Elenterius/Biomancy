package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.*;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {

	protected static LootTable.Builder droppingWithContents(Block itemContainer) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(itemContainer, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(itemContainer)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("Contents", "BlockEntityTag.Contents")
								.replaceOperation("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.replaceOperation("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	@Override
	protected void addTables() {

		registerDropSelfLootTable(ModBlocks.INFERTILE_SOIL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG_AND_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL_GROUP.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SHROOM_TALL.get());
		registerDropSelfLootTable(ModBlocks.CANDELABRA_FUNGUS.get());
		registerDropSelfLootTable(ModBlocks.LILY_TREE_SAPLING.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_0.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_1.get());
		registerDropSelfLootTable(ModBlocks.LILY_TREE_STEM.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_QUARTZ_ORE.get());

		registerLootTable(ModBlocks.BLIGHT_MOSS_SLAB.get(), BlockLootTables::droppingSlab);

		registerLootTable(ModBlocks.GULGE.get(), ModBlockLootTables::droppingWithContents);

		registerLootTable(ModBlocks.LUMINOUS_SOIL.get(), (soil) -> droppingWithSilkTouch(soil, withExplosionDecay(soil, ItemLootEntry.builder(ModItems.LUMINESCENT_SPORES.get())
				.acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		BlightlingsMod.LOGGER.info(MarkerManager.getMarker("BockLootTables"), String.format("generating loot tables for %s blocks...", blocks.size()));
		return blocks;
	}
}
