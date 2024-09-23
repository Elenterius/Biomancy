package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.property.BlockPropertyUtil;
import com.github.elenterius.biomancy.util.PillarPlantUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.IPlantable;

import java.util.Optional;

public class FertilizerItem extends SimpleItem {

	public FertilizerItem(Properties properties) {
		super(properties);
	}

	public static boolean applyFertilizer(ItemStack stack, Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof BonemealableBlock bonemealableBlock) {
			return growBonmealableBlock(level, pos, state, bonemealableBlock);
		}
		else if (block == Blocks.DIRT) {
			return growDirtAreaIntoGrassBlocks(level, pos);
		}
		else if (block instanceof ChorusFlowerBlock) {
			return growChorusFlower(level, pos, state);
		}
		else if (PillarPlantUtil.isPillarPlant(block)) {
			return PillarPlantUtil.applyMegaGrowthBoost(level, pos, state, block);
		}
		else if (block instanceof IPlantable) { //e.g. nether wart
			return growPlantableBlock(level, pos, state, block);
		}

		return false;
	}

	private static boolean growChorusFlower(Level level, BlockPos pos, BlockState state) {
		if (state.getValue(ChorusFlowerBlock.AGE) >= 5) return false;

		BlockState stateBelow = level.getBlockState(pos.below());

		if (stateBelow.is(Blocks.END_STONE)) {
			if (!level.isClientSide()) {
				ChorusFlowerBlock.generatePlant(level, pos, level.random, 8);
			}
			return true;
		}

		boolean isAttachedToChorusPlant = stateBelow.is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.north()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.south()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.west()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.east()).is(Blocks.CHORUS_PLANT);

		if (isAttachedToChorusPlant) {
			if (!level.isClientSide()) {
				ChorusFlowerBlock.generatePlant(level, pos, level.random, 8);
			}
			return true;
		}

		return false;
	}

	private static boolean growDirtAreaIntoGrassBlocks(Level level, BlockPos pos) {
		if (!level.getBlockState(pos.above()).isAir()) return false;

		if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
			level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
			serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 1.25d, pos.getZ() + 0.5d, 5, 0.25, 0.25, 0.25, 0);

			BlockPos.breadthFirstTraversal(pos, 10, 20,
					(blockPos, queue) -> {
						queue.accept(blockPos.offset(-1, 1, 0));
						queue.accept(blockPos.offset(-1, 0, 0));
						queue.accept(blockPos.offset(-1, -1, 0));
						queue.accept(blockPos.offset(1, 1, 0));
						queue.accept(blockPos.offset(1, 0, 0));
						queue.accept(blockPos.offset(1, -1, 0));
						queue.accept(blockPos.offset(0, 1, 1));
						queue.accept(blockPos.offset(0, 0, 1));
						queue.accept(blockPos.offset(0, -1, 1));
						queue.accept(blockPos.offset(0, 1, -1));
						queue.accept(blockPos.offset(0, 0, -1));
						queue.accept(blockPos.offset(0, -1, -1));
					},
					blockPos -> {
						if (blockPos.equals(pos)) return true;

						BlockState state = level.getBlockState(blockPos);
						boolean isAirAbove = level.getBlockState(blockPos.above()).isAir();

						if (state.is(Blocks.DIRT) && isAirAbove) {
							level.setBlockAndUpdate(blockPos, Blocks.GRASS_BLOCK.defaultBlockState());
							serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, blockPos.getX() + 0.5d, blockPos.getY() + 1.25d, blockPos.getZ() + 0.5d, 5, 0.25, 0.25, 0.25, 0);
							return true;
						}

						return state.is(BlockTags.DIRT) && isAirAbove;
					}
			);
		}

		return true;
	}

	private static boolean growPlantableBlock(Level level, BlockPos pos, BlockState state, Block block) {
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
			if (age < maxAge) {
				if (!level.isClientSide()) {
					level.setBlock(pos, state.setValue(ageProperty, maxAge), Block.UPDATE_CLIENTS);
					level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
				}
				return true;
			}
		}
		else if (block.isRandomlyTicking(state) && !level.getBlockTicks().willTickThisTick(pos, block)) {
			if (!level.isClientSide()) {
				level.scheduleTick(pos, block, 2);
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
			return true;
		}

		return false;
	}

	private static boolean growBonmealableBlock(Level level, BlockPos pos, BlockState state, BonemealableBlock block) {
		if (!block.isValidBonemealTarget(level, pos, state, level.isClientSide)) return false;

		// "power" grow plant to maturity
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
			if (age < maxAge) {
				if (!level.isClientSide()) {
					level.setBlock(pos, state.setValue(ageProperty, maxAge), Block.UPDATE_CLIENTS);
					level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
				}
				return true;
			}
		}
		else {
			if (level instanceof ServerLevel serverLevel) {
				block.performBonemeal(serverLevel, level.random, pos, state); //fall back
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		if (applyFertilizer(stack, level, clickedPos)) {
			if (!level.isClientSide) {
				stack.shrink(1);
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, clickedPos, 0);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

}
