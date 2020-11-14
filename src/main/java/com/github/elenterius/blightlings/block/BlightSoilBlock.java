package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class BlightSoilBlock extends Block
{
    public BlightSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        BlockState plant = plantable.getPlant(world, pos.offset(facing));
        PlantType type = plantable.getPlantType(world, pos.offset(facing));
        return type == ModBlocks.BLIGHT_PLANT_TYPE;
    }
}
