package com.github.elenterius.biomancy.fluid;

import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Map;

public abstract class AcidFluid extends ForgeFlowingFluid {

	protected static final Map<Block, BlockState> NORMAL_TO_ERODED_BLOCK_CONVERSION = Map.of(
			Blocks.GRASS_BLOCK, Blocks.DIRT.defaultBlockState(),
			Blocks.COBBLESTONE, Blocks.GRAVEL.defaultBlockState(),
			Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(),
			Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(),
			Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState(),
			Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(),
			Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS.defaultBlockState()
	);

	protected AcidFluid(Properties properties) {
		super(properties);
	}

	public static void onEntityInside(LivingEntity livingEntity) {
		if (livingEntity.isSpectator()) return;
		if (livingEntity.tickCount % 5 != 0) return;
		if (!livingEntity.isInFluidType(ModFluids.ACID_TYPE.get())) return;

		if (!livingEntity.level().isClientSide) {
			CombatUtil.applyAcidEffect(livingEntity, 4);
		}
		else if (livingEntity.tickCount % 10 == 0 && livingEntity.getRandom().nextFloat() < 0.4f) {
			Level level = livingEntity.level();
			RandomSource random = livingEntity.getRandom();
			Vec3 pos = livingEntity.position();
			double height = livingEntity.getBoundingBox().getYsize() * 0.5f;

			level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
			for (int i = 0; i < 4; i++) {
				level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x + random.nextDouble(), pos.y + random.nextDouble() * height, pos.z + random.nextDouble(), 0, 0.1d, 0);
			}
		}
	}

	@Override
	protected boolean canPassThrough(BlockGetter level, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState) {
		return toBlockState.getBlock() instanceof FleshVeinsBlock || super.canPassThrough(level, fluid, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState);
	}

	@Override
	protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
		return toBlockState.getBlock() instanceof FleshVeinsBlock || super.canSpreadTo(level, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluid);
	}

	@Override
	protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
		if (state.getBlock() instanceof FleshVeinsBlock) {
			beforeDestroyingBlock(level, pos, state);
			level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);
			return;
		}

		super.spreadTo(level, pos, state, direction, fluidState);
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		if (state.is(ModBlockTags.ACID_DESTRUCTIBLE)) return; //don't drop any block resources i.e. "destroy" them

		super.beforeDestroyingBlock(level, pos, state);
	}

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}

	@Override
	protected void randomTick(Level level, BlockPos liquidPos, FluidState fluidState, RandomSource random) {
		if (level.random.nextFloat() > 0.4f) return;

		for (int i = 0; i < 3; i++) {
			float p = level.random.nextFloat();
			int yOffset = 0;
			if (p < 0.2f) yOffset = 1;
			else if (p < 0.6f) yOffset = -1;
			BlockPos blockPos = liquidPos.offset(random.nextInt(3) - 1, yOffset, random.nextInt(3) - 1);

			if (!level.isLoaded(blockPos)) return;

			BlockState blockState = level.getBlockState(blockPos);
			if (blockState.isAir()) return;

			if (level.random.nextFloat() >= 0.1f) {
				Block block = blockState.getBlock();
				if (corrodeCopper(level, liquidPos, block, blockState, blockPos)) continue;
				if (destroyBlock(level, liquidPos, block, blockState, blockPos)) continue;
				if (erodeBlock(level, liquidPos, block, blockState, blockPos)) continue;

				if (fluidState.getAmount() > 2) {
					destroyFleshVeins(level, liquidPos, block, blockState, blockPos);
				}
			}
			else if (fluidState.getAmount() > 2) {
				Block block = blockState.getBlock();
				destroyFleshVeins(level, liquidPos, block, blockState, blockPos);
			}
		}
	}

	protected boolean corrodeCopper(Level level, BlockPos liquidPos, Block block, BlockState blockState, BlockPos pos) {
		if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, liquidPos, state)));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
			return true;
		}

		return false;
	}

	protected boolean destroyBlock(Level level, BlockPos liquidPos, Block block, BlockState blockState, BlockPos pos) {
		if (!blockState.is(ModBlockTags.ACID_DESTRUCTIBLE)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, liquidPos, Blocks.AIR.defaultBlockState()));
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	protected boolean erodeBlock(Level level, BlockPos liquidPos, Block block, BlockState blockState, BlockPos pos) {
		if (!NORMAL_TO_ERODED_BLOCK_CONVERSION.containsKey(block)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, liquidPos, NORMAL_TO_ERODED_BLOCK_CONVERSION.get(block)));
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	protected void destroyFleshVeins(Level level, BlockPos liquidPos, Block block, BlockState blockState, BlockPos pos) {
		if (block instanceof FleshVeinsBlock) {
			level.setBlockAndUpdate(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, liquidPos, Blocks.AIR.defaultBlockState()));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		}
	}

	@Override
	protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (!state.isSource() && Boolean.FALSE.equals(state.getValue(FALLING))) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
			}
		}
		else if (random.nextInt(10) == 0) {
			level.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0, 0, 0);
		}
	}

	public static class Flowing extends AcidFluid {
		public Flowing(Properties properties) {
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends AcidFluid {

		public Source(Properties properties) {
			super(properties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

}
