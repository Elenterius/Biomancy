package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModLoot;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModEntityLoot extends EntityLootSubProvider {

	protected ModEntityLoot() {
		super(FeatureFlags.REGISTRY.allFlags());
	}

	private LootTable.Builder fleshBlobLootTableBuilderWithDrop(int rolls) {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get())))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootTableReference.lootTableReference(ModEntityTypes.FLESH_BLOB.get().getDefaultLootTable())));
	}

	private LootTable.Builder fleshBlobLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get())
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(-4f, 1f)))
								.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0f, 1f))))
						.when(LootItemKilledByPlayerCondition.killedByPlayer()));
	}

	private LootTable.Builder cowLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.BEEF)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
						.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
						.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
				);
	}

	private LootTable.Builder fleshSheepLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.MUTTON)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
						.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
						.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
				);
	}

	private LootTable.Builder unshornFleshSheepLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.FLESH_BITS.get())
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.STRING)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootTableReference.lootTableReference(ModEntityTypes.FLESH_SHEEP.get().getDefaultLootTable())));
	}

	private LootTable.Builder sheepLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.MUTTON)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
						.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
						.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))));
	}

	private LootTable.Builder pigLootTable() {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.PORKCHOP)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
						.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
						.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
				);
	}

	private LootTable.Builder chickenLootTable() {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.CHICKEN)
				.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
				.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
		);
	}

	private LootTable.Builder noLoot() {
		return LootTable.lootTable();
	}

	@Override
	public void generate() {
		add(ModEntityTypes.FLESH_BLOB.get(), fleshBlobLootTable());
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_2, fleshBlobLootTableBuilderWithDrop(1));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_3, fleshBlobLootTableBuilderWithDrop(2));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_4, fleshBlobLootTableBuilderWithDrop(3));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_5, fleshBlobLootTableBuilderWithDrop(4));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_6, fleshBlobLootTableBuilderWithDrop(5));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_7, fleshBlobLootTableBuilderWithDrop(6));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_8, fleshBlobLootTableBuilderWithDrop(7));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_9, fleshBlobLootTableBuilderWithDrop(8));
		add(ModEntityTypes.FLESH_BLOB.get(), ModLoot.Entity.FLESH_BLOB_SIZE_10, fleshBlobLootTableBuilderWithDrop(9));

		add(ModEntityTypes.LEGACY_FLESH_BLOB.get(), fleshBlobLootTable());
		add(ModEntityTypes.HUNGRY_FLESH_BLOB.get(), fleshBlobLootTable());
		add(ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), fleshBlobLootTable());
		add(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get(), fleshBlobLootTable());
		add(ModEntityTypes.FLESH_COW.get(), cowLootTable());
		add(ModEntityTypes.FLESH_SHEEP.get(), fleshSheepLootTable());
		add(ModEntityTypes.FLESH_SHEEP.get(), ModLoot.Entity.FLESH_SHEEP_UNSHORN, unshornFleshSheepLootTable());
		add(ModEntityTypes.FLESH_PIG.get(), pigLootTable());
		add(ModEntityTypes.FLESH_CHICKEN.get(), chickenLootTable());
		add(ModEntityTypes.CHROMA_SHEEP.get(), sheepLootTable());
		add(ModEntityTypes.THICK_FUR_SHEEP.get(), sheepLootTable());
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		Stream<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get);
		LOGGER.info(ModLootTableProvider.LOG_MARKER, () -> String.format("generating loot tables for %s entity types...", ModEntityTypes.ENTITIES.getEntries().size()));
		return entityTypes;
	}

}
