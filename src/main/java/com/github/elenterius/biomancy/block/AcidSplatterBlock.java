package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class AcidSplatterBlock extends MultifaceBlock {

	public static final EnhancedIntegerProperty AGE = EnhancedIntegerProperty.wrap(BlockStateProperties.AGE_3);

	private final MultifaceSpreader spreader = new MultifaceSpreader(this);

	public AcidSplatterBlock(Properties properties) {
		super(properties.randomTicks());
		registerDefaultState(defaultBlockState().setValue(AGE.get(), AGE.getMin()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE.get());
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(8) != 0) {
			level.scheduleTick(pos, this, Mth.nextInt(random, 40, 80));
			return;
		}

		if (level.isAreaLoaded(pos, 1)) {
			BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
			for (Direction direction : Direction.values()) {
				mutablePos.setWithOffset(pos, direction);
				BlockState neighborState = level.getBlockState(mutablePos);

				if (neighborState.is(this) && reduceSplatter(neighborState, level, mutablePos, random)) {
					level.scheduleTick(mutablePos, this, Mth.nextInt(random, 40, 80));
				}
			}
		}

		reduceSplatter(state, level, pos, random);
	}

	protected boolean reduceSplatter(BlockState state, Level level, BlockPos pos, RandomSource random) {
		int age = AGE.getValue(state);

		if (age < AGE.getMax()) {
			level.setBlock(pos, AGE.setValue(state, age + 1), Block.UPDATE_CLIENTS);
			affectNeighborBlock(state, level, pos, random);

			return true;
		}

		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		affectNeighborBlock(state, level, pos, random);

		return false;
	}

	private void affectNeighborBlock(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(10) != 0) return;

		for (Direction direction : Direction.allShuffled(random)) {
			if (hasFace(state, direction)) {
				BlockPos neighborPos = pos.relative(direction);
				BlockState neighborState = level.getBlockState(neighborPos);
				Block neighborBlock = neighborState.getBlock();
				if (corrodeCopper(level, neighborPos, neighborBlock, neighborState) || erodeBlock(level, neighborPos, neighborBlock, neighborState)) {
					break;
				}
			}
		}
	}

	protected boolean corrodeCopper(Level level, BlockPos pos, Block block, BlockState blockState) {
		if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, state));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
			return true;
		}

		return false;
	}

	protected boolean erodeBlock(Level level, BlockPos pos, Block block, BlockState blockState) {
		if (!AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.containsKey(block)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.get(block));
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.tickCount % 5 == 0) {
			if (!isEntityInsideDamageArea(level, pos, state, entity)) return;
			AcidInteractions.handleEntityInsideAcid(livingEntity);
		}
	}

	protected boolean isEntityInsideDamageArea(Level level, BlockPos pos, BlockState state, Entity entity) {
		VoxelShape blockShape = getShape(state, level, pos, CollisionContext.of(entity)).move(pos.getX(), pos.getY(), pos.getZ());
		VoxelShape entityShape = Shapes.create(entity.getBoundingBox());
		return Shapes.joinIsNotEmpty(blockShape, entityShape, BooleanOp.AND);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return !context.getItemInHand().is(asItem()) || super.canBeReplaced(state, context);
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	public boolean spreadSplatter(ServerLevel level, BlockPos pos, Direction direction, RandomSource random) {
		BlockState state = level.getBlockState(pos);

		if (state.is(this)) {
			if (AGE.getValue(state) > AGE.getMin()) {
				level.setBlock(pos, AGE.setValue(state, AGE.getMin()), Block.UPDATE_CLIENTS);
			}

			return spreadSplatterFromSource(level, pos, random);
		}
		else if (state.canBeReplaced(new DirectionalPlaceContext(level, pos, direction.getOpposite(), ItemStack.EMPTY, direction))) {
			BlockState stateForPlacement = getStateForPlacement(state, level, pos, direction.getOpposite());
			if (stateForPlacement != null) {
				level.setBlock(pos, stateForPlacement, Block.UPDATE_CLIENTS);
				for (int i = 0; i < 4; i++) {
					getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, pos, random);
				}
				level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
		}

		return false;
	}

	protected boolean spreadSplatterFromSource(ServerLevel level, BlockPos pos, RandomSource random) {
		BlockState state = level.getBlockState(pos);

		boolean hasPlacedVeins = false;

		for (int i = 0; i < 4; i++) {
			if (random.nextFloat() < 0.6f) {
				Optional<MultifaceSpreader.SpreadPos> spreadPos = getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
				if (spreadPos.isPresent()) hasPlacedVeins = true;
			}
		}
		return hasPlacedVeins;
	}

}
