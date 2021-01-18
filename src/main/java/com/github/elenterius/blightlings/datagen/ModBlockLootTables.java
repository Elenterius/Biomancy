package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.block.MutatedFleshBlock;
import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.*;
import net.minecraft.state.properties.DoubleBlockHalf;
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

	protected static LootTable.Builder droppingSimpleOwnableDoor(Block ownableDoor) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(ownableDoor, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(ownableDoor)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.replaceOperation("UserList", "BlockEntityTag.UserList")
						)
						.acceptCondition(BlockStateProperty.builder(ownableDoor).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
				)));
	}

	protected static LootTable.Builder droppingSimpleOwnable(Block ownableBock) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(ownableBock, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(ownableBock)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.replaceOperation("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingMutatedFlesh(Block flesh) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(flesh, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(flesh).acceptFunction(CopyBlockState.func_227545_a_(flesh).func_227552_a_(MutatedFleshBlock.MUTATION_TYPE)))));
	}

	@Override
	protected void addTables() {

		registerDropSelfLootTable(ModBlocks.INFERTILE_SOIL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG_AND_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL_GROUP.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SHROOM_TALL.get());
		registerDropSelfLootTable(ModBlocks.LILY_TREE_SAPLING.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT_SMALL.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_0.get());
		registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_1.get());
		registerDropSelfLootTable(ModBlocks.LILY_TREE_STEM.get());

		registerLootTable(ModBlocks.BLIGHT_MOSS_SLAB.get(), BlockLootTables::droppingSlab);

		registerDropSelfLootTable(ModBlocks.FLESH_BLOCK.get());
		registerLootTable(ModBlocks.FLESH_BLOCK_SLAB.get(), BlockLootTables::droppingSlab);
		registerLootTable(ModBlocks.MUTATED_FLESH_BLOCK.get(), ModBlockLootTables::droppingMutatedFlesh);
		registerLootTable(ModBlocks.BIO_FLESH_DOOR.get(), ModBlockLootTables::droppingSimpleOwnableDoor);
		registerLootTable(ModBlocks.BIO_FLESH_TRAPDOOR.get(), ModBlockLootTables::droppingSimpleOwnable);
		registerLootTable(ModBlocks.BIO_FLESH_PRESSURE_PLATE.get(), ModBlockLootTables::droppingSimpleOwnable);
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
