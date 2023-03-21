package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;

import java.util.List;
import java.util.function.Function;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLoot {

	private static final Marker logMarker = ModLootTableProvider.LOG_MARKER;

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
		LOGGER.info(logMarker, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

	protected static LootTable.Builder dropWithInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Inventory", "BlockEntityTag.Inventory"))
				)));
	}

	protected static LootTable.Builder dropFleshkinChest(Block block) {
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

	@Override
	protected void addTables() {
		LOGGER.info(logMarker, "registering block loot...");

		dropSelf(ModBlocks.PRIMORDIAL_CRADLE.get());
		dropSelf(ModBlocks.TONGUE.get());
		dropSelf(ModBlocks.MAW_HOPPER.get());
		add(ModBlocks.STORAGE_SAC.get(), ModBlockLoot::dropWithInventory);

		add(ModBlocks.BIO_FORGE.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.BIO_LAB.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.DIGESTER.get(), BlockLoot::createNameableBlockEntityTable);
		add(ModBlocks.DECOMPOSER.get(), BlockLoot::createNameableBlockEntityTable);

		add(ModBlocks.FLESHKIN_CHEST.get(), ModBlockLoot::dropFleshkinChest);
		add(ModBlocks.FLESHKIN_DOOR.get(), ModBlockLoot::dropWithOwnableData);
		add(ModBlocks.FLESHKIN_TRAPDOOR.get(), ModBlockLoot::dropWithOwnableData);
		add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get(), ModBlockLoot::dropWithOwnableData);

		dropSelf(ModBlocks.FLESH.get());
		add(ModBlocks.FLESH_SLAB.get(), BlockLoot::createSlabItemTable);
		dropSelf(ModBlocks.FLESH_STAIRS.get());
		dropSelf(ModBlocks.FLESH_WALL.get());

		dropSelf(ModBlocks.PACKED_FLESH.get());
		add(ModBlocks.PACKED_FLESH_SLAB.get(), BlockLoot::createSlabItemTable);
		dropSelf(ModBlocks.PACKED_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PACKED_FLESH_WALL.get());

		dropSelf(ModBlocks.PRIMAL_FLESH.get());
		add(ModBlocks.PRIMAL_FLESH_SLAB.get(), BlockLoot::createSlabItemTable);
		dropSelf(ModBlocks.PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.CORRUPTED_PRIMAL_FLESH.get());

		dropSelf(ModBlocks.MALIGNANT_FLESH.get());
		add(ModBlocks.MALIGNANT_FLESH_SLAB.get(), BlockLoot::createSlabItemTable);
		dropSelf(ModBlocks.MALIGNANT_FLESH_STAIRS.get());
		add(ModBlocks.MALIGNANT_FLESH_VEINS.get(), (block) -> {return createMultifaceBlockDrops(block, MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)));});

		dropSelf(ModBlocks.VOICE_BOX.get());
		dropSelf(ModBlocks.FLESH_IRIS_DOOR.get());
		dropSelf(ModBlocks.FLESH_FENCE.get());
		dropSelf(ModBlocks.FLESH_FENCE_GATE.get());
		dropSelf(ModBlocks.FLESH_LADDER.get());
		dropSelf(ModBlocks.BIO_LANTERN.get());
		dropSelf(ModBlocks.TENDON_CHAIN.get());

		addCustom(ModBlocks.FLESH_DOOR.get(), ModBlockLoot::createFleshDoorTable);
		addCustom(ModBlocks.FULL_FLESH_DOOR.get(), ModBlockLoot::createFleshDoorTable);

		dropSelf(ModBlocks.BONE_SPIKE.get());
	}

	protected <T extends Block> void addCustom(T block, Function<T, LootTable.Builder> function) {
		add(block, function.apply(block));
	}

}
