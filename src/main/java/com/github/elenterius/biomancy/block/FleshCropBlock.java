package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class FleshCropBlock extends CropsBlock {

	public FleshCropBlock(Properties builder) {
		super(builder);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (!worldIn.isAreaLoaded(pos, 1)) return;
		BlockPos downPos = pos.below();
		if (worldIn.getBlockState(downPos).is(ModBlocks.FLESH_BLOCK.get()) && worldIn.getRawBrightness(pos, 0) >= 8) {
			int age = getAge(state);
			if (age < getMaxAge()) {
				if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25f / getGrowthSpeed(this, worldIn, pos)) + 1) == 0)) {
					if (age + 1 == getMaxAge()) {
						worldIn.setBlock(downPos, ModBlocks.NECROTIC_FLESH_BLOCK.get().defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
					}
					worldIn.setBlock(pos, getStateForAge(age + 1), Constants.BlockFlags.BLOCK_UPDATE);
					ForgeHooks.onCropsGrowPost(worldIn, pos, state);
				}
			}
		}
	}

	@Override
	public void growCrops(World worldIn, BlockPos pos, BlockState state) {
		int newAge = MathHelper.clamp(getAge(state) + getBonemealAgeIncrease(worldIn), 0, getMaxAge());
		if (newAge == getMaxAge()) {
			BlockPos downPos = pos.below();
			if (worldIn.getBlockState(downPos).is(ModBlocks.FLESH_BLOCK.get())) {
				worldIn.setBlock(downPos, ModBlocks.NECROTIC_FLESH_BLOCK.get().defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
			}
		}

		worldIn.setBlock(pos, getStateForAge(newAge), Constants.BlockFlags.BLOCK_UPDATE);
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return worldIn.getBlockState(pos.below()).is(ModBlocks.FLESH_BLOCK.get()) && super.isValidBonemealTarget(worldIn, pos, state, isClient);
	}

	@Override
	public int getAge(BlockState state) {
		return super.getAge(state);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.is(ModBlocks.FLESH_BLOCK.get()) || state.is(ModBlocks.NECROTIC_FLESH_BLOCK.get());
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return ModBlocks.FLESH_PLANT_TYPE;
	}

}
