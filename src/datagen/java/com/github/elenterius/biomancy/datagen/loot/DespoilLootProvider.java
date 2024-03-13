package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.loot.DespoilLootModifier;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class DespoilLootProvider implements LootTableSubProvider {

	private final Map<ResourceLocation, LootTableBuilder> mobToDespoilLootMap = new HashMap<>();

	protected boolean canHaveLootTable(EntityType<?> entityType) {
		return entityType == EntityType.PLAYER || entityType == EntityType.VILLAGER || entityType.getCategory() != MobCategory.MISC;
	}

	protected void add(EntityType<?> entityType, LootTable.Builder builder) {
		ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		ResourceLocation lootTableId = DespoilLootModifier.getLootTableId(entityType);

		if (canHaveLootTable(entityType)) {
			add(entityTypeId, lootTableId, builder);
		}
		else {
			throw new IllegalStateException(String.format(Locale.ROOT, "Weird despoil loottable '%s' for '%s', not a LivingEntity so should not have despoil loot", lootTableId, entityTypeId));
		}
	}

	protected void add(ResourceLocation entityTypeId, LootTable.Builder builder) {
		ResourceLocation lootTableId = DespoilLootModifier.getLootTableId(entityTypeId);

		mobToDespoilLootMap.put(entityTypeId, new LootTableBuilder(lootTableId, builder));
	}

	private void add(ResourceLocation entityTypeId, ResourceLocation lootTableId, LootTable.Builder builder) {
		mobToDespoilLootMap.put(entityTypeId, new LootTableBuilder(lootTableId, builder));
	}

	public abstract void generate();

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
		generate();

		Set<ResourceLocation> uniqueLootTables = Sets.newHashSet();

		for (Map.Entry<ResourceLocation, LootTableBuilder> entry : mobToDespoilLootMap.entrySet()) {
			LootTableBuilder lootTableBuilder = entry.getValue();
			if (lootTableBuilder != null) {
				if (!uniqueLootTables.add(lootTableBuilder.id)) {
					ResourceLocation entityTypeId = entry.getKey();
					throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate despoil loottable '%s' for '%s'", lootTableBuilder.id, entityTypeId));
				}
				else {
					output.accept(lootTableBuilder.id, lootTableBuilder.builder);
				}
			}
		}

		mobToDespoilLootMap.clear();
	}

	record LootTableBuilder(ResourceLocation id, LootTable.Builder builder) {}

}
