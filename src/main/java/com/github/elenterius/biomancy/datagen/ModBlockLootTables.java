package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.MeatsoupCauldronBlock;
import com.github.elenterius.biomancy.block.MutatedFleshBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
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
		return LootTable.lootTable().withPool(applyExplosionCondition(itemContainer, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(itemContainer)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("Inventory", "BlockEntityTag.Inventory")
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingWithFuel(Block itemContainer) {
		return LootTable.lootTable().withPool(applyExplosionCondition(itemContainer, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(itemContainer)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("Fuel", "BlockEntityTag.Fuel")
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingWithFluidInput(Block itemContainer) {
		return LootTable.lootTable().withPool(applyExplosionCondition(itemContainer, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(itemContainer)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("FluidInput", "BlockEntityTag.FluidInput")
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingWithFuelAndFluidOutput(Block itemContainer) {
		return LootTable.lootTable().withPool(applyExplosionCondition(itemContainer, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(itemContainer)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("Fuel", "BlockEntityTag.Fuel")
								.copy("FluidOutput", "BlockEntityTag.FluidOutput")
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
				)));
	}

	protected static LootTable.Builder droppingSimpleOwnableDoor(Block ownableDoor) {
		return LootTable.lootTable().withPool(applyExplosionCondition(ownableDoor, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(ownableDoor)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
						.when(BlockStateProperty.hasBlockStateProperties(ownableDoor).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
				)));
	}

	protected static LootTable.Builder droppingSimpleOwnable(Block ownableBock) {
		return LootTable.lootTable().withPool(applyExplosionCondition(ownableBock, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(ownableBock)
						.apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
						.apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
								.copy("OwnerUUID", "BlockEntityTag.OwnerUUID")
								.copy("UserList", "BlockEntityTag.UserList")
						)
				)));

	}

	protected static LootTable.Builder droppingMutatedFlesh(Block flesh) {
		return LootTable.lootTable().withPool(applyExplosionCondition(flesh, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.add(ItemLootEntry.lootTableItem(flesh).apply(CopyBlockState.copyState(flesh).copy(MutatedFleshBlock.MUTATION_TYPE)))));
	}

	protected static LootTable.Builder droppingCauldronWithFlesh() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
						.add(applyExplosionDecay(Blocks.CAULDRON, ItemLootEntry.lootTableItem(Items.CAULDRON))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(9))
						.add(ItemLootEntry.lootTableItem(ModItems.NECROTIC_FLESH.get()))
						.when(BlockStateProperty.hasBlockStateProperties(ModBlocks.MEATSOUP_CAULDRON.get())
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MeatsoupCauldronBlock.LEVEL, MeatsoupCauldronBlock.MAX_LEVEL))));
	}

	//fix for reflection issue with jdk 16
	protected static LootTable.Builder createSlabItemTable(Block block) {
		return BlockLootTables.createSlabItemTable(block);
	}

	@Override
	protected void addTables() {

//		registerDropSelfLootTable(ModBlocks.VILE_MELON_BLOCK.get());
//		registerLootTable(ModBlocks.VILE_MELON_CROP.get(), droppingFruitWithBonusOrSeeds(ModBlocks.VILE_MELON_CROP.get(), ModItems.VILE_MELON_BLOCK.get(), ModItems.VILE_MELON_SEEDS.get()));

		dropSelf(ModBlocks.FLESH_TENTACLE.get());
		dropSelf(ModBlocks.FLESH_BLOCK.get());
		add(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createSingleItemTable(ModItems.NECROTIC_FLESH.get(), BinomialRange.binomial(9, 0.5f)));
		add(ModBlocks.FLESH_BLOCK_SLAB.get(), ModBlockLootTables::createSlabItemTable);
		dropSelf(ModBlocks.FLESH_BLOCK_STAIRS.get());
//		registerLootTable(ModBlocks.MUTATED_FLESH_BLOCK.get(), ModBlockLootTables::droppingMutatedFlesh);
		add(ModBlocks.FLESHBORN_DOOR.get(), ModBlockLootTables::droppingSimpleOwnableDoor);
		add(ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlockLootTables::droppingSimpleOwnable);
		add(ModBlocks.FLESHBORN_PRESSURE_PLATE.get(), ModBlockLootTables::droppingSimpleOwnable);

		add(ModBlocks.MEATSOUP_CAULDRON.get(), droppingCauldronWithFlesh());
		add(ModBlocks.GULGE.get(), ModBlockLootTables::droppingWithInventory);
		add(ModBlocks.FLESHBORN_CHEST.get(), ModBlockLootTables::droppingWithInventory);

		add(ModBlocks.CHEWER.get(), ModBlockLootTables::droppingWithFuel);
		add(ModBlocks.DIGESTER.get(), ModBlockLootTables::droppingWithFuelAndFluidOutput);
		add(ModBlocks.SOLIDIFIER.get(), ModBlockLootTables::droppingWithFluidInput);
		add(ModBlocks.DECOMPOSER.get(), ModBlockLootTables::droppingWithFuel);
		add(ModBlocks.EVOLUTION_POOL.get(), createSingleItemTable(ModBlocks.FLESH_BLOCK_STAIRS.get()));

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
