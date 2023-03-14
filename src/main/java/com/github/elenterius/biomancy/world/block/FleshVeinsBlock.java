package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.world.block.property.DirectionalSlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Optional;

public class FleshVeinsBlock extends MultifaceBlock implements SimpleWaterloggedBlock {

	protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final IntegerProperty CHARGE = ModBlocks.CHARGE;
	private final MultifaceSpreader spreader = new MultifaceSpreader(new SpreaderConfig(this));

	public FleshVeinsBlock(Properties properties) {
		super(properties.randomTicks());
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(CHARGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED, CHARGE);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return getCharge(state) > 0;
	}

	protected int getCharge(BlockState state) {
		return state.getValue(CHARGE);
	}

	protected BlockState applyCharge(BlockState state, int charge) {
		return state.setValue(CHARGE, Mth.clamp(charge, 0, 15));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return useContext.getItemInHand().is(asItem()) || super.canBeReplaced(state, useContext);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide) return;

		if (entity instanceof ItemEntity itemEntity) {
			int charge = getCharge(state);
			if (charge >= 15) return;

			ItemStack stack = itemEntity.getItem();

			if (stack.is(ModItems.LIVING_FLESH.get())) {
				charge = 15;

				Vec3 motion = new Vec3((level.random.nextFloat() - 0.5d) * 0.1d, level.random.nextFloat() * 0.1d + 0.15d, (level.random.nextFloat() - 0.5d) * 0.1d);
				((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 8, motion.x, motion.y, motion.z, 0.05f);

				stack.shrink(1);
				level.setBlockAndUpdate(pos, applyCharge(state, charge));
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			}
			else if (stack.isEdible()) {
				int nutrition = Optional.ofNullable(stack.getFoodProperties(null))
						.filter(FoodProperties::isMeat)
						.map(FoodProperties::getNutrition).orElse(0);
				if (nutrition <= 0) return;

				Vec3 motion = new Vec3((level.random.nextFloat() - 0.5d) * 0.1d, level.random.nextFloat() * 0.1d + 0.15d, (level.random.nextFloat() - 0.5d) * 0.1d);
				((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 8, motion.x, motion.y, motion.z, 0.05f);

				int optimalAmount = Mth.ceil((15f - charge) / nutrition);
				int amount = Math.min(stack.getCount(), optimalAmount);
				stack.shrink(amount);
				charge += amount * nutrition;
				level.setBlockAndUpdate(pos, applyCharge(state, charge));
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 2)) return;
		int charge = getCharge(state);
		if (charge <= 0) return;

		if (random.nextFloat() < (charge / 15f) && convertSelf(state, level, pos)) {
			if (random.nextFloat() < 0.75f) getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
			if (random.nextFloat() < 0.60f) getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
			if (random.nextFloat() < 0.45f) getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);

			for (Direction subDirection : Direction.allShuffled(random)) {
				BlockPos neighborPos = pos.relative(subDirection);
				BlockState neighborState = level.getBlockState(neighborPos);
				increaseCharge(level, neighborPos, neighborState, 1);
			}

			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1.2f, 0.15f + random.nextFloat() * 0.5f);
			return;
		}

		if (charge > 4) {
			long growthAmount = getSpreader().spreadAll(state, level, pos, false);
			if (growthAmount > 0) {
				charge -= growthAmount;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
		}
		else {
			if (getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random).isPresent()) {
				charge -= 1;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
		}

		for (Direction direction : Direction.allShuffled(random)) {
			if (charge <= 0) break;

			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			int usedCharge = increaseCharge(level, neighborPos, neighborState, 1);
			charge -= usedCharge;
		}

		level.setBlockAndUpdate(pos, applyCharge(state, charge));
	}

	protected boolean convertSelf(BlockState state, ServerLevel level, BlockPos pos) {
		BitSet bitSet = new BitSet(6);
		bitSet.set(5, hasFace(state, Direction.DOWN));
		bitSet.set(4, hasFace(state, Direction.UP));
		bitSet.set(3, hasFace(state, Direction.NORTH));
		bitSet.set(2, hasFace(state, Direction.SOUTH));
		bitSet.set(1, hasFace(state, Direction.WEST));
		bitSet.set(0, hasFace(state, Direction.EAST));

		int faces = bitSet.cardinality();

		if (faces > 3) {
			level.setBlockAndUpdate(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());
			return true;
		}

		if (faces == 1) {
			for (int i = 0; i < 5; i++) {
				if (bitSet.get(i)) {
					Direction direction = Direction.from3DDataValue(5 - i);
					BlockState slabState = ModBlocks.MALIGNANT_FLESH_SLAB.get().defaultBlockState();
					DirectionalSlabType type = DirectionalSlabType.getHalfFrom(pos, Vec3.atCenterOf(pos), direction.getOpposite());
					level.setBlockAndUpdate(pos, slabState.setValue(DirectionalSlabBlock.TYPE, type));
					return true;
				}
			}
		}

		int mask = bitSet.toByteArray()[0];

		if (mask == 0b10_10_00) { //down & north
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_01_00) { //down & south
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_00_10) { //down & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_00_01) { //down & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_10_01) { //down & north & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_10_10) { //down & north & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_01_10) { //down & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b10_01_01) { //down & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_10_01) { //up & north & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_01_10) { //up & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_01_01) { //up & south & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_10_10) { //up & north & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_10_00) { //up & north
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_01_00) { //up & south
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_00_10) { //up & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		if (mask == 0b01_00_01) { //up & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlockAndUpdate(pos, blockState);
		}

		return false;
	}

	public int increaseCharge(ServerLevel level, BlockPos pos, BlockState state, int amount) {
		if (state.is(this)) {
			int charge = getCharge(state);
			if (charge < 15) {
				level.setBlockAndUpdate(pos, applyCharge(state, charge + amount));
				return amount;
			}
		}
		return 0;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection, int charge) {
		BlockState stateForPlacement = getStateForPlacement(currentState, level, pos, lookingDirection);
		if (stateForPlacement != null) {
			return applyCharge(stateForPlacement, charge);
		}
		return null;
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return this.spreader;
	}

	static class SpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {

		public SpreaderConfig(MultifaceBlock block) {
			super(block);
		}

		@Override
		public boolean isOtherBlockValidAsSource(BlockState state) {
			return state.getBlock() instanceof PrimordialCradleBlock || state.is(ModBlocks.MALIGNANT_FLESH_SLAB.get()) || state.is(ModBlocks.MALIGNANT_FLESH_STAIRS.get()) || state.is(ModBlocks.MALIGNANT_FLESH.get());
		}

		@Nullable
		@Override
		public BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection) {
			return super.getStateForPlacement(currentState, level, pos, lookingDirection);
		}

		@Override
		protected boolean stateCanBeReplaced(BlockGetter level, BlockPos posA, BlockPos posB, Direction direction, BlockState state) {
			BlockState blockstate = level.getBlockState(posB.relative(direction));
			if (!blockstate.is(Blocks.MOVING_PISTON)) {
				FluidState fluidState = state.getFluidState();
				if (!fluidState.isEmpty() && !fluidState.is(Fluids.WATER)) return false;

				Material material = state.getMaterial();
				if (material == Material.FIRE) return false;

				return material.isReplaceable() || super.stateCanBeReplaced(level, posA, posB, direction, state);
			}

			return super.stateCanBeReplaced(level, posA, posB, direction, state);
		}

		@Override
		public boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos spreadPos, BlockState state, boolean markForPostprocessing) {
			int neighbors = 0;
			for (Direction direction : DIRECTIONS) {
				for (int i = 0; i < 2; i++) {
					BlockState neighborState = level.getBlockState(spreadPos.pos().relative(direction, i + 1));
					neighbors += neighborState.is(block) || isOtherBlockValidAsSource(neighborState) ? 1 : 0;
				}
			}
			if (neighbors >= 4) return false;

			BlockState blockstate = getStateForPlacement(state, level, spreadPos.pos(), spreadPos.face());
			if (blockstate == null) return false;

			if (markForPostprocessing) {
				level.getChunk(spreadPos.pos()).markPosForPostprocessing(spreadPos.pos());
			}

			return level.setBlock(spreadPos.pos(), blockstate, 2);
		}
	}
}
