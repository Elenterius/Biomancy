package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLoot {

	private static final Marker logMarker = ModLootTableProvider.LOG_MARKER;

	protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
	protected static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
		LOGGER.info(logMarker, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

	protected static LootTable.Builder createNameableBioMachineTable(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Fuel", "BlockEntityTag.Fuel"))
				)));
	}

	protected static LootTable.Builder dropWithInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
				)));
	}

	protected static LootTable.Builder dropOwnableInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("OwnerUUID", "BlockEntityTag.OwnerUUID"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("UserList", "BlockEntityTag.UserList"))
				)));
	}

	protected static LootTable.Builder dropWithOwnableData(Block container) {
		return LootTable.lootTable().withPool(applyExplosionCondition(container, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(container)
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("OwnerUUID", "BlockEntityTag.OwnerUUID"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("UserList", "BlockEntityTag.UserList"))
				)));
	}

	protected static LootTable.Builder createFleshDoorTable(DoorBlock block) {
		return createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
	}

	protected static LootTable.Builder createDirectionalSlabTable(Block slab) {
		return LootTable.lootTable().withPool(
				LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(applyExplosionDecay(slab, LootItem.lootTableItem(slab)
								.apply(
										SetItemCountFunction.setCount(ConstantValue.exactly(2)).when(
												LootItemBlockStatePropertyCondition
														.hasBlockStateProperties(slab)
														.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DirectionalSlabBlock.TYPE, DirectionalSlabType.FULL))
										))
						)));
	}

	protected static LootTable.Builder createFleshSpikeTable(FleshSpikeBlock block) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(block, LootItem.lootTableItem(block).apply(
						IntStream.range(FleshSpikeBlock.MIN_SPIKES + 1, FleshSpikeBlock.MAX_SPIKES + 1).boxed().toList(),
						spikes -> SetItemCountFunction.setCount(ConstantValue.exactly(spikes))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FleshSpikeBlock.SPIKES, spikes)))
				))));
	}

	@Override
	protected void addTables() {
		LOGGER.info(logMarker, "registering block loot...");

		dropSelf(ModBlocks.PRIMORDIAL_CRADLE.get());
		dropSelf(ModBlocks.TONGUE.get());
		dropSelf(ModBlocks.MAW_HOPPER.get());
		add(ModBlocks.STORAGE_SAC.get(), ModBlockLoot::dropWithInventory);

		add(ModBlocks.BIO_FORGE.get(), ModBlockLoot::createNameableBioMachineTable);
		add(ModBlocks.BIO_LAB.get(), ModBlockLoot::createNameableBioMachineTable);
		add(ModBlocks.DIGESTER.get(), ModBlockLoot::createNameableBioMachineTable);
		add(ModBlocks.DECOMPOSER.get(), ModBlockLoot::createNameableBioMachineTable);

		add(ModBlocks.FLESHKIN_CHEST.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.FLESHKIN_DOOR.get(), ModBlockLoot::dropWithOwnableData);
		add(ModBlocks.FLESHKIN_TRAPDOOR.get(), ModBlockLoot::dropWithOwnableData);
		add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get(), ModBlockLoot::dropWithOwnableData);

		dropSelf(ModBlocks.FLESH.get());
		add(ModBlocks.FLESH_SLAB.get(), ModBlockLoot::createDirectionalSlabTable);
		dropSelf(ModBlocks.FLESH_STAIRS.get());
		dropSelf(ModBlocks.FLESH_WALL.get());

		dropSelf(ModBlocks.PACKED_FLESH.get());
		add(ModBlocks.PACKED_FLESH_SLAB.get(), ModBlockLoot::createDirectionalSlabTable);
		dropSelf(ModBlocks.PACKED_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PACKED_FLESH_WALL.get());

		dropSelf(ModBlocks.PRIMAL_FLESH.get());
		add(ModBlocks.PRIMAL_FLESH_SLAB.get(), ModBlockLoot::createDirectionalSlabTable);
		dropSelf(ModBlocks.PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.CORRUPTED_PRIMAL_FLESH.get());

		dropSelf(ModBlocks.MALIGNANT_FLESH.get());
		add(ModBlocks.MALIGNANT_FLESH_SLAB.get(), ModBlockLoot::createDirectionalSlabTable);
		dropSelf(ModBlocks.MALIGNANT_FLESH_STAIRS.get());
		add(ModBlocks.MALIGNANT_FLESH_VEINS.get(), block -> createMultifaceBlockDrops(block, HAS_SHEARS_OR_SILK_TOUCH));

		dropSelf(ModBlocks.VOICE_BOX.get());
		dropSelf(ModBlocks.FLESH_IRIS_DOOR.get());
		dropSelf(ModBlocks.FLESH_FENCE.get());
		dropSelf(ModBlocks.FLESH_FENCE_GATE.get());
		dropSelf(ModBlocks.FLESH_LADDER.get());
		dropSelf(ModBlocks.FLESH_PILLAR.get());
		dropSelf(ModBlocks.YELLOW_BIO_LANTERN.get());
		dropSelf(ModBlocks.BLUE_BIO_LANTERN.get());
		dropSelf(ModBlocks.TENDON_CHAIN.get());
		dropSelf(ModBlocks.VIAL_HOLDER.get());

		addCustom(ModBlocks.FLESH_DOOR.get(), ModBlockLoot::createFleshDoorTable);
		addCustom(ModBlocks.FULL_FLESH_DOOR.get(), ModBlockLoot::createFleshDoorTable);

		addCustom(ModBlocks.FLESH_SPIKE.get(), ModBlockLoot::createFleshSpikeTable);
	}

	protected <T extends Block> void addCustom(T block, Function<T, LootTable.Builder> function) {
		add(block, function.apply(block));
	}

}
