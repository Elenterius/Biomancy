package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables
{
    @Override
    protected void addTables() {

        registerDropSelfLootTable(ModBlocks.INFERTILE_SOIL.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_BIG_AND_SMALL.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_PUSTULE_SMALL_GROUP.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_SHROOM_TALL.get());
        registerDropSelfLootTable(ModBlocks.CANDELABRA_FUNGUS.get());
        registerDropSelfLootTable(ModBlocks.LILY_TREE_SAPLING.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_SPROUT_SMALL.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_0.get());
        registerDropSelfLootTable(ModBlocks.BLIGHT_TENTACLE_1.get());
        registerDropSelfLootTable(ModBlocks.LILY_TREE_STEM.get());

        registerLootTable(ModBlocks.BLIGHT_MOSS_SLAB.get(), BlockLootTables::droppingSlab);

        registerLootTable(ModBlocks.LUMINOUS_SOIL.get(), (soil) -> droppingWithSilkTouch(soil, withExplosionDecay(soil, ItemLootEntry.builder(ModItems.LUMINESCENT_SPORES.get())
                .acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> blocks = ModBlocks.BLOCK_REGISTRY.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        BlightlingsMod.LOGGER.info(MarkerManager.getMarker("BockLootTables"), String.format("generating loot tables for %s blocks...", blocks.size()));
        return blocks;
    }
}
