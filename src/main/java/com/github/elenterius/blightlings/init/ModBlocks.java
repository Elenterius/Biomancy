package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.block.BlightPlantBlock;
import com.github.elenterius.blightlings.block.BlightPustuleBlock;
import com.github.elenterius.blightlings.block.BlightSaplingBlock;
import com.github.elenterius.blightlings.block.BlightSoilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModBlocks
{
    public static final PlantType BLIGHT_PLANT_TYPE = PlantType.get("blight");

    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<Block> INFERTILE_SOIL = BLOCK_REGISTRY.register("infertile_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));


    public static final RegistryObject<Block> LILY_TREE_SAPLING = BLOCK_REGISTRY.register("lilytree_sapling", BlightSaplingBlock::new);

    public static final RegistryObject<Block> LUMINOUS_SOIL = BLOCK_REGISTRY.register("luminous_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));

    public static final RegistryObject<Block> BLIGHT_PUSTULE = BLOCK_REGISTRY.register("blight_pustule", BlightPustuleBlock::new);

    public static final RegistryObject<Block> BLIGHT_SPROUT = BLOCK_REGISTRY.register("blight_sprout", () -> new BlightPlantBlock(true));
    public static final RegistryObject<Block> BLIGHT_SPROUT_SMALL = BLOCK_REGISTRY.register("blight_sprout_small", () -> new BlightPlantBlock(true));
    public static final RegistryObject<Block> BLIGHT_TENTACLE_0 = BLOCK_REGISTRY.register("blight_tentacle_0", BlightPlantBlock::new);
    public static final RegistryObject<Block> BLIGHT_TENTACLE_1 = BLOCK_REGISTRY.register("blight_tentacle_1", BlightPlantBlock::new);
    public static final RegistryObject<Block> BLIGHT_SHROOM_TALL = BLOCK_REGISTRY.register("blight_shroom_tall", BlightPlantBlock::new);

    public static final RegistryObject<Block> BLIGHT_MOSS_SLAB = BLOCK_REGISTRY.register("blight_moss_slab",
            () -> new SlabBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.4F).sound(SoundType.GROUND)));
}
