package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModBlocks;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider
{
//    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(
//            Pair.of(FishingLootTables::new, LootParameterSets.FISHING),
//            Pair.of(ChestLootTables::new, LootParameterSets.CHEST),
//            Pair.of(EntityLootTables::new, LootParameterSets.ENTITY),
//            Pair.of(BlockLootTables::new, LootParameterSets.BLOCK),
//            Pair.of(PiglinBarteringAddition::new, LootParameterSets.field_237453_h_),
//            Pair.of(GiftLootTables::new, LootParameterSets.GIFT));

    public ModLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    private void addTables(Map<ResourceLocation, LootTable.Builder> lootTables) {
        addDefaultTable(lootTables, ModBlocks.INFERTILE_SOIL.get());
        addDefaultTable(lootTables, ModBlocks.LUMINOUS_SOIL.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_PUSTULE.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_SHROOM_TALL.get());
        addDefaultTable(lootTables, ModBlocks.LILY_TREE_SAPLING.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_SPROUT.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_SPROUT_SMALL.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_TENTACLE_0.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_TENTACLE_1.get());
        addDefaultTable(lootTables, ModBlocks.BLIGHT_MOSS_SLAB.get());
        addDefaultTable(lootTables, ModBlocks.LILY_TREE_STEM.get());
        BlightlingsMod.LOGGER.info(String.format("added loot tables for %s blocks", lootTables.size()));
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(Pair.of(() -> resourceLocationBuilderBiConsumer -> {
            Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();
            addTables(map);
            map.forEach(resourceLocationBuilderBiConsumer);
        }, LootParameterSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        // do nothing
    }

    @Override
    public String getName() {
        return "BlightlingsMod " + super.getName();
    }

    protected void addDefaultTable(Map<ResourceLocation, LootTable.Builder> lootTables, Block block) {
        //noinspection ConstantConditions
        lootTables.put(new ResourceLocation(block.getRegistryName().getNamespace(), "blocks/" + block.getRegistryName().getPath()), createDefaultTable(block));
    }

    protected LootTable.Builder createDefaultTable(Block block) {
        //noinspection ConstantConditions
        return createDefaultTable(block.getRegistryName().getPath(), block);
    }

    protected LootTable.Builder createDefaultTable(String name, Block block) {
        LootPool.Builder builder = LootPool.builder()
//                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(builder);
    }
}