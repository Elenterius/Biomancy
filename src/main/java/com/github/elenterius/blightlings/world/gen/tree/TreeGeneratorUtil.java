package com.github.elenterius.blightlings.world.gen.tree;

import com.github.elenterius.blightlings.init.ModBlocks;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class TreeGeneratorUtil
{
    public static void generateLilyTree(IWorld world, Random rand, BlockPos pos, BlockState state) {
        List<Direction> cardinalDirections = Lists.newArrayList(Direction.Plane.HORIZONTAL);
        Collections.shuffle(cardinalDirections, rand);

        generateLilyTree(world, rand, pos, state, cardinalDirections, 0);
    }

    private static void generateLilyTree(IWorld world, Random rand, BlockPos pos, BlockState state, List<Direction> cardinalDirections, int depth) {
        if (depth >= cardinalDirections.size() + 1) return;

        BlockPos.Mutable currPos = pos.toMutable();

        BlockState slabState = ModBlocks.BLIGHT_MOSS_SLAB.get().getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
        BlockState stemHorizontalState = ModBlocks.LILY_TREE_STEM.get().getDefaultState();
        BlockState stemVerticalState = ModBlocks.LILY_TREE_STEM.get().getDefaultState();
        BlockState sporeInfestedState = ModBlocks.LUMINOUS_SOIL.get().getDefaultState();

        int baseHeight = rand.nextInt(2) + 1;
        for (int j = 0; j < baseHeight; ++j) {
            world.setBlockState(currPos, stemVerticalState, Constants.BlockFlags.BLOCK_UPDATE);
            currPos.move(Direction.UP);
        }
        world.setBlockState(currPos, stemVerticalState, Constants.BlockFlags.BLOCK_UPDATE); //corner

        BlockPos forkPos = currPos.toImmutable();
        BlockPos endPos = currPos.toImmutable();

        Direction branchDirection = cardinalDirections.get(depth);

        currPos.setPos(forkPos);
        currPos.move(branchDirection);
        int maxLength = rand.nextInt(5) + 3;
        int horizontalSteps = 0;
        Direction currDirection = branchDirection;
        for (int i = 0; i < maxLength && tryToSetBlockState(world, currPos, stemHorizontalState.with(BlockStateProperties.AXIS, currDirection.getAxis()), Constants.BlockFlags.BLOCK_UPDATE); i++) {
            currPos.move(currDirection);
            horizontalSteps++;
            if (horizontalSteps >= 2 && rand.nextFloat() < 0.5F) {
                int maxHeight = rand.nextInt(2) + 2;
                for (int j = 0; j < maxHeight && tryToSetBlockState(world, currPos, stemVerticalState, Constants.BlockFlags.BLOCK_UPDATE); j++) {
                    currPos.move(Direction.UP);
                    if (j == 0 && i >= maxLength * 0.45f || (maxHeight > 2 && rand.nextFloat() < 0.4F)) {
                        endPos = currPos.toImmutable();
                        if (maxHeight > 2 && rand.nextFloat() < 0.44F) { //generate disc shape
                            tryToGenerateDiscShape(world, currPos, slabState, Constants.BlockFlags.BLOCK_UPDATE);
                        }
                        else { //generate small ring
                            for (Direction dir : cardinalDirections) {
                                currPos.setPos(endPos);
                                currPos.move(dir);
                                tryToSetBlockState(world, currPos, slabState, Constants.BlockFlags.BLOCK_UPDATE);
                            }
                        }
                        currPos.setPos(endPos);
                    }
                }
                horizontalSteps = 0;
                currDirection = currDirection.getOpposite();
            }
            endPos = currPos.toImmutable();
        }

        //we have reached the top/end
        world.setBlockState(endPos, sporeInfestedState, Constants.BlockFlags.BLOCK_UPDATE);
        endPos = endPos.up();
        world.setBlockState(endPos, sporeInfestedState, Constants.BlockFlags.BLOCK_UPDATE);
        tryToGenerateDiscShape(world, endPos, slabState, Constants.BlockFlags.BLOCK_UPDATE); //generate disc shape

        //decide if we want to generate a second branch starting from the fork position
        if (depth == 0) {
            currPos.setPos(forkPos);
            currPos.move(Direction.UP, 2);
            boolean isEmpty2Up = world.getBlockState(currPos) == Blocks.AIR.getDefaultState() && world.getBlockState(currPos.move(branchDirection)) == Blocks.AIR.getDefaultState();
            currPos.move(Direction.UP);
            boolean isEmpty3Up = world.getBlockState(currPos) == Blocks.AIR.getDefaultState() && world.getBlockState(currPos.move(branchDirection.getOpposite())) == Blocks.AIR.getDefaultState();
            if (isEmpty2Up && isEmpty3Up && rand.nextFloat() < 0.6f) {
                generateLilyTree(world, rand, forkPos, state, cardinalDirections, 1);
            }
        }
    }

    public static boolean tryToSetBlockState(IWorld world, BlockPos pos, BlockState state, int updateFlag) {
        return world.getBlockState(pos) == Blocks.AIR.getDefaultState() && world.setBlockState(pos, state, updateFlag);
    }

    public static void tryToGenerateDiscShape(IWorld world, BlockPos pos, BlockState state, int updateFlag) {
        BlockPos.Mutable currPos = pos.toMutable();
        currPos.move(1, 0, 0);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(0, 0, 1);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(-1, 0, 0);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(-1, 0, 0);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(0, 0, -1);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(0, 0, -1);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(1, 0, 0);
        tryToSetBlockState(world, currPos, state, updateFlag);
        currPos.move(1, 0, 0);
        tryToSetBlockState(world, currPos, state, updateFlag);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            currPos.setPos(pos);
            currPos.move(dir, 2);
            tryToSetBlockState(world, currPos, state, updateFlag);
            currPos.move(dir.rotateY());
            tryToSetBlockState(world, currPos, state, updateFlag);
            currPos.move(dir.rotateY().getOpposite(), 2);
            tryToSetBlockState(world, currPos, state, updateFlag);
        }
    }
}
