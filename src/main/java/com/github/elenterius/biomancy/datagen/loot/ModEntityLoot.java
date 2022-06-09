package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModEntityLoot extends EntityLoot {

	private static LootTable.Builder fleshBlobLootTableBuilderWithDrop(int rolls) {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get())))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootTableReference.lootTableReference(ModEntityTypes.FLESH_BLOB.get().getDefaultLootTable())));
	}

	@Override
	protected void addTables() {
		add(ModEntityTypes.FLESH_BLOB.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get())
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(-4f, 1f))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0f, 1f))))
						.when(LootItemKilledByPlayerCondition.killedByPlayer()))
		);
		add(ModLoot.Entity.FLESH_BLOB_SIZE_2, fleshBlobLootTableBuilderWithDrop(1));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_3, fleshBlobLootTableBuilderWithDrop(2));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_4, fleshBlobLootTableBuilderWithDrop(3));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_5, fleshBlobLootTableBuilderWithDrop(4));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_6, fleshBlobLootTableBuilderWithDrop(5));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_7, fleshBlobLootTableBuilderWithDrop(6));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_8, fleshBlobLootTableBuilderWithDrop(7));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_9, fleshBlobLootTableBuilderWithDrop(8));
		add(ModLoot.Entity.FLESH_BLOB_SIZE_10, fleshBlobLootTableBuilderWithDrop(9));

		add(ModEntityTypes.BOOMLING.get(), LootTable.lootTable()); //no item drops

		add(ModEntityTypes.FLESHKIN.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ModItems.LIVING_FLESH.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(2f, 4f)))
								.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0f, 1f)))))
		);
	}

	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		List<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		LOGGER.info(ModLootTableProvider.LOG_MARKER, () -> String.format("generating loot tables for %s entity types...", entityTypes.size()));
		return entityTypes;
	}

}
