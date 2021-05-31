package com.github.elenterius.biomancy.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public final class CactusUtil {

	public static final int MAX_AGE = BlockPropertyUtil.getMaxAge(CactusBlock.AGE);

	private CactusUtil() {}

	public static boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state) {
		if (state.getBlock() != Blocks.CACTUS) return false;

		BlockPos upPos = pos.up();
		BlockState upState = worldIn.getBlockState(upPos);
		if (upState.isAir(worldIn, upPos)) {
			int n = getNumOfCactusBlocksBelow(worldIn, pos);
			return n + 1 < 3 && worldIn.getBlockState(pos).get(CactusBlock.AGE) < MAX_AGE;
		}
		else if (upState.getBlock() == Blocks.CACTUS) {
			int nUp = getNumOfCactusBlocksAbove(worldIn, pos);
			int nDown = getNumOfCactusBlocksBelow(worldIn, pos);
			return nUp + nDown + 1 < 3 & worldIn.getBlockState(pos.up(nUp)).get(CactusBlock.AGE) < MAX_AGE;
		}
		return false;
	}

	public static void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		BlockPos upPos = pos.up();
		BlockState upState = worldIn.getBlockState(upPos);
		if (upState.isAir(worldIn, upPos)) {
			grow(worldIn, pos, state, rand);
		}
		else {
			int nUp = getNumOfCactusBlocksAbove(worldIn, pos);
			BlockPos topPos = pos.up(nUp);
			BlockState topState = worldIn.getBlockState(topPos);
			grow(worldIn, topPos, topState, rand);
		}
	}

	protected static int getNumOfCactusBlocksAbove(IBlockReader worldIn, BlockPos pos) {
		int i = 0;
		while (i < 3 && worldIn.getBlockState(pos.up(i + 1)).getBlock() == Blocks.CACTUS) i++;
		return i;
	}

	protected static int getNumOfCactusBlocksBelow(IBlockReader worldIn, BlockPos pos) {
		int i = 0;
		while (i < 3 && worldIn.getBlockState(pos.down(i + 1)).getBlock() == Blocks.CACTUS) i++;
		return i;
	}

	private static void grow(ServerWorld worldIn, BlockPos pos, BlockState state, Random rand) {
		int age = Math.min(MAX_AGE, state.get(CactusBlock.AGE) + rand.nextInt(3) + 2);
		if (age == MAX_AGE) {
			BlockPos upPos = pos.up();
			worldIn.setBlockState(upPos, Blocks.CACTUS.getDefaultState());

			BlockState newState = state.with(CactusBlock.AGE, 0);
			worldIn.setBlockState(pos, newState, Constants.BlockFlags.NO_RERENDER);
			newState.neighborChanged(worldIn, upPos, Blocks.CACTUS, pos, false);
		}
		else {
			worldIn.setBlockState(pos, state.with(CactusBlock.AGE, age), Constants.BlockFlags.NO_RERENDER);
		}
	}
}
