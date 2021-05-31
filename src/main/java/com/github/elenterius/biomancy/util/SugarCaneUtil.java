package com.github.elenterius.biomancy.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public final class SugarCaneUtil {

	public static final int MAX_AGE = BlockPropertyUtil.getMaxAge(SugarCaneBlock.AGE);

	private SugarCaneUtil() {}

	public static boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state) {
		if (state.getBlock() != Blocks.SUGAR_CANE) return false;

		BlockPos upPos = pos.up();
		BlockState upState = worldIn.getBlockState(upPos);
		if (upState.isAir(worldIn, upPos)) {
			int n = getNumOfSugarCaneBlocksBelow(worldIn, pos);
			return n + 1 < 3 && worldIn.getBlockState(pos).get(SugarCaneBlock.AGE) < MAX_AGE;
		}
		else if (upState.getBlock() == Blocks.SUGAR_CANE) {
			int nUp = getNumOfSugarCaneBlocksAbove(worldIn, pos);
			int nDown = getNumOfSugarCaneBlocksBelow(worldIn, pos);
			return nUp + nDown + 1 < 3 & worldIn.getBlockState(pos.up(nUp)).get(SugarCaneBlock.AGE) < MAX_AGE;
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
			int nUp = getNumOfSugarCaneBlocksAbove(worldIn, pos);
			BlockPos topPos = pos.up(nUp);
			BlockState topState = worldIn.getBlockState(topPos);
			grow(worldIn, topPos, topState, rand);
		}
	}

	protected static int getNumOfSugarCaneBlocksAbove(IBlockReader worldIn, BlockPos pos) {
		int i = 0;
		while (i < 3 && worldIn.getBlockState(pos.up(i + 1)).getBlock() == Blocks.SUGAR_CANE) i++;
		return i;
	}

	protected static int getNumOfSugarCaneBlocksBelow(IBlockReader worldIn, BlockPos pos) {
		int i = 0;
		while (i < 3 && worldIn.getBlockState(pos.down(i + 1)).getBlock() == Blocks.SUGAR_CANE) i++;
		return i;
	}

	private static void grow(ServerWorld worldIn, BlockPos pos, BlockState state, Random rand) {
		int age = Math.min(MAX_AGE, state.get(SugarCaneBlock.AGE) + rand.nextInt(3) + 2);
		if (age == MAX_AGE) {
			BlockPos upPos = pos.up();
			worldIn.setBlockState(upPos, Blocks.SUGAR_CANE.getDefaultState());
			worldIn.setBlockState(pos, state.with(SugarCaneBlock.AGE, 0), Constants.BlockFlags.NO_RERENDER);
		}
		else {
			worldIn.setBlockState(pos, state.with(SugarCaneBlock.AGE, age), Constants.BlockFlags.NO_RERENDER);
		}
	}
}
