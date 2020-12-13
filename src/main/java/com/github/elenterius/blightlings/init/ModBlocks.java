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

public abstract class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlightlingsMod.MOD_ID);
    public static final PlantType BLIGHT_PLANT_TYPE = PlantType.get("blight");

    public static final RegistryObject<Block> INFERTILE_SOIL = BLOCKS.register("infertile_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.GROUND)));
    public static final RegistryObject<Block> LUMINOUS_SOIL = BLOCKS.register("luminous_soil",
            () -> new BlightSoilBlock(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).hardnessAndResistance(0.5F).sound(SoundType.GROUND)));

    public static final RegistryObject<Block> BLIGHT_PUSTULE_SMALL = BLOCKS.register("blight_pustule_0", () -> new BlightPustuleBlock(glowingPlantProperties(3)));
    public static final RegistryObject<Block> BLIGHT_PUSTULE_BIG = BLOCKS.register("blight_pustule_1", () -> new BlightPustuleBlock(glowingPlantProperties(9)));
    public static final RegistryObject<Block> BLIGHT_PUSTULE_BIG_AND_SMALL = BLOCKS.register("blight_pustule_2", () -> new BlightPustuleBlock(glowingPlantProperties(12)));
    public static final RegistryObject<Block> BLIGHT_PUSTULE_SMALL_GROUP = BLOCKS.register("blight_pustule_3", () -> new BlightPustuleBlock(glowingPlantProperties(7)));

    public static final RegistryObject<Block> BLIGHT_SHROOM_TALL = BLOCKS.register("blight_shroom_tall",
            () -> new BlightPlantBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT)) {
                @Override
                public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
                    return worldIn.getLightSubtracted(pos, 0) < 13 && super.isValidPosition(state, worldIn, pos);
                }
            });
    public static final RegistryObject<Block> CANDELABRA_FUNGUS = BLOCKS.register("candelabra_fungus", () -> new BlightPlantBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.PLANT)));

    public static final RegistryObject<Block> BLIGHT_SPROUT = BLOCKS.register("blight_sprout", () -> new BlightPlantBlock(true, blighPlantProperties()));
    public static final RegistryObject<Block> BLIGHT_SPROUT_SMALL = BLOCKS.register("blight_sprout_small", () -> new BlightPlantBlock(true, blighPlantProperties()));
    public static final RegistryObject<Block> BLIGHT_TENTACLE_0 = BLOCKS.register("blight_tentacle_0", () -> new BlightPlantBlock(blighPlantProperties()));
    public static final RegistryObject<Block> BLIGHT_TENTACLE_1 = BLOCKS.register("blight_tentacle_1", () -> new BlightPlantBlock(blighPlantProperties()));

    public static final RegistryObject<Block> LILY_TREE_SAPLING = BLOCKS.register("lilytree_sapling", () -> new BlightSaplingBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)));
    public static final RegistryObject<Block> LILY_TREE_STEM = BLOCKS.register("lilytree_stem", () -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(0.4F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.PLANT)));
    public static final RegistryObject<Block> BLIGHT_MOSS_SLAB = BLOCKS.register("blight_moss_slab", () -> new SlabBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.4F).setAllowsSpawn(ModBlocks::canEntitySpawn).sound(SoundType.PLANT)));

    public static Block.Properties glowingPlantProperties(int i) {
        return Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.PLANT).setLightLevel(v -> i);
    }

    public static AbstractBlock.Properties blighPlantProperties() {
        return Block.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().setAllowsSpawn(ModBlocks::canEntitySpawn).hardnessAndResistance(0.0F).sound(SoundType.PLANT);
    }

    public static boolean canEntitySpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
        return state.isSolidSide(reader, pos, Direction.UP);
    }
}
