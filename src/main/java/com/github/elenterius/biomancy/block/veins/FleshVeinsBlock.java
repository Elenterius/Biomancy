package com.github.elenterius.biomancy.block.veins;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
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
import com.github.elenterius.biomancy.util.random.Noise;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
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

import java.util.Optional;

public class FleshVeinsBlock extends MultifaceBlock implements SimpleWaterloggedBlock {

	protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final EnhancedIntegerProperty CHARGE = ModBlockProperties.CHARGE;
	private final MultifaceSpreader spreader = new MultifaceSpreader(new MalignantFleshSpreaderConfig(this));

	public FleshVeinsBlock(Properties properties) {
		super(properties.randomTicks());
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(CHARGE.get(), CHARGE.getMin()));
	}

	public static boolean convert(BlockState state, ServerLevel level, BlockPos pos, int directNeighbors, boolean isCradleNearby, float nearCradlePct) {

		Bit32Set facesSet = new Bit32Set();
		facesSet.set(5, hasFace(state, Direction.DOWN));
		facesSet.set(4, hasFace(state, Direction.UP));
		facesSet.set(3, hasFace(state, Direction.NORTH));
		facesSet.set(2, hasFace(state, Direction.SOUTH));
		facesSet.set(1, hasFace(state, Direction.WEST));
		facesSet.set(0, hasFace(state, Direction.EAST));

		int numFaces = facesSet.cardinality();

		if (numFaces == 1) {
			if (isCradleNearby) {
				int bitIndex = facesSet.nextSetBit(0);
				if (bitIndex < 6) {
					Direction direction = Direction.from3DDataValue(5 - bitIndex);
					BlockPos posBelow = pos.relative(direction);
					BlockState stateBelow = level.getBlockState(posBelow);

					Block replacementBlock = ModBlocks.PRIMAL_FLESH.get();

					if (stateBelow.getBlock() == ModBlocks.PRIMAL_FLESH.get() || stateBelow.getBlock() == ModBlocks.MALIGNANT_FLESH.get()) {
						BlockPos posBelow2 = pos.relative(direction, 2);
						BlockState stateBelow2 = level.getBlockState(posBelow2);

						if (direction == Direction.UP && (stateBelow2.getBlock() == ModBlocks.PRIMAL_FLESH.get() || stateBelow2.getBlock() == ModBlocks.MALIGNANT_FLESH.get())) {
							if (level.getLightEngine().getRawBrightness(pos, 0) < 5) {
								level.setBlock(posBelow, Blocks.SHROOMLIGHT.defaultBlockState(), Block.UPDATE_CLIENTS);
								return true;
							}
						}

						posBelow = posBelow2;
						stateBelow = stateBelow2;
					}
					else {
						replacementBlock = level.random.nextFloat() < nearCradlePct ? ModBlocks.PRIMAL_FLESH.get() : ModBlocks.MALIGNANT_FLESH.get();
					}

					return PrimordialEcosystem.tryToReplaceBlock(level, posBelow, stateBelow, replacementBlock.defaultBlockState());
				}

				return false;
			}

			if (directNeighbors < 3) return false;

			int bitIndex = facesSet.nextSetBit(0);
			if (bitIndex < 6) {
				//vein faces point inwards and the direction is in reference to itself and not in reference to the block it's attached to

				Direction direction = Direction.from3DDataValue(5 - bitIndex);
				BlockPos posBelow = pos.relative(direction);
				BlockState stateBelow = level.getBlockState(posBelow);

				BlockState replacementBlockState;

				Noise noise = PrimordialEcosystem.getCellularNoise(level);
				final float outerBorderThreshold = 0.16f;
				final float innerBorderThreshold = 0.16f - 0.03f;
				final float n = noise.getValueAtCenter(pos);

				if (stateBelow.getBlock() == ModBlocks.PRIMAL_FLESH.get() || stateBelow.getBlock() == ModBlocks.MALIGNANT_FLESH.get()) {
					posBelow = pos.relative(direction, 2);
					stateBelow = level.getBlockState(posBelow);
					replacementBlockState = ModBlocks.PRIMAL_FLESH.get().defaultBlockState();
				}
				else if (stateBelow.is(ModBlockTags.PRIMORDIAL_ECO_SYSTEM_REPLACEABLE) && stateBelow.is(BlockTags.OVERWORLD_NATURAL_LOGS)) {
					if (n < innerBorderThreshold) {
						if (state.hasProperty(RotatedPillarBlock.AXIS)) {
							Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
							replacementBlockState = Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
						}
						else replacementBlockState = ModBlocks.PRIMAL_FLESH_WALL.get().defaultBlockState();
					}
					else replacementBlockState = ModBlocks.PRIMAL_FLESH.get().defaultBlockState();
				}
				else {
					replacementBlockState = ModBlocks.MALIGNANT_FLESH.get().defaultBlockState();
				}

				if (!PrimordialEcosystem.tryToReplaceBlock(level, posBelow, stateBelow, replacementBlockState)) {
					if (n >= outerBorderThreshold) {
						if (!LevelUtil.isBlockNearby(level, pos, 2, blockState -> blockState.is(ModBlocks.MALIGNANT_BLOOM.get()))) {
							BlockState slabState = ModBlocks.MALIGNANT_FLESH_SLAB.get().getStateForPlacement(level, pos, direction.getOpposite());
							level.setBlock(pos, slabState, Block.UPDATE_CLIENTS);
						}
					}
					else if (n < innerBorderThreshold && (PrimordialEcosystem.getRandomWithSeed(pos).nextFloat() <= 0.3f) && (LevelUtil.getMaxBrightness(level, pos) > 5)) {
						posBelow = pos.relative(direction);
						stateBelow = level.getBlockState(posBelow);
						MalignantBloomBlock block = ModBlocks.MALIGNANT_BLOOM.get();
						boolean mayPlace = block.mayPlaceOn(level, posBelow, stateBelow);
						if (mayPlace && !LevelUtil.isBlockNearby(level, pos, 4, blockState -> blockState.is(block)) && block.hasUnobstructedAim(level, pos, direction.getOpposite())) {
							BlockState slabState = block.getStateForPlacement(level, pos, direction.getOpposite());
							level.setBlock(pos, slabState, Block.UPDATE_CLIENTS);
						}
					}
				}
			}

			return true;
		}

		if (isCradleNearby) return false;
		if (LevelUtil.isBlockNearby(level, pos, 2, blockState -> blockState.is(ModBlocks.MALIGNANT_BLOOM.get()))) return false;

		if (numFaces > 3) {
			convertSelfIntoFullBlock(level, pos);
		}

		return convertSelfIntoStairs(level, pos, facesSet);
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
		return state.is(ModBlockTags.PRIMORDIAL_ECO_SYSTEM_REPLACEABLE) || Block.isFaceFull(state.getBlockSupportShape(level, pos), direction.getOpposite()) || Block.isFaceFull(state.getCollisionShape(level, pos), direction.getOpposite());
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
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
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
				level.playSound(null, pos, ModSoundEvents.DECOMPOSER_EAT.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.random.nextFloat() >= 0.5f) return;
		if (!level.isAreaLoaded(pos, 2)) return;

		int cradleCoreRadius = 8;
		int maxCradleDist = cradleCoreRadius * 4;
		PrimordialCradleBlockEntity cradle = LevelUtil.findNearestBlockEntity(level, pos, maxCradleDist, PrimordialCradleBlockEntity.class);

		int charge = getCharge(state);
		if (charge < 2) {
			if (BiomancyConfig.SERVER.doUnlimitedGrowth.get()) {
				setCharge(level, pos, state, CHARGE.getMax());
			}
			else if (cradle != null && cradle.consumePrimalEnergy(level, 1)) {
				setCharge(level, pos, state, charge + 1);
			}
			return;
		}

		double cradleDistance = cradle != null ? Math.sqrt(cradle.getBlockPos().distSqr(pos)) : maxCradleDist + 1;
		float nearCradlePct = Mth.clamp((float) (1d - cradleDistance / maxCradleDist), 0f, 1f);

		int directNeighbors = 0;
		for (Direction direction : Direction.values()) {
			BlockState neighborState = level.getBlockState(pos.relative(direction));
			directNeighbors += neighborState.is(this) ? 1 : 0;
		}

		float populationPct = directNeighbors / (float) Direction.values().length;
		float conversionChance = charge / (CHARGE.getMax() + 5f) + populationPct * 0.5f;

		if (random.nextFloat() < conversionChance && convert(state, level, pos, directNeighbors, cradleDistance < cradleCoreRadius, nearCradlePct)) {
			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1.2f, 0.15f + random.nextFloat() * 0.5f);
			return;
		}

		if (charge > 4) {
			int growthAmount = (int) Mth.clamp(getSpreader().spreadAll(state, level, pos, false), 0, CHARGE.getMax());
			if (growthAmount > 0) {
				charge -= growthAmount * 2;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
		}
		else {
			if (getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random).isPresent()) {
				charge -= 2;
				state = level.getBlockState(pos);
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
		}

		if (cradleDistance <= maxCradleDist) {
			int primalEnergy = Math.max(charge, Math.round(CHARGE.getMax() * nearCradlePct) / 2);
			if (cradle != null && cradle.consumePrimalEnergy(level, primalEnergy)) {
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
