package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.world.gen.tree.TreeGenerator;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlightSaplingBlock extends BushBlock implements IGrowable
{
    public static final IntegerProperty GROWTH_STAGE = BlockStateProperties.STAGE_0_1;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlightSaplingBlock() {
        super(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        setDefaultState(stateContainer.getBaseState().with(GROWTH_STAGE, 0));
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (worldIn.getLight(pos.up()) >= 9 && random.nextInt(7) == 0) {
            if (!worldIn.isAreaLoaded(pos, 1)) return;
            placeTree(worldIn, pos, state, random);
        }
    }

    public void placeTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
        if (state.get(GROWTH_STAGE) == 0) {
            world.setBlockState(pos, state.func_235896_a_(GROWTH_STAGE), 4);
        }
        else {
            if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(world, rand, pos)) return;
            TreeGenerator.generateLilyTree(world, rand, pos, state);
        }

    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.isValidGround(state, worldIn, pos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return super.isValidPosition(state, worldIn, pos);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return worldIn.rand.nextFloat() < 0.45D;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        placeTree(worldIn, pos, state, rand);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(GROWTH_STAGE);
    }
}
