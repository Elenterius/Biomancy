package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModEntityTypes;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.stream.Collectors;

public class ModEntityLootTables extends EntityLootTables {
	@Override
	protected void addTables() {
		registerLootTable(ModEntityTypes.BLOBLING.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.ERODING_BILE.get()).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.LENS_SHARD.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-2.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));

		registerLootTable(ModEntityTypes.BROOD_MOTHER.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(2))
						.addEntry(ItemLootEntry.builder(ModItems.LENS_SHARD.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-1.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 2.0F))))
						.addEntry(ItemLootEntry.builder(ModItems.TWISTED_HEART.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-3.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))
						.acceptCondition(KilledByPlayer.builder())));

		registerLootTable(ModEntityTypes.BEETLING.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.ERODING_BILE.get()).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.LENS_SHARD.get()).acceptFunction(SetCount.builder(RandomValueRange.of(-2.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));

		registerLootTable(ModEntityTypes.POTION_BEETLE.get(), LootTable.builder());
		registerLootTable(ModEntityTypes.MASON_BEETLE.get(), LootTable.builder());

		registerLootTable(ModEntityTypes.FLESH_BLOB.get(), LootTable.builder()
				.addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ModItems.FLESH_BLOCK.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
	}

	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		List<EntityType<?>> entityTypes = ModEntityTypes.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		BlightlingsMod.LOGGER.info(MarkerManager.getMarker("EntityLootTables"), String.format("generating loot tables for %s entity types...", entityTypes.size()));
		return entityTypes;
	}
}
