package com.github.elenterius.biomancy.datagen;

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
		return LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(rolls)).addEntry(ItemLootEntry.builder(ModItems.FLESH_BLOCK.get())))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(ModEntityTypes.FLESH_BLOB.get().getLootTable())));
	}

	@Override
	protected void addTables() {
		registerLootTable(ModEntityTypes.BOOMLING.get(), LootTable.builder()); //no item drops

		registerLootTable(ModEntityTypes.BROOD_MOTHER.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(2))
						.addEntry(ItemLootEntry.builder(ModItems.MENISCUS_LENS.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-1.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 2.0F))))
						.acceptCondition(KilledByPlayer.builder())));

		registerLootTable(ModEntityTypes.BEETLING.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.ERODING_BILE.get()).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.MENISCUS_LENS.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-2.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));

		registerLootTable(ModEntityTypes.FLESHKIN.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.FLESH_BLOCK.get())
						.acceptFunction(SetCount.builder(ConstantRange.of(1))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
		);

		registerLootTable(ModEntityTypes.FLESH_BLOB.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.FLESH_BLOCK.get())
						.acceptFunction(SetCount.builder(ConstantRange.of(1))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(ModItems.OCULUS.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-3f, 1f))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))
						.acceptCondition(KilledByPlayer.builder()))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(ModItems.STOMACH.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-3f, 1f))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
		);
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_2, fleshBlobLootTableBuilderWithDrop(1));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_3, fleshBlobLootTableBuilderWithDrop(2));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_4, fleshBlobLootTableBuilderWithDrop(3));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_5, fleshBlobLootTableBuilderWithDrop(4));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_6, fleshBlobLootTableBuilderWithDrop(5));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_7, fleshBlobLootTableBuilderWithDrop(6));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_8, fleshBlobLootTableBuilderWithDrop(7));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_9, fleshBlobLootTableBuilderWithDrop(8));
		registerLootTable(FleshBlobEntity.LOOT_TABLE_FOR_SIZE_10, fleshBlobLootTableBuilderWithDrop(9));

		registerLootTable(ModEntityTypes.OCULUS_OBSERVER.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(ModItems.OCULUS.get()).acceptFunction(SetCount.builder(RandomValueRange.of(0f, 1f))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))
						.acceptCondition(KilledByPlayer.builder()))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(ModItems.STOMACH.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-2f, 1f))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
		);

		registerLootTable(ModEntityTypes.FAILED_SHEEP.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.FLESH_LUMP.get()).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.SKIN_CHUNK.get()).acceptFunction(SetCount.builder(RandomValueRange.of(2, 4))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));
		registerLootTable(ModEntityTypes.CHROMA_SHEEP.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MUTTON).acceptFunction(SetCount.builder(RandomValueRange.of(1, 2))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));
		registerLootTable(ModEntityTypes.THICK_WOOL_SHEEP.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MUTTON).acceptFunction(SetCount.builder(RandomValueRange.of(1, 2))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));

		registerLootTable(ModEntityTypes.SILKY_WOOL_SHEEP.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MUTTON).acceptFunction(SetCount.builder(RandomValueRange.of(2, 3))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));
		registerLootTable(SilkyWoolSheepEntity.SILKY_WOOL_LOOT_TABLE, LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0, 3)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(ModEntityTypes.SILKY_WOOL_SHEEP.get().getLootTable()))));

		registerLootTable(ModEntityTypes.FAILED_COW.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.FLESH_LUMP.get()).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.SKIN_CHUNK.get()).acceptFunction(SetCount.builder(RandomValueRange.of(2, 4))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));
		registerLootTable(ModEntityTypes.NUTRIENT_SLURRY_COW.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0f, 2f)))
						.acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BEEF).acceptFunction(SetCount.builder(RandomValueRange.of(1f, 3f)))
						.acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE)))
						.acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0f, 1f))))));
	}

	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		List<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		BiomancyMod.LOGGER.info(MarkerManager.getMarker("EntityLootTables"), () -> String.format("generating loot tables for %s entity types...", entityTypes.size()));
		return entityTypes;
	}
}
