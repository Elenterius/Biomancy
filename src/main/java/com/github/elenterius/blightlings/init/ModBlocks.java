package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.block.BlightSaplingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModBlocks
{
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<Block> INFERTILE_SOIL = BLOCK_REGISTRY.register("infertile_soil",
            () -> new Block(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));
    public static final RegistryObject<Block> LILY_TREE_SAPLING = BLOCK_REGISTRY.register("lilytree_sapling", BlightSaplingBlock::new);
}
