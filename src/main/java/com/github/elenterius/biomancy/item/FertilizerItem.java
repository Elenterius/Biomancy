package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.property.BlockPropertyUtil;
import com.github.elenterius.biomancy.util.PillarPlantUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.IPlantable;

import java.util.Optional;

public class FertilizerItem extends SimpleItem {

	public FertilizerItem(Properties properties) {
		super(properties);
	}

	public static boolean applyFertilizer(ItemStack stack, Level level, BlockPos pos, Direction clickedFace) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof BonemealableBlock bonemealableBlock) {
			return growBonmealableBlock(level, pos, state, bonemealableBlock);
		}
		else if (state.isFaceSturdy(level, pos, clickedFace) && BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, pos.relative(clickedFace), clickedFace)) {
			return true;
		}
		else if (block == Blocks.DIRT) {
			return growDirtAreaIntoGrassBlocks(level, pos);
		}
		else if (block == Blocks.MYCELIUM) {
			return growDirtAndGrassAreaIntoMycelium(level, pos);
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

	private static boolean growDirtAreaIntoGrassBlocks(Level level, BlockPos startPos) {
		if (!level.getBlockState(startPos.above()).isAir()) return false;

		if (level instanceof ServerLevel serverLevel) {
			serverLevel.setBlockAndUpdate(startPos, Blocks.GRASS_BLOCK.defaultBlockState());
			serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, startPos.getX() + 0.5d, startPos.getY() + 1.25d, startPos.getZ() + 0.5d, 5, 0.25, 0.25, 0.25, 0);

			BlockPos.breadthFirstTraversal(startPos, 10, 20,
					(pos, queue) -> {
						queue.accept(pos.offset(-1, 1, 0));
						queue.accept(pos.offset(-1, 0, 0));
						queue.accept(pos.offset(-1, -1, 0));
						queue.accept(pos.offset(1, 1, 0));
						queue.accept(pos.offset(1, 0, 0));
						queue.accept(pos.offset(1, -1, 0));
						queue.accept(pos.offset(0, 1, 1));
						queue.accept(pos.offset(0, 0, 1));
						queue.accept(pos.offset(0, -1, 1));
						queue.accept(pos.offset(0, 1, -1));
						queue.accept(pos.offset(0, 0, -1));
						queue.accept(pos.offset(0, -1, -1));
					},
					pos -> {
						if (pos.equals(startPos)) return true;

						BlockState currentState = serverLevel.getBlockState(pos);
						boolean isAirAbove = serverLevel.getBlockState(pos.above()).isAir();

						if (currentState.is(Blocks.DIRT) && isAirAbove) {
							serverLevel.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
							serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 1.25d, pos.getZ() + 0.5d, 5, 0.25, 0.25, 0.25, 0);
							return true;
						}

						return (currentState.is(BlockTags.DIRT) || currentState.is(Blocks.GRASS_BLOCK)) && isAirAbove;
					}
			);
		}

		return true;
	}

	private static boolean growDirtAndGrassAreaIntoMycelium(Level level, BlockPos startPos) {
		if (!level.getBlockState(startPos.above()).isAir()) return false;

		if (level instanceof ServerLevel serverLevel) {
			int visited = BlockPos.breadthFirstTraversal(startPos, 10, 20,
					(pos, queue) -> {
						queue.accept(pos.offset(-1, 1, 0));
						queue.accept(pos.offset(-1, 0, 0));
						queue.accept(pos.offset(-1, -1, 0));
						queue.accept(pos.offset(1, 1, 0));
						queue.accept(pos.offset(1, 0, 0));
						queue.accept(pos.offset(1, -1, 0));
						queue.accept(pos.offset(0, 1, 1));
						queue.accept(pos.offset(0, 0, 1));
						queue.accept(pos.offset(0, -1, 1));
						queue.accept(pos.offset(0, 1, -1));
						queue.accept(pos.offset(0, 0, -1));
						queue.accept(pos.offset(0, -1, -1));
					},
					pos -> {
						if (pos.equals(startPos)) return true;

						BlockState currentState = serverLevel.getBlockState(pos);
						boolean isAirAbove = serverLevel.getBlockState(pos.above()).isAir();

						if ((currentState.is(Blocks.DIRT) || currentState.is(Blocks.GRASS_BLOCK)) && isAirAbove) {
							serverLevel.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState());
							serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 1.25d, pos.getZ() + 0.5d, 5, 0.25, 0.25, 0.25, 0);
							return true;
						}

						return (currentState.is(BlockTags.DIRT) || currentState.is(Blocks.MYCELIUM)) && isAirAbove;
					}
			);
			return visited > 1;
		}

		return true;
	}

	private static boolean growPlantableBlock(Level level, BlockPos pos, BlockState state, Block block) {
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxValue(ageProperty);
			if (age < maxAge) {
				if (!level.isClientSide()) {
					level.setBlock(pos, state.setValue(ageProperty, maxAge), Block.UPDATE_CLIENTS);
					level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
				}
				return true;
			}
		}
		else if (block.isRandomlyTicking(state)) {
			if (!level.isClientSide() && !level.getBlockTicks().willTickThisTick(pos, block)) {
				level.scheduleTick(pos, block, 2);
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
			return true;
		}

		return false;
	}

	private static boolean growBonmealableBlock(Level level, BlockPos pos, BlockState state, BonemealableBlock block) {
		if (!block.isValidBonemealTarget(level, pos, state, level.isClientSide)) return false;
		if (!(level instanceof ServerLevel serverLevel)) return false;

		final BlockState prevState = state;

		if (state.is(BlockTags.SAPLINGS) && state.hasProperty(BlockStateProperties.STAGE)) {
			state = state.setValue(BlockStateProperties.STAGE, 1);
		}

		// "power" grow plant with age property to maturity - i.e. crops, etc.
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxValue(ageProperty);
			if (age < maxAge) {
				state = state.setValue(ageProperty, maxAge);
			}
		}

		if (state != prevState) {
			serverLevel.setBlock(pos, state, Block.UPDATE_CLIENTS);
		}
		block.performBonemeal(serverLevel, serverLevel.random, pos, state);

		serverLevel.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);

		return true;
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
		if (applyFertilizer(stack, level, clickedPos, context.getClickedFace())) {
			if (!level.isClientSide) {
				stack.shrink(1);
				level.playSound(null, clickedPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1f, 1f);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

}
