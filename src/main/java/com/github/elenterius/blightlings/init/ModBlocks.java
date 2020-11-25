package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.block.BlightPlantBlock;
import com.github.elenterius.blightlings.block.BlightPustuleBlock;
import com.github.elenterius.blightlings.block.BlightSaplingBlock;
import com.github.elenterius.blightlings.block.BlightSoilBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModBlocks
{
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BlightlingsMod.MOD_ID);
    public static final PlantType BLIGHT_PLANT_TYPE = PlantType.get("blight");

    private static boolean canEntitySpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
        return state.isSolidSide(reader, pos, Direction.UP);
    }

    public static final RegistryObject<Block> INFERTILE_SOIL = BLOCK_REGISTRY.register("infertile_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.GROUND)));
    public static final RegistryObject<Block> LUMINOUS_SOIL = BLOCK_REGISTRY.register("luminous_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));

    public static final RegistryObject<Block> BLIGHT_PUSTULE = BLOCK_REGISTRY.register("blight_pustule", BlightPustuleBlock::new);

    public static final RegistryObject<Block> BLIGHT_SHROOM_TALL = BLOCK_REGISTRY.register("blight_shroom_tall",
            () -> new BlightPlantBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
            {
                @Override
                public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
                    return worldIn.getLightSubtracted(pos, 0) < 13 && super.isValidPosition(state, worldIn, pos);
                }
            });

    public static final RegistryObject<Block> LILY_TREE_SAPLING = BLOCK_REGISTRY.register("lilytree_sapling", BlightSaplingBlock::new);

    public static final RegistryObject<Block> BLIGHT_SPROUT = BLOCK_REGISTRY.register("blight_sprout", () -> new BlightPlantBlock(true));
    public static final RegistryObject<Block> BLIGHT_SPROUT_SMALL = BLOCK_REGISTRY.register("blight_sprout_small", () -> new BlightPlantBlock(true));
    public static final RegistryObject<Block> BLIGHT_TENTACLE_0 = BLOCK_REGISTRY.register("blight_tentacle_0", BlightPlantBlock::new);
    public static final RegistryObject<Block> BLIGHT_TENTACLE_1 = BLOCK_REGISTRY.register("blight_tentacle_1", BlightPlantBlock::new);

    public static final RegistryObject<Block> BLIGHT_MOSS_SLAB = BLOCK_REGISTRY.register("blight_moss_slab",
            () -> new SlabBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.4F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.PLANT)));

    public static final RegistryObject<Block> LILY_TREE_STEM = BLOCK_REGISTRY.register("lilytree_stem",
            () -> new RotatedPillarBlock(Block.Properties.create(Material.PLANTS, MaterialColor.DIRT).hardnessAndResistance(0.4F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.PLANT)));
}
