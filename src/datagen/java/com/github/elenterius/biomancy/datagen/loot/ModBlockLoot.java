package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.chrysalis.Chrysalis;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlockEntity;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLootSubProvider {

	private static final Marker logMarker = ModLootTableProvider.LOG_MARKER;

	protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
	protected static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);

	private static final Set<Item> EXPLOSION_RESISTANT = Set.of();

	public ModBlockLoot() {
		super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
		LOGGER.info(logMarker, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

	protected LootTable.Builder createShearsOrSilkTouchOnlyDrop(ItemLike item) {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(HAS_SHEARS_OR_SILK_TOUCH).add(LootItem.lootTableItem(item)));
	}

	protected LootTable.Builder createNameableBioMachineTable(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Fuel", "BlockEntityTag.Fuel"))
				)));
	}

	protected LootTable.Builder createPrimordialCradleTable(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("PrimalEnergy", "BlockEntityTag.PrimalEnergy"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("ProcGenValues", "BlockEntityTag.ProcGenValues"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("SacrificeHandler", "BlockEntityTag.SacrificeHandler"))
				)));
	}

	protected LootTable.Builder dropWithInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
				)));
	}

	protected LootTable.Builder dropChrysalisWithEntity(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(Chrysalis.ENTITY_KEY, "BlockEntityTag." + Chrysalis.ENTITY_KEY))
				)));
	}

	protected LootTable.Builder dropMembraneSettings(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(BiometricMembraneBlockEntity.MEMBRANE_KEY, "BlockEntityTag." + BiometricMembraneBlockEntity.MEMBRANE_KEY))
				)));
	}

	protected LootTable.Builder dropOwnableInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("OwnerUUID", "BlockEntityTag.OwnerUUID"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("UserList", "BlockEntityTag.UserList"))
				)));
	}

	protected LootTable.Builder dropWithOwnableData(Block container) {
		return LootTable.lootTable().withPool(applyExplosionCondition(container, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(container)
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("OwnerUUID", "BlockEntityTag.OwnerUUID"))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("UserList", "BlockEntityTag.UserList"))
				)));
	}

	protected LootTable.Builder createFleshDoorTable(DoorBlock block) {
		return createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
	}

	protected LootTable.Builder createDirectionalSlabTable(Block slab) {
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

	protected LootTable.Builder createFleshSpikeTable(FleshSpikeBlock block) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(block, LootItem.lootTableItem(block).apply(
						IntStream.range(FleshSpikeBlock.SPIKES.getMin() + 1, FleshSpikeBlock.SPIKES.getMax() + 1).boxed().toList(),
						spikes -> SetItemCountFunction.setCount(ConstantValue.exactly(spikes))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FleshSpikeBlock.SPIKES.get(), spikes)))
				))));
	}

	protected LootTable.Builder drop(Item item) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(item,LootItem.lootTableItem(item))));
	}

	@Override
	protected void generate() {
		LOGGER.info(logMarker, "registering block loot...");

		add(ModBlocks.PRIMORDIAL_CRADLE.get(), this::createPrimordialCradleTable);
		dropSelf(ModBlocks.TONGUE.get());
		dropSelf(ModBlocks.MAW_HOPPER.get());
		add(ModBlocks.STORAGE_SAC.get(), this::dropWithInventory);
		add(ModBlocks.CHRYSALIS.get(), this::dropChrysalisWithEntity);

		add(ModBlocks.BIO_FORGE.get(), this::createNameableBioMachineTable);
		add(ModBlocks.BIO_LAB.get(), this::createNameableBioMachineTable);
		add(ModBlocks.DIGESTER.get(), this::createNameableBioMachineTable);
		add(ModBlocks.DECOMPOSER.get(), this::createNameableBioMachineTable);

		add(ModBlocks.FLESHKIN_CHEST.get(), this::createNameableBlockEntityTable);
		add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get(), this::dropWithOwnableData);

		dropSelf(ModBlocks.FLESH.get());
		add(ModBlocks.FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.FLESH_STAIRS.get());
		dropSelf(ModBlocks.FLESH_WALL.get());

		dropSelf(ModBlocks.PACKED_FLESH.get());
		add(ModBlocks.PACKED_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.PACKED_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PACKED_FLESH_WALL.get());

		dropSelf(ModBlocks.FIBROUS_FLESH.get());
		add(ModBlocks.FIBROUS_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.FIBROUS_FLESH_STAIRS.get());
		dropSelf(ModBlocks.FIBROUS_FLESH_WALL.get());

		dropSelf(ModBlocks.FLESH_PILLAR.get());
		dropSelf(ModBlocks.CHISELED_FLESH.get());
		dropSelf(ModBlocks.ORNATE_FLESH.get());
		add(ModBlocks.ORNATE_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.TUBULAR_FLESH_BLOCK.get());

		dropSelf(ModBlocks.PRIMAL_FLESH.get());
		add(ModBlocks.PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH.get());
		add(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH.get());
		add(ModBlocks.POROUS_PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.MALIGNANT_FLESH.get());
		add(ModBlocks.MALIGNANT_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.MALIGNANT_FLESH_STAIRS.get());
		dropSelf(ModBlocks.MALIGNANT_FLESH_WALL.get());
		add(ModBlocks.MALIGNANT_FLESH_VEINS.get(), block -> createMultifaceBlockDrops(block, HAS_SHEARS_OR_SILK_TOUCH));
		add(ModBlocks.PRIMAL_BLOOM.get(), this::createShearsOrSilkTouchOnlyDrop);
		dropSelf(ModBlocks.PRIMAL_ORIFICE.get());

		dropSelf(ModBlocks.IMPERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.IMPERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE.get());
		add(ModBlocks.BIOMETRIC_MEMBRANE.get(), this::dropMembraneSettings);

		dropSelf(ModBlocks.MODULAR_LARYNX.get());
		//dropSelf(ModBlocks.NEURAL_INTERCEPTOR.get());

		dropSelf(ModBlocks.FLESH_IRIS_DOOR.get());
		dropSelf(ModBlocks.FLESH_FENCE.get());
		dropSelf(ModBlocks.FLESH_FENCE_GATE.get());
		dropSelf(ModBlocks.FLESH_LADDER.get());
		dropSelf(ModBlocks.YELLOW_BIO_LANTERN.get());
		dropSelf(ModBlocks.PRIMORDIAL_BIO_LANTERN.get());
		dropSelf(ModBlocks.BLOOMLIGHT.get());
		dropSelf(ModBlocks.BLUE_BIO_LANTERN.get());
		dropSelf(ModBlocks.TENDON_CHAIN.get());
		dropSelf(ModBlocks.VIAL_HOLDER.get());

		addCustom(ModBlocks.FLESH_DOOR.get(), this::createFleshDoorTable);
		addCustom(ModBlocks.FULL_FLESH_DOOR.get(), this::createFleshDoorTable);

		addCustom(ModBlocks.FLESH_SPIKE.get(), this::createFleshSpikeTable);

		add(ModBlocks.ACID_FLUID_BLOCK.get(), noDrop());
		add(ModBlocks.ACID_CAULDRON.get(), drop(Items.CAULDRON));
	}

	protected <T extends Block> void addCustom(T block, Function<T, LootTable.Builder> function) {
		add(block, function.apply(block));
	}

}
