package com.github.elenterius.biomancy.block.veins;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.block.cradle.PrimalEnergyHandler;
import com.github.elenterius.biomancy.block.malignantbloom.MalignantBloomBlock;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.util.ArrayUtil;
import com.github.elenterius.biomancy.util.Bit32Set;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.random.CellularNoise;
import com.github.elenterius.biomancy.util.shape.Shape;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.ShapeManager;
import com.github.elenterius.biomancy.world.mound.MoundChamber;
import com.github.elenterius.biomancy.world.mound.MoundShape;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class FleshVeinsBlock extends MultifaceBlock implements SimpleWaterloggedBlock {

	protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final EnhancedIntegerProperty CHARGE = ModBlockProperties.CHARGE;
	public static final Predicate<BlockState> BLOCKS_TO_AVOID_PREDICATE = blockState -> blockState.is(ModBlocks.MALIGNANT_BLOOM.get());
	private final MultifaceSpreader spreader = new MultifaceSpreader(new MalignantFleshSpreaderConfig(this));

	public FleshVeinsBlock(Properties properties) {
		super(properties.randomTicks());
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(CHARGE.get(), CHARGE.getMin()));
	}

	public static boolean convert(BlockState state, ServerLevel level, BlockPos pos, int directNeighbors, @Nullable MoundChamber chamber, float nearBoundingCenterPct, @Nullable PrimalEnergyHandler energyHandler) {

		Bit32Set facesSet = new Bit32Set();
		facesSet.set(5, hasFace(state, Direction.DOWN));
		facesSet.set(4, hasFace(state, Direction.UP));
		facesSet.set(3, hasFace(state, Direction.NORTH));
		facesSet.set(2, hasFace(state, Direction.SOUTH));
		facesSet.set(1, hasFace(state, Direction.WEST));
		facesSet.set(0, hasFace(state, Direction.EAST));

		if (chamber != null) {
			return convertInsideChamber(level, pos, directNeighbors, chamber, nearBoundingCenterPct, facesSet, energyHandler);
		}

		return convertNormal(level, pos, directNeighbors, facesSet);
	}

	protected static boolean convertNormal(ServerLevel level, BlockPos pos, int directNeighbors, Bit32Set facesSet) {
		int numFaces = facesSet.cardinality();

		boolean hasAnyBlockToAvoidNearby = LevelUtil.isBlockNearby(level, pos, 2, BLOCKS_TO_AVOID_PREDICATE);

		if (numFaces == 1) {
			CellularNoise cellularNoise = PrimordialEcosystem.getCellularNoise(level);
			float noiseValue = cellularNoise.getValueAtCenter(pos);

			//vein face points inwards and the direction is in reference to itself and not in reference to the block it's attached to
			Direction axisDirection = Direction.from3DDataValue(5 - facesSet.nextSetBit(0));

			boolean hasConvertedAnyOtherBlocks = convertDirectNeighborBlock(level, pos, axisDirection, directNeighbors, cellularNoise, noiseValue);

			if (!hasConvertedAnyOtherBlocks && !hasAnyBlockToAvoidNearby && directNeighbors > 2) {
				if (noiseValue >= cellularNoise.borderThreshold()) {
					return convertSelfIntoSlabBlock(level, pos, axisDirection.getOpposite());
				}
				else if (noiseValue < cellularNoise.coreThreshold() && (PrimordialEcosystem.getRandomWithSeed(pos).nextFloat() <= 0.3f) && (LevelUtil.getMaxBrightness(level, pos) > 5)) {
					return convertSelfIntoBloom(level, pos, axisDirection);
				}
			}

			return hasConvertedAnyOtherBlocks;
		}

		if (hasAnyBlockToAvoidNearby) return false;

		if (numFaces > 3) {
			return convertSelfIntoFullBlock(level, pos);
		}

		return convertSelfIntoStairs(level, pos, facesSet);
	}

	protected static boolean convertInsideChamber(ServerLevel level, BlockPos pos, int directNeighbors, MoundChamber chamber, float nearBoundingCenterPct, Bit32Set facesSet, @Nullable PrimalEnergyHandler energyHandler) {
		Direction[] axisDirections = Arrays.stream(facesSet.getIndices()).mapToObj(i -> Direction.from3DDataValue(5 - i)).toArray(Direction[]::new);
		ArrayUtil.shuffle(axisDirections, level.random);

		for (Direction axisDirection : axisDirections) {
			BlockPos posRelative = pos.relative(axisDirection);
			BlockState stateRelative = level.getBlockState(posRelative);

			if (PrimordialEcosystem.isReplaceable(stateRelative)) {
				return destroyBlockAndConvertIntoEnergy(level, posRelative, energyHandler, 15);
			}
			else if (PrimordialEcosystem.FULL_FLESH_BLOCKS.contains(stateRelative.getBlock())) {
				if (chamber.contains(posRelative.getX(), posRelative.getY(), posRelative.getZ())) {
					return destroyBlockAndConvertIntoEnergy(level, posRelative, energyHandler, 30); //TODO: this might interfere with future room content generation
				}

				BlockPos posBelow2 = pos.relative(axisDirection, 2);
				BlockState stateBelow2 = level.getBlockState(posBelow2);

				if (axisDirection == Direction.UP && PrimordialEcosystem.FULL_FLESH_BLOCKS.contains(stateBelow2.getBlock()) && LevelUtil.getMaxBrightness(level, pos) < 5) {
					return level.setBlock(posRelative, ModBlocks.BLOOMLIGHT.get().defaultBlockState(), Block.UPDATE_CLIENTS);
				}

				posRelative = posBelow2;
				stateRelative = stateBelow2;
				if (PrimordialEcosystem.isReplaceable(stateRelative)) {
					BlockState replacementState = level.random.nextFloat() < nearBoundingCenterPct ? ModBlocks.PRIMAL_FLESH.get().defaultBlockState() : ModBlocks.MALIGNANT_FLESH.get().defaultBlockState();
					return level.setBlock(posRelative, replacementState, Block.UPDATE_CLIENTS);
				}
			}
		}

		return false;
	}

	protected static boolean destroyBlockAndConvertIntoEnergy(ServerLevel level, BlockPos pos, @Nullable PrimalEnergyHandler energyHandler, int amount) {
		if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS)) {
			if (energyHandler != null) energyHandler.fillPrimalEnergy(amount);
			return true;
		}
		return false;
	}

	protected static boolean convertDirectNeighborBlock(ServerLevel level, BlockPos pos, Direction axisDirection, int directNeighbors, CellularNoise cellularNoise, float noiseValue) {
		BlockPos posRelative = pos.relative(axisDirection);
		BlockState stateRelative = level.getBlockState(posRelative);
		BlockState replacementState = null;

		if (directNeighbors > 2) {
			if (stateRelative.getBlock() == ModBlocks.PRIMAL_FLESH.get() || stateRelative.getBlock() == ModBlocks.MALIGNANT_FLESH.get()) {
				posRelative = pos.relative(axisDirection, 2);
				stateRelative = level.getBlockState(posRelative);
				return PrimordialEcosystem.tryToReplaceBlock(level, posRelative, stateRelative, ModBlocks.PRIMAL_FLESH.get().defaultBlockState());
			}
			else {
				replacementState = ModBlocks.MALIGNANT_FLESH.get().defaultBlockState();
			}
		}

		if (PrimordialEcosystem.isReplaceableLog(stateRelative)) {
			if (noiseValue < cellularNoise.coreThreshold()) {
				if (stateRelative.hasProperty(RotatedPillarBlock.AXIS)) {
					Direction.Axis axis = stateRelative.getValue(RotatedPillarBlock.AXIS);
					replacementState = Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
				}
				else replacementState = ModBlocks.PRIMAL_FLESH_WALL.get().defaultBlockState();
			}
			else replacementState = ModBlocks.PRIMAL_FLESH.get().defaultBlockState();
		}

		if (replacementState != null) {
			return PrimordialEcosystem.tryToReplaceBlock(level, posRelative, stateRelative, replacementState);
		}

		return false;
	}

	protected static boolean convertSelfIntoBloom(ServerLevel level, BlockPos pos, Direction direction) {
		MalignantBloomBlock bloomBlock = ModBlocks.MALIGNANT_BLOOM.get();

		BlockPos posBelow = pos.relative(direction);
		BlockState stateBelow = level.getBlockState(posBelow);
		boolean mayPlace = bloomBlock.mayPlaceOn(level, posBelow, stateBelow);

		if (mayPlace && !LevelUtil.isBlockNearby(level, pos, 4, blockState -> blockState.is(bloomBlock)) && bloomBlock.hasUnobstructedAim(level, pos, direction.getOpposite())) {
			BlockState stateForPlacement = bloomBlock.getStateForPlacement(level, pos, direction.getOpposite());
			return level.setBlock(pos, stateForPlacement, Block.UPDATE_CLIENTS);
		}

		return false;
	}

	protected static boolean convertSelfIntoSlabBlock(ServerLevel level, BlockPos pos, Direction direction) {
		BlockState stateForPlacement = ModBlocks.MALIGNANT_FLESH_SLAB.get().getStateForPlacement(level, pos, direction);
		return level.setBlock(pos, stateForPlacement, Block.UPDATE_CLIENTS);
	}

	protected static boolean convertSelfIntoFullBlock(ServerLevel level, BlockPos pos) {
		return level.setBlock(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState(), Block.UPDATE_CLIENTS);
	}

	protected static boolean convertSelfIntoStairs(ServerLevel level, BlockPos pos, Bit32Set facesSet) {
		int mask = facesSet.getBits();

		if (mask == 0b10_10_00) { //down & north
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_01_00) { //down & south
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_00_10) { //down & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_00_01) { //down & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_10_01) { //down & north & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_10_10) { //down & north & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_01_10) { //down & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b10_01_01) { //down & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.BOTTOM)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_10_01) { //up & north & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_01_10) { //up & south & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_01_01) { //up & south & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_10_10) { //up & north & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_10_00) { //up & north
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.NORTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_01_00) { //up & south
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.SOUTH)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_00_10) { //up & west
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.WEST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		if (mask == 0b01_00_01) { //up & east
			BlockState blockState = ModBlocks.MALIGNANT_FLESH_STAIRS.get().defaultBlockState()
					.setValue(StairBlock.HALF, Half.TOP)
					.setValue(StairBlock.FACING, Direction.EAST)
					.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
			return level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
		}

		return false;
	}

	protected static BlockState removeFace(BlockState state, BooleanProperty face) {
		BlockState blockstate = state.setValue(face, Boolean.FALSE);
		return hasAnyFace(blockstate) ? blockstate : Blocks.AIR.defaultBlockState();
	}

	public static boolean canVeinsAttachTo(BlockGetter level, Direction direction, BlockPos pos, BlockState state) {
		if (state.is(ModBlockTags.DISALLOW_VEINS_TO_ATTACH)) return false;
		if (state.is(ModBlockTags.ALLOW_VEINS_TO_ATTACH)) return true;
		return Block.isFaceFull(state.getBlockSupportShape(level, pos), direction.getOpposite()) || Block.isFaceFull(state.getCollisionShape(level, pos), direction.getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED, CHARGE.get());
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return getCharge(state) > 0;
	}

	protected int getCharge(BlockState state) {
		return CHARGE.getValue(state);
	}

	protected void setCharge(Level level, BlockPos pos, BlockState state, int amount) {
		if (!state.is(this)) return;

		BlockState newState = CHARGE.setValue(state, amount);
		if (newState == state) return;

		level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (!hasAnyFace(state)) {
			return Blocks.AIR.defaultBlockState();
		}

		if (hasFace(state, direction) && !canVeinsAttachTo(level, direction, neighborPos, neighborState)) {
			return removeFace(state, getFaceProperty(direction));
		}

		return state;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		boolean flag = false;

		for (Direction direction : DIRECTIONS) {
			if (hasFace(state, direction)) {
				BlockPos blockpos = pos.relative(direction);
				if (!canVeinsAttachTo(level, direction, blockpos, level.getBlockState(blockpos))) {
					return false;
				}
				flag = true;
			}
		}

		return flag;
	}

	@Override
	public boolean isValidStateForPlacement(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
		if (isFaceSupported(direction) && (!state.is(this) || !hasFace(state, direction))) {
			BlockPos blockPos = pos.relative(direction);
			return canVeinsAttachTo(level, direction, blockPos, level.getBlockState(blockPos));
		}
		return false;
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return !useContext.getItemInHand().is(asItem()) || super.canBeReplaced(state, useContext);
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
			int charge = CHARGE.getValue(state);
			if (charge >= CHARGE.getMax()) return;

			ItemStack stack = itemEntity.getItem();

			if (stack.is(ModItems.LIVING_FLESH.get())) {
				charge = CHARGE.getMax();

				Vec3 motion = new Vec3((level.random.nextFloat() - 0.5d) * 0.1d, level.random.nextFloat() * 0.1d + 0.15d, (level.random.nextFloat() - 0.5d) * 0.1d);
				((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 8, motion.x, motion.y, motion.z, 0.05f);

				stack.shrink(1);
				setCharge(level, pos, state, charge);
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 0.6f, 0.15f + level.random.nextFloat() * 0.5f);
			}
			else if (stack.isEdible()) {
				int nutrition = Optional.ofNullable(stack.getFoodProperties(null))
						.filter(FoodProperties::isMeat)
						.map(FoodProperties::getNutrition).orElse(0);
				if (nutrition <= 0) return;

				Vec3 motion = new Vec3((level.random.nextFloat() - 0.5d) * 0.1d, level.random.nextFloat() * 0.1d + 0.15d, (level.random.nextFloat() - 0.5d) * 0.1d);
				((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 8, motion.x, motion.y, motion.z, 0.05f);

				int optimalAmount = Mth.ceil((CHARGE.getMax() - (float) charge) / nutrition);
				int amount = Math.min(stack.getCount(), optimalAmount);
				stack.shrink(amount);
				charge += amount * nutrition;
				setCharge(level, pos, state, charge);
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 0.6f, 0.15f + level.random.nextFloat() * 0.5f);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.random.nextFloat() >= 0.5f) return;
		if (!level.isAreaLoaded(pos, 2)) return;

		PrimalEnergyHandler energyHandler = null;
		MoundChamber chamber = null;
		float nearBoundingCenterPct = 0;

		if (ShapeManager.getShape(level, pos) instanceof MoundShape moundShape) {
			BlockPos origin = moundShape.getOrigin();
			BlockEntity existingBlockEntity = level.getExistingBlockEntity(origin);
			if (existingBlockEntity instanceof PrimalEnergyHandler peh) {
				energyHandler = peh;
			}

			chamber = moundShape.getChamberAt(pos.getX(), pos.getY(), pos.getZ());
			Shape boundingShape = moundShape.getBoundingShapeAt(pos.getX(), pos.getY(), pos.getZ());
			if (boundingShape != null) {
				double radius = boundingShape instanceof Shape.Sphere sphere ? sphere.getRadius() : boundingShape.getAABB().getSize() / 2;
				double radiusSqr = radius * radius;
				double distSqr = boundingShape.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
				nearBoundingCenterPct = Mth.clamp((float) (1 - distSqr / radiusSqr), 0f, 1f);
			}
		}

		int charge = getCharge(state);
		if (charge < 2) {
			if (Boolean.TRUE.equals(BiomancyConfig.SERVER.doUnlimitedGrowth.get())) {
				setCharge(level, pos, state, CHARGE.getMax());
			}
			else if (energyHandler != null && energyHandler.drainPrimalEnergy(1) > 0) {
				setCharge(level, pos, state, charge + 1);
			}
			return;
		}

		int directNeighbors = 0;
		for (Direction direction : Direction.values()) {
			BlockState neighborState = level.getBlockState(pos.relative(direction));
			directNeighbors += neighborState.is(this) ? 1 : 0;
		}

		float populationPct = directNeighbors / (float) Direction.values().length;
		float conversionChance = charge / (CHARGE.getMax() + 5f) + populationPct * 0.5f;

		if (random.nextFloat() < conversionChance && convert(state, level, pos, directNeighbors, chamber, nearBoundingCenterPct, energyHandler)) {
			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.8f, 0.15f + random.nextFloat() * 0.5f);
			//return; //TODO: exiting early hampers growth to a very extreme degree. reevaluate which conversions should return true or be ignored
		}

		if (charge > 4) {
			int growthAmount = (int) Mth.clamp(getSpreader().spreadAll(state, level, pos, false), 0, CHARGE.getMax());
			if (growthAmount > 0) {
				charge -= growthAmount * 2;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.6f, 0.15f + random.nextFloat() * 0.5f);
			}
		}
		else {
			if (getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random).isPresent()) {
				charge -= 2;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.6f, 0.15f + random.nextFloat() * 0.5f);
			}
		}

		if (energyHandler != null) {
			int primalEnergy = Math.max(charge, Math.round(CHARGE.getMax() * nearBoundingCenterPct) / 2);
			if (energyHandler.getPrimalEnergy() > primalEnergy && energyHandler.drainPrimalEnergy(primalEnergy) >= primalEnergy) {
				increaseChargeAroundPos(level, pos, random, primalEnergy * 2);
			}
			else if (charge > 1) {
				int usedCharge = increaseChargeAroundPos(level, pos, random, charge);
				for (int i = usedCharge; i > 0; i--) {
					if (random.nextFloat() < 0.75f) charge--;
				}
				charge = Math.max(charge, 1);
			}
			setCharge(level, pos, state, charge);
		}
		else {
			if (charge > 1) {
				charge -= increaseChargeAroundPos(level, pos, random, charge);
			}
			setCharge(level, pos, state, charge);
		}
	}

	public int increaseChargeAroundPos(ServerLevel level, BlockPos pos, RandomSource random, final int availableCharge) {
		if (availableCharge <= 0) return 0;

		BlockPos[] positions = new BlockPos[3 * 3 * 3 - 1];
		int i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					positions[i++] = pos.offset(x, y, z);
				}
			}
		}
		ArrayUtil.shuffle(positions, random);

		int amount = Math.max(availableCharge / 2, 1);

		int usedCharge = 0;
		for (BlockPos neighborPos : positions) {
			BlockState neighborState = level.getBlockState(neighborPos);
			usedCharge += increaseCharge(level, neighborPos, neighborState, amount);
			if (availableCharge - usedCharge <= 0) break;
		}

		return usedCharge;
	}

	public int increaseCharge(ServerLevel level, BlockPos pos, BlockState state, int amount) {
		if (!state.is(this)) return 0;

		int currentCharge = getCharge(state);
		if (currentCharge < CHARGE.getMax()) {
			int usedCharge = Math.min(amount, CHARGE.getMax() - currentCharge);
			setCharge(level, pos, state, usedCharge);
			return usedCharge;
		}
		return 0;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection, int charge) {
		BlockState stateForPlacement = getStateForPlacement(currentState, level, pos, lookingDirection);
		if (stateForPlacement != null) {
			return CHARGE.setValue(stateForPlacement, charge);
		}
		return null;
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}
}
