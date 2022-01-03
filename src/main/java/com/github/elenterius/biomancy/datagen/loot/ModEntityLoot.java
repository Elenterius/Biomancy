package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModEntityLoot extends EntityLoot {

	private final Marker logMarker = ModLootTableProvider.LOG_MARKER;

//	private static LootTable.Builder fleshBlobLootTableBuilderWithDrop(int rolls) {
//		return LootTable.lootTable()
//				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).add(LootItem.lootTableItem(ModItems.FLESH_BLOCK.get())))
//				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootTableReference.lootTableReference(ModEntityTypes.FLESH_BLOB.get().getDefaultLootTable())));
//	}

	@Override
	protected void addTables() {
//		add(ModEntityTypes.FLESH_BLOB.get(), LootTable.lootTable()
//				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.FLESH_BLOCK.get())
//						.apply(SetCount.setCount(ConstantRange.exactly(1))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
//				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
//						.add(ItemLootEntry.lootTableItem(ModItems.OCULUS.get()).apply(SetCount.setCount(RandomValueRange.between(-4f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))
//						.when(KilledByPlayer.killedByPlayer()))
//				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
//						.add(ItemLootEntry.lootTableItem(ModItems.STOMACH.get()).apply(SetCount.setCount(RandomValueRange.between(-2f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
//		);
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_2, fleshBlobLootTableBuilderWithDrop(1));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_3, fleshBlobLootTableBuilderWithDrop(2));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_4, fleshBlobLootTableBuilderWithDrop(3));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_5, fleshBlobLootTableBuilderWithDrop(4));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_6, fleshBlobLootTableBuilderWithDrop(5));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_7, fleshBlobLootTableBuilderWithDrop(6));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_8, fleshBlobLootTableBuilderWithDrop(7));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_9, fleshBlobLootTableBuilderWithDrop(8));
//		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_10, fleshBlobLootTableBuilderWithDrop(9));
	}

	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		List<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		LOGGER.info(logMarker, () -> String.format("generating loot tables for %s entity types...", entityTypes.size()));
		return entityTypes;
	}

}
