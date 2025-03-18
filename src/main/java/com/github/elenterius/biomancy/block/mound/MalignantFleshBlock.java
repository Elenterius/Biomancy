package com.github.elenterius.biomancy.block.mound;

import com.github.elenterius.biomancy.block.FleshBlock;
import com.github.elenterius.biomancy.block.cradle.PrimalEnergyHandler;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModPlantTypes;
import com.github.elenterius.biomancy.util.ArrayUtil;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.random.CellularNoise;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.MoundChamber;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.mound.decorator.ChamberDecorator;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import com.github.elenterius.biomancy.world.spatial.geometry.HasRadius;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class MalignantFleshBlock extends FleshBlock {

	public static final Predicate<BlockState> BLOCKS_TO_AVOID_PREDICATE = blockState -> blockState.is(ModBlocks.PRIMAL_BLOOM.get());

	public static final EnhancedIntegerProperty CHARGE = ModBlockProperties.CHARGE;

	public MalignantFleshBlock(Properties properties) {
		super(properties.randomTicks(), ModPlantTypes.PRIMAL_FLESH);
		registerDefaultState(defaultBlockState().setValue(CHARGE.get(), CHARGE.getMin()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CHARGE.get());
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return getCharge(state) > 0;
	}

	protected int getCharge(BlockState state) {
		return CHARGE.getValue(state);
	}

	protected void setCharge(Level level, BlockPos pos, BlockState state, int amount) {
		BlockState newState = CHARGE.setValue(state, amount);
		if (newState == state) return;

		level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 2)) return;

		int charge = getCharge(state);

		CellularNoise cellularNoise = PrimordialEcosystem.getCellularNoise(level);
		float noiseValue = cellularNoise.getValueAtCenter(pos);

		Neighbor[] directNeighbors = new Neighbor[6];
		int n = 0;
		for (Direction direction : Direction.allShuffled(level.random)) {
			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			directNeighbors[n++] = new Neighbor(neighborPos, direction, neighborState);
		}

		if (SpatialShapeManager.getClosestShape(level, pos, MoundShape.class::isInstance) instanceof MoundShape mound) {
			BlockEntity blockEntity = level.getExistingBlockEntity(mound.getOrigin());
			if (blockEntity instanceof PrimalEnergyHandler energyHandler) {

				MoundChamber chamber = mound.getChamberAt(pos);
				if (chamber != null && chamber.contains(pos)) {
					convertNeighborsInsideMound(level, pos, directNeighbors, mound, energyHandler);

					charge += energyHandler.drainPrimalEnergy(CHARGE.getMax() - charge);

					if (charge >= 2) {
						charge -= increaseNeighborCharge(level, pos, charge, random);
						charge += energyHandler.drainPrimalEnergy(CHARGE.getMax() - charge);
					}

					if (convertSelfInsideChamber(level, pos, state, charge, directNeighbors, mound, chamber, energyHandler, random)) return;
				}
				else {
					convertNeighborsInsideMound(level, pos, directNeighbors, mound, energyHandler);

					charge += energyHandler.drainPrimalEnergy(CHARGE.getMax() - charge);

					if (charge >= 2) {
						charge -= increaseNeighborCharge(level, pos, charge, random);
						charge += energyHandler.drainPrimalEnergy(CHARGE.getMax() - charge);
					}

					if (convertSelfIntoDoor(level, pos, mound)) return;
					if (convertSelfInsideMound(level, pos, directNeighbors, mound, random)) return;
				}
			}
		}
		else {
			charge -= convertNeighborsOutside(level, pos, charge, cellularNoise, directNeighbors);

			if (charge >= 2) {
				charge -= increaseNeighborCharge(level, pos, charge, random);
			}

			if (convertSelfOutside(level, pos, random, noiseValue, cellularNoise, directNeighbors)) return;
		}

		setCharge(level, pos, state, charge);
	}

	private static boolean convertSelfIntoDoor(ServerLevel level, BlockPos pos, MoundShape mound) {
		// create "Door" between two adjacent chambers that are separated by a one block thick wall

		for (Direction.Axis axis : Direction.Axis.VALUES) {
			Direction directionA = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
			Direction directionB = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

			BlockPos posA = pos.relative(directionA);
			BlockPos posB = pos.relative(directionB);

			if (mound.hasChamberAt(posA) && mound.hasChamberAt(posB)) {
				MoundChamber chamberA = mound.getChamberAt(posA);
				MoundChamber chamberB = mound.getChamberAt(posB);
				if (chamberA != null && chamberB != null && chamberA != chamberB) {
					level.setBlock(pos, ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean convertSelfOutside(ServerLevel level, BlockPos pos, RandomSource random, float noiseValue, CellularNoise cellularNoise, Neighbor[] directNeighbors) {
		if (noiseValue < cellularNoise.coreThreshold() || noiseValue > cellularNoise.borderThreshold() * 1.2f) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_CLIENTS);
			return true;
		}
		else if (noiseValue >= cellularNoise.coreThreshold() && noiseValue < cellularNoise.coreThreshold() + 0.001f && random.nextFloat() < 0.4f && LevelUtil.getMaxBrightness(level, pos) < 5) {
			level.setBlock(pos, ModBlocks.BLOOMLIGHT.get().defaultBlockState(), UPDATE_CLIENTS);
			return true;
		}
		else if (noiseValue >= cellularNoise.coreThreshold() && noiseValue <= cellularNoise.borderThreshold() * 0.8f) {
			BlockState blockState = level.random.nextFloat() < 0.75f ? ModBlocks.PRIMAL_FLESH.get().defaultBlockState() : ModBlocks.SMOOTH_PRIMAL_FLESH.get().defaultBlockState();
			level.setBlock(pos, blockState, UPDATE_CLIENTS);
			return true;
		}

		int solidNeighborCount = 0;
		for (Neighbor neighbor : directNeighbors) {
			if (neighbor.state.isFaceSturdy(level, neighbor.pos, neighbor.direction.getOpposite())) solidNeighborCount++;
		}

		// if we are surrounded by sturdy faces turn ourselves into a non-spreading static flesh block
		if (solidNeighborCount > 4 && random.nextFloat() <= solidNeighborCount / 6f) {
			BlockState blockState = level.random.nextFloat() < 0.75f ? ModBlocks.PRIMAL_FLESH.get().defaultBlockState() : ModBlocks.SMOOTH_PRIMAL_FLESH.get().defaultBlockState();
			level.setBlock(pos, blockState, UPDATE_CLIENTS);
			return true;
		}

		return false;
	}

	protected boolean convertSelfInsideMound(ServerLevel level, BlockPos pos, Neighbor[] directNeighbors, MoundShape mound, RandomSource random) {
		int solidNeighborCount = 0;
		for (Neighbor neighbor : directNeighbors) {
			if (neighbor.state.isFaceSturdy(level, neighbor.pos, neighbor.direction.getOpposite())) solidNeighborCount++;
		}

		// if we are surrounded by sturdy faces turn ourselves into a non-spreading static flesh block
		if (solidNeighborCount > 4 && random.nextFloat() <= solidNeighborCount / 6f) {

			float nearLocalShapePct = nearLocalShapePct(pos, mound);

			if (level.random.nextFloat() < nearLocalShapePct + 0.1f) {
				BlockState replacementState = level.random.nextFloat() < nearLocalShapePct ? ModBlocks.SMOOTH_PRIMAL_FLESH.get().defaultBlockState() : ModBlocks.PRIMAL_FLESH.get().defaultBlockState();
				level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);
				return true;
			}
			else if (nearLocalShapePct > 0) {
				int malignantFleshCount = 0;
				for (Neighbor neighbor : directNeighbors) {
					if (neighbor.state.is(ModBlocks.SPREADING_MALIGNANT_FLESH.get())) malignantFleshCount++;
				}

				if (malignantFleshCount < 4) {
					level.setBlock(pos, ModBlocks.POROUS_PRIMAL_FLESH.get().defaultBlockState(), Block.UPDATE_CLIENTS);
					return true;
				}
			}
		}

		return false;
	}

	protected boolean convertSelfInsideChamber(ServerLevel level, BlockPos pos, BlockState state, int charge, Neighbor[] directNeighbors, MoundShape mound, MoundChamber chamber, PrimalEnergyHandler energyHandler, RandomSource random) {
		ChamberDecorator chamberDecorator = chamber.getDecorator();
		ChamberDecorator.PartOfDecorationResult result = chamberDecorator.isBlockPartOfDecoration(chamber, level, pos, state);

		if (!result.positionIsValid) {
			return destroyBlockAndConvertIntoEnergy(level, pos, energyHandler, charge + 8);
		}

		if (!result.materialIsValid && chamberDecorator.canPlace(chamber, level, pos)) {
			return chamberDecorator.place(chamber, level, pos);
		}

		int solidNeighborCount = 0;
		int notInsideChambeWalls = 0;
		for (Neighbor neighbor : directNeighbors) {
			if (neighbor.state.isFaceSturdy(level, neighbor.pos, neighbor.direction.getOpposite())) solidNeighborCount++;
			if (!chamber.contains(neighbor.pos)) notInsideChambeWalls++;
		}

		// if we are surrounded by sturdy faces turn ourselves into a non-spreading static flesh block
		if (solidNeighborCount > 4 && random.nextFloat() <= solidNeighborCount / 6f) {

			BlockState replacementState;
			if (level.random.nextFloat() < nearLocalShapePct(pos, mound)) {
				replacementState = level.random.nextFloat() < 0.75f ? ModBlocks.PRIMAL_FLESH.get().defaultBlockState() : ModBlocks.SMOOTH_PRIMAL_FLESH.get().defaultBlockState();
			}
			else {
				replacementState = ModBlocks.POROUS_PRIMAL_FLESH.get().defaultBlockState();
			}
			level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);

			return true;
		}

		if (notInsideChambeWalls > 2 && solidNeighborCount < 6 && LevelUtil.getMaxBrightness(level, pos) < 5 && random.nextFloat() < 0.2f) {
			level.setBlock(pos, ModBlocks.BLOOMLIGHT.get().defaultBlockState(), Block.UPDATE_CLIENTS);
			return true;
		}

		return false;
	}

	protected void convertNeighborsInsideMound(ServerLevel level, BlockPos pos, Neighbor[] directNeighbors, MoundShape mound, PrimalEnergyHandler energyHandler) {
		if (LevelUtil.isBlockNearby(level, pos, 2, BLOCKS_TO_AVOID_PREDICATE)) return;

		for (Neighbor neighbor : directNeighbors) {
			MoundChamber chamber = mound.getChamberAt(neighbor.pos);
			if (chamber == null || !chamber.contains(neighbor.pos)) {
				if (PrimordialEcosystem.isReplaceable(neighbor.state) && !neighbor.state.is(ModBlocks.BLOOMLIGHT.get())) {
					int charge = Math.max(energyHandler.drainPrimalEnergy(CHARGE.getMax()), CHARGE.getMin() + 1);
					level.setBlock(neighbor.pos, CHARGE.setValue(defaultBlockState(), charge), Block.UPDATE_CLIENTS);
				}
				continue;
			}

			if (PrimordialEcosystem.isReplaceable(neighbor.state) && !neighbor.state.is(ModBlocks.BLOOMLIGHT.get())) {
				ChamberDecorator chamberDecorator = chamber.getDecorator();
				if (chamberDecorator.canPlace(chamber, level, neighbor.pos)) {
					int charge = Math.max(energyHandler.drainPrimalEnergy(CHARGE.getMax()), CHARGE.getMin() + 1);
					level.setBlock(neighbor.pos, CHARGE.setValue(defaultBlockState(), charge), Block.UPDATE_CLIENTS);
				}
			}
		}
	}

	protected int convertNeighborsOutside(ServerLevel level, BlockPos pos, int charge, CellularNoise cellularNoise, Neighbor[] directNeighbors) {
		if (LevelUtil.isBlockNearby(level, pos, 2, BLOCKS_TO_AVOID_PREDICATE)) return 0;

		int usedCharge = 0;

		for (Neighbor neighbor : directNeighbors) {
			float noiseValue = cellularNoise.getValueAtCenter(neighbor.pos);

			if (noiseValue < cellularNoise.coreThreshold() && convertLogIntoPillar(level, neighbor.pos, neighbor.state)) {
				usedCharge++;
			}
			else if (noiseValue >= cellularNoise.coreThreshold() && noiseValue <= cellularNoise.borderThreshold()) {
				if (PrimordialEcosystem.tryToReplaceBlock(level, neighbor.pos, neighbor.state, CHARGE.setValue(defaultBlockState(), charge / 2))) {
					usedCharge++;
				}
			}
		}

		return usedCharge;
	}

	private static float nearLocalShapePct(BlockPos pos, MoundShape mound) {
		Shape boundingShape = mound.getBoundingShapeAt(pos);
		if (boundingShape == null) return 0f;

		double radius = boundingShape instanceof HasRadius sphere ? sphere.getRadius() : boundingShape.getAABB().getSize() / 2;
		double radiusSqr = radius * radius;
		double distSqr = boundingShape.distanceToSqr(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
		return Mth.clamp((float) (1 - distSqr / radiusSqr), 0f, 1f);
	}

	protected boolean destroyBlockAndConvertIntoEnergy(ServerLevel level, BlockPos pos, @Nullable PrimalEnergyHandler energyHandler, int amount) {
		if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS)) {
			if (energyHandler != null) energyHandler.fillPrimalEnergy(amount);
			return true;
		}
		return false;
	}

	protected boolean convertLogIntoPillar(ServerLevel level, BlockPos pos, BlockState state) {
		BlockState replacementState = null;

		if (PrimordialEcosystem.isReplaceableLog(state)) {
			if (state.hasProperty(RotatedPillarBlock.AXIS)) {
				Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
				replacementState = ModBlocks.PRIMAL_FLESH_PILLAR.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
			}
			else replacementState = ModBlocks.PRIMAL_FLESH_WALL.get().defaultBlockState();
		}

		if (replacementState == null) return false;

		return PrimordialEcosystem.tryToReplaceBlock(level, pos, state, replacementState);
	}

	public int increaseNeighborCharge(ServerLevel level, BlockPos pos, final int availableCharge, RandomSource random) {
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
		if (!(state.getBlock() instanceof MalignantFleshBlock)) return 0;

		int currentCharge = getCharge(state);
		if (currentCharge < CHARGE.getMax()) {
			int usedCharge = Math.min(amount, CHARGE.getMax() - currentCharge);
			setCharge(level, pos, state, usedCharge);
			return usedCharge;
		}
		return 0;
	}

	protected record Neighbor(BlockPos pos, Direction direction, BlockState state) {}

}
