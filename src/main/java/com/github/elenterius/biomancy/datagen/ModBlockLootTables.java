package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.MutatedFleshBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {

	protected static LootTable.Builder droppingWithInventory(Block itemContainer) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(itemContainer, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(itemContainer)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("Inventory", "BlockEntityTag.Inventory")
								.replaceOperation("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.replaceOperation("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingWithFuel(Block itemContainer) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(itemContainer, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(itemContainer)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("Fuel", "BlockEntityTag.Fuel")
								.replaceOperation("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.replaceOperation("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingWithSpecialFuel(Block itemContainer) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(itemContainer, LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(itemContainer)
						.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
						.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
								.replaceOperation("MainFuel", "BlockEntityTag.MainFuel")
								.replaceOperation("SpeedFuel", "BlockEntityTag.SpeedFuel")
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

//	protected static LootTable.Builder droppingFruitWithBonusOrSeeds(Block block, Item fruit, Item seeds) {
//		ILootCondition.IBuilder conditionBuilder = BlockStateProperty.builder(ModBlocks.VILE_MELON_CROP.get()).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CropsBlock.AGE, 7));
//		return withExplosionDecay(block, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(fruit).acceptCondition(conditionBuilder).alternatively(ItemLootEntry.builder(seeds)))));
//	}

	@Override
	protected void addTables() {

//		registerDropSelfLootTable(ModBlocks.VILE_MELON_BLOCK.get());
//		registerLootTable(ModBlocks.VILE_MELON_CROP.get(), droppingFruitWithBonusOrSeeds(ModBlocks.VILE_MELON_CROP.get(), ModItems.VILE_MELON_BLOCK.get(), ModItems.VILE_MELON_SEEDS.get()));

		registerDropSelfLootTable(ModBlocks.FLESH_TENTACLE.get());
		registerDropSelfLootTable(ModBlocks.FLESH_BLOCK.get());
		registerLootTable(ModBlocks.NECROTIC_FLESH_BLOCK.get(), droppingRandomly(ModItems.NECROTIC_FLESH.get(), BinomialRange.of(9, 0.5f)));
		registerLootTable(ModBlocks.FLESH_BLOCK_SLAB.get(), BlockLootTables::droppingSlab);
		registerDropSelfLootTable(ModBlocks.FLESH_BLOCK_STAIRS.get());
//		registerLootTable(ModBlocks.MUTATED_FLESH_BLOCK.get(), ModBlockLootTables::droppingMutatedFlesh);
		registerLootTable(ModBlocks.FLESHBORN_DOOR.get(), ModBlockLootTables::droppingSimpleOwnableDoor);
		registerLootTable(ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlockLootTables::droppingSimpleOwnable);
		registerLootTable(ModBlocks.FLESHBORN_PRESSURE_PLATE.get(), ModBlockLootTables::droppingSimpleOwnable);

		registerLootTable(ModBlocks.MEATSOUP_CAULDRON.get(), dropping(Blocks.CAULDRON));
		registerLootTable(ModBlocks.GULGE.get(), ModBlockLootTables::droppingWithInventory);
		registerLootTable(ModBlocks.FLESHBORN_CHEST.get(), ModBlockLootTables::droppingWithInventory);
		registerLootTable(ModBlocks.CHEWER.get(), ModBlockLootTables::droppingWithFuel);
		registerLootTable(ModBlocks.DIGESTER.get(), ModBlockLootTables::droppingWithFuel);
		registerLootTable(ModBlocks.DECOMPOSER.get(), ModBlockLootTables::droppingWithSpecialFuel);
		registerLootTable(ModBlocks.EVOLUTION_POOL.get(), dropping(ModBlocks.FLESH_BLOCK_STAIRS.get()));

//		registerLootTable(ModBlocks.LUMINOUS_SOIL.get(), (soil) -> droppingWithSilkTouch(soil, withExplosionDecay(soil, ItemLootEntry.builder(ModItems.LUMINESCENT_SPORES.get())
//				.acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		BiomancyMod.LOGGER.info(MarkerManager.getMarker("BockLootTables"), String.format("generating loot tables for %s blocks...", blocks.size()));
		return blocks;
	}
}
