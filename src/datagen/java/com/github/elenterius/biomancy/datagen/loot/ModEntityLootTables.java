package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.entity.mutation.SilkyWoolSheepEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.Smelt;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.stream.Collectors;

public class ModEntityLootTables extends EntityLootTables {

//	private static LootTable.Builder sheepLootTableBuilderWithDrop(IItemProvider wool) {
//		return LootTable.builder()
//				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(wool)))
//				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(EntityType.SHEEP.getLootTable())));
//	}

	private static LootTable.Builder fleshBlobLootTableBuilderWithDrop(int rolls) {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(rolls)).add(ItemLootEntry.lootTableItem(ModItems.FLESH_BLOCK.get())))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(TableLootEntry.lootTableReference(ModEntityTypes.FLESH_BLOB.get().getDefaultLootTable())));
	}

	@Override
	protected void addTables() {
		add(ModEntityTypes.BOOMLING.get(), LootTable.lootTable()); //no item drops

		add(ModEntityTypes.BROOD_MOTHER.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.STRING).apply(SetCount.setCount(RandomValueRange.between(0f, 2f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(2))
						.add(ItemLootEntry.lootTableItem(ModItems.HORMONE_BILE.get()).apply(SetCount.setCount(RandomValueRange.between(-1f, 2f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 2f))))
						.when(KilledByPlayer.killedByPlayer())));

		add(ModEntityTypes.BEETLING.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.ERODING_BILE.get()).apply(SetCount.setCount(RandomValueRange.between(0f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.MENISCUS_LENS.get()).apply(SetCount.setCount(RandomValueRange.between(-2f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))).when(KilledByPlayer.killedByPlayer())));

		add(ModEntityTypes.FLESHKIN.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.NECROTIC_FLESH.get())
						.apply(SetCount.setCount(RandomValueRange.between(6f, 9f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
		);

		add(ModEntityTypes.FLESH_BLOB.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.FLESH_BLOCK.get())
						.apply(SetCount.setCount(ConstantRange.exactly(1))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ModItems.OCULUS.get()).apply(SetCount.setCount(RandomValueRange.between(-4f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))
						.when(KilledByPlayer.killedByPlayer()))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ModItems.STOMACH.get()).apply(SetCount.setCount(RandomValueRange.between(-2f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
		);
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_2, fleshBlobLootTableBuilderWithDrop(1));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_3, fleshBlobLootTableBuilderWithDrop(2));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_4, fleshBlobLootTableBuilderWithDrop(3));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_5, fleshBlobLootTableBuilderWithDrop(4));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_6, fleshBlobLootTableBuilderWithDrop(5));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_7, fleshBlobLootTableBuilderWithDrop(6));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_8, fleshBlobLootTableBuilderWithDrop(7));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_9, fleshBlobLootTableBuilderWithDrop(8));
		add(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_10, fleshBlobLootTableBuilderWithDrop(9));

		add(ModEntityTypes.OCULUS_OBSERVER.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ModItems.OCULUS.get()).apply(SetCount.setCount(RandomValueRange.between(0f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))
						.when(KilledByPlayer.killedByPlayer()))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ModItems.MENISCUS_LENS.get()).apply(SetCount.setCount(RandomValueRange.between(-2f, 1f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
		);

		add(ModEntityTypes.FAILED_SHEEP.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.FLESH_LUMP.get()).apply(SetCount.setCount(RandomValueRange.between(1, 3))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.SKIN_CHUNK.get()).apply(SetCount.setCount(RandomValueRange.between(2, 4))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));
		add(ModEntityTypes.CHROMA_SHEEP.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.MUTTON).apply(SetCount.setCount(RandomValueRange.between(1, 2))).apply(Smelt.smelted().when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));
		add(ModEntityTypes.THICK_WOOL_SHEEP.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.MUTTON).apply(SetCount.setCount(RandomValueRange.between(1, 2))).apply(Smelt.smelted().when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));

		add(ModEntityTypes.SILKY_WOOL_SHEEP.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.MUTTON).apply(SetCount.setCount(RandomValueRange.between(2, 3))).apply(Smelt.smelted().when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));
		add(SilkyWoolSheepEntity.SILKY_WOOL_LOOT_TABLE, LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.STRING).apply(SetCount.setCount(RandomValueRange.between(0, 3)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(TableLootEntry.lootTableReference(ModEntityTypes.SILKY_WOOL_SHEEP.get().getDefaultLootTable()))));

		add(ModEntityTypes.FAILED_COW.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.FLESH_LUMP.get()).apply(SetCount.setCount(RandomValueRange.between(1, 3))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(ModItems.SKIN_CHUNK.get()).apply(SetCount.setCount(RandomValueRange.between(2, 4))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));
		add(ModEntityTypes.NUTRIENT_SLURRY_COW.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.LEATHER).apply(SetCount.setCount(RandomValueRange.between(0f, 2f)))
						.apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f)))))
				.withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.BEEF).apply(SetCount.setCount(RandomValueRange.between(1f, 3f)))
						.apply(Smelt.smelted().when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
						.apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0f, 1f))))));
	}

	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		List<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		BiomancyMod.LOGGER.info(MarkerManager.getMarker("EntityLootTables"), () -> String.format("generating loot tables for %s entity types...", entityTypes.size()));
		return entityTypes;
	}
}
