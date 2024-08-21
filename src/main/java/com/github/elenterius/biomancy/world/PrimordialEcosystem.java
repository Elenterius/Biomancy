package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.entity.mob.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.random.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class PrimordialEcosystem {

	private static final RandomSource random = RandomSource.create();
	public static final IntSupplier MAX_CHARGE_SUPPLIER = () -> 15;
	public static final Set<Block> MALIGNANT_UPGRADE_TARGETS = Set.of(ModBlocks.MALIGNANT_FLESH_SLAB.get(), ModBlocks.MALIGNANT_FLESH_STAIRS.get());
	//	public static final Set<Block> POROUS_UPGRADE_TARGETS = Set.of(ModBlocks.POROUS_PRIMAL_FLESH_SLAB.get(), ModBlocks.POROUS_PRIMAL_FLESH_STAIRS.get());
	//	public static final Set<Block> SMOOTH_UPGRADE_TARGETS = Set.of(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB.get(), ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS.get());

	public static final Set<Block> FULL_FLESH_BLOCKS = Set.of(
			ModBlocks.MALIGNANT_FLESH.get(),
			ModBlocks.PRIMAL_FLESH.get(),
			ModBlocks.SMOOTH_PRIMAL_FLESH.get(),
			ModBlocks.POROUS_PRIMAL_FLESH.get(),
			ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get()
	);

	public static final Set<Block> SOLID_FLESH_BLOCKS = Set.of(
			ModBlocks.MALIGNANT_FLESH.get(),
			ModBlocks.PRIMAL_FLESH.get(),
			ModBlocks.SMOOTH_PRIMAL_FLESH.get(),
			ModBlocks.POROUS_PRIMAL_FLESH.get()
	);

	private PrimordialEcosystem() {}

	public static void placeMalignantBlocksOnLivingDeath(ServerLevel level, LivingEntity livingEntity) {
		BlockPos pos = livingEntity.getOnPos().above();

		float volume = MobUtil.getVolume(livingEntity);
		float referenceVolume = MobUtil.getVolume(EntityType.PLAYER);
		float pct = volume / referenceVolume;

		if (!PrimordialEcosystem.placeMalignantBlocks(level, pos, livingEntity.getRandom(), pct)) {
			for (int i = 0; i < 4; i++) {
				BlockPos relativePos = pos.relative(Direction.from2DDataValue(i));
				if (PrimordialEcosystem.placeMalignantBlocks(level, relativePos, livingEntity.getRandom(), pct)) break;
			}
		}
	}

	public static boolean placeMalignantBlocks(ServerLevel level, BlockPos pos, RandomSource random, float chargePct) {
		BlockState currentState = level.getBlockState(pos);
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		if (currentState.is(veinsBlock)) {
			if (veinsBlock.getSpreader().spreadAll(currentState, level, pos, false) > 0) {
				for (Direction subDirection : Direction.allShuffled(random)) {
					BlockPos neighborPos = pos.relative(subDirection);
					BlockState neighborState = level.getBlockState(neighborPos);
					veinsBlock.increaseCharge(level, neighborPos, neighborState, 1);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			else {
				Block block = chargePct < 1f ? ModBlocks.MALIGNANT_FLESH_SLAB.get() : ModBlocks.MALIGNANT_FLESH.get();
				level.setBlockAndUpdate(pos, block.defaultBlockState());
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			return true;
		}
		else if (currentState.is(ModBlocks.MALIGNANT_FLESH_SLAB.get())) {
			if (currentState.getValue(DirectionalSlabBlock.TYPE) == DirectionalSlabType.FULL) return false;

			level.setBlockAndUpdate(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());

			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			return true;
		}
		else if (currentState.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
			BlockState stateForPlacement = veinsBlock.getStateForPlacement(currentState, level, pos, Direction.DOWN, Math.max(1, Mth.ceil(chargePct * 15)));
			if (stateForPlacement != null) {
				level.setBlockAndUpdate(pos, stateForPlacement);
				if (random.nextFloat() < chargePct) {
					veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, pos, random);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
		}

		return false;
	}

	public static <T extends FleshBlob & PrimordialFleshkin> boolean placeMalignantBlocks(ServerLevel level, BlockPos pos, T fleshBlob) {
		BlockState currentState = level.getBlockState(pos);
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		RandomSource random = fleshBlob.getRandom();

		if (currentState.is(veinsBlock)) {
			if (veinsBlock.getSpreader().spreadAll(currentState, level, pos, false) > 0) {
				for (Direction subDirection : Direction.allShuffled(random)) {
					BlockPos neighborPos = pos.relative(subDirection);
					BlockState neighborState = level.getBlockState(neighborPos);
					veinsBlock.increaseCharge(level, neighborPos, neighborState, 1);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			else {
				Block block = fleshBlob.getBlobSize() < FleshBlob.MAX_SIZE / 2f ? ModBlocks.MALIGNANT_FLESH_SLAB.get() : ModBlocks.MALIGNANT_FLESH.get();
				level.setBlockAndUpdate(pos, block.defaultBlockState());
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			return true;
		}
		else if (currentState.is(ModBlocks.MALIGNANT_FLESH_SLAB.get())) {
			if (currentState.getValue(DirectionalSlabBlock.TYPE) == DirectionalSlabType.FULL) return false;

			level.setBlockAndUpdate(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());

			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			return true;
		}
		else if (currentState.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
			BlockState stateForPlacement = veinsBlock.getStateForPlacement(currentState, level, pos, Direction.DOWN, fleshBlob.getBlobSize() * 2);
			if (stateForPlacement != null) {
				level.setBlockAndUpdate(pos, stateForPlacement);
				if (random.nextFloat() < (float) fleshBlob.getBlobSize() / FleshBlob.MAX_SIZE) {
					veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, pos, random);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
		}

		return false;
	}

	public static boolean placeBloomOrBlocks(ServerLevel level, BlockPos pos, Direction direction) {

		BlockState state = level.getBlockState(pos);
		if (MALIGNANT_UPGRADE_TARGETS.contains(state.getBlock())) {
			level.setBlock(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState(), Block.UPDATE_CLIENTS);
			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			return true;
		}

		BlockPos relativePos = pos.relative(direction);
		BlockState relativeState = level.getBlockState(relativePos);
		RandomSource random = level.getRandom();

		if (random.nextFloat() < 0.7f && ModBlocks.PRIMAL_BLOOM.get().mayPlaceOn(level, pos, state, direction)) {
			boolean canBeReplaced = relativeState.canBeReplaced(new DirectionalPlaceContext(level, relativePos, direction.getOpposite(), ItemStack.EMPTY, direction));
			boolean noBloomNearby = !LevelUtil.isBlockNearby(level, pos, 4, blockState -> blockState.is(ModBlocks.PRIMAL_BLOOM.get()));
			if (canBeReplaced && noBloomNearby) {
				BlockState blockState = ModBlocks.PRIMAL_BLOOM.get().getStateForPlacement(level, relativePos, direction);
				level.playSound(null, relativePos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.5f + random.nextFloat() * 0.5f);
				level.setBlock(relativePos, blockState, Block.UPDATE_CLIENTS);
				return true;
			}
		}

		if (relativeState.is(ModBlocks.MALIGNANT_FLESH_VEINS.get())) {
			if (PrimordialEcosystem.tryToReplaceBlock(level, relativePos, ModBlocks.PRIMAL_FLESH.get().defaultBlockState())) {
				level.playSound(null, relativePos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
			else if (FleshVeinsBlock.convert(relativeState, level, relativePos, 0, null, 0.5f, null)) {
				level.playSound(null, relativePos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}

			return PrimordialEcosystem.spreadMalignantVeinsFromSource(level, relativePos, PrimordialEcosystem.MAX_CHARGE_SUPPLIER);
		}
		else if (relativeState.canBeReplaced(new DirectionalPlaceContext(level, relativePos, direction.getOpposite(), ItemStack.EMPTY, direction))) {
			FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
			BlockState stateForPlacement = veinsBlock.getStateForPlacement(relativeState, level, relativePos, direction.getOpposite(), 15);
			if (stateForPlacement != null) {
				level.setBlock(relativePos, stateForPlacement, Block.UPDATE_CLIENTS);
				for (int i = 0; i < 4; i++) {
					if (random.nextFloat() < 0.4f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, relativePos, random);
				}
				level.playSound(null, relativePos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
		}

		return false;
	}

	public static boolean spreadMalignantVeinsFromSource(ServerLevel level, BlockPos pos, IntSupplier chargeSupplier) {
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		BlockState state = level.getBlockState(pos);
		RandomSource random = level.random;

		boolean hasPlacedVeins = false;

		for (int i = 0; i < 4; i++) {
			if (random.nextFloat() < 0.6f) {
				Optional<MultifaceSpreader.SpreadPos> spreadPos = veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
				if (spreadPos.isPresent()) hasPlacedVeins = true;
			}
		}

		increaseMalignantVeinsChargeAroundPos(level, pos, chargeSupplier);
		return hasPlacedVeins;
	}

	public static int increaseMalignantVeinsChargeAroundPos(ServerLevel level, BlockPos pos, IntSupplier chargeSupplier) {
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		int usedCharge = 0;

		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					BlockPos neighborPos = pos.offset(x, y, z);
					BlockState neighborState = level.getBlockState(neighborPos);
					usedCharge += veinsBlock.increaseCharge(level, neighborPos, neighborState, chargeSupplier.getAsInt());
				}
			}
		}

		return usedCharge;
	}

	public static int countMalignantChargeAroundPos(ServerLevel level, BlockPos pos) {
		int totalCharge = 0;

		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					BlockPos neighborPos = pos.offset(x, y, z);
					BlockState neighborState = level.getBlockState(neighborPos);
					totalCharge += neighborState.getOptionalValue(ModBlockProperties.CHARGE.get()).orElse(0);
				}
			}
		}

		return totalCharge;
	}

	public static int countMalignantVeinsAroundPos(ServerLevel level, BlockPos pos) {
		int veins = 0;

		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					if (level.getBlockState(pos.offset(x, y, z)).is(ModBlocks.MALIGNANT_FLESH_VEINS.get())) veins += 1;
				}
			}
		}

		return veins;
	}

	private static double step(double value, double threshold) {
		return value >= threshold ? 1 : 0;
	}

	public static void generateHollowVoronoiSphere(ServerLevel level, BlockPos pos, int radius, BlockState state) {
		CellularNoise noise = createPreconfiguredCellularNoise((int) Mth.getSeed(pos));

		for (int i = 0; i <= radius; i = i > 0 ? -i : 1 - i) {
			int currRadius = radius - Math.abs(i) / 2;

			int x = -currRadius;
			int z = 0;
			int error = 2 - 2 * currRadius;

			do {
				trySetBlock(level, pos, state, noise, -x, i, z);
				trySetBlock(level, pos, state, noise, -z, i, -x);
				trySetBlock(level, pos, state, noise, x, i, -z);
				trySetBlock(level, pos, state, noise, z, i, x);
				currRadius = error;
				if (currRadius <= z) error += ++z * 2 + 1;
				if (currRadius > x || error > z) error += ++x * 2 + 1;
			} while (x < 0);
		}
	}

	private static void trySetBlockInCellularLevel(ServerLevel level, BlockPos pos, BlockState placementState, int x, int y, int z) {
		Noise noise = getCellularNoise(level);
		float borderThreshold = 0.15f;
		float n = noise.getValue(x + 0.5f, y + 0.5f, z + 0.5f);
		if (n >= borderThreshold) {
			tryToReplaceBlock(level, pos.offset(x, y, z), placementState);
		}
	}

	private static void trySetBlock(ServerLevel level, BlockPos pos, BlockState placementState, CellularNoise noise, int x, int y, int z) {
		float borderThreshold = 0.12f;
		float n = noise.getValue(x + 0.5f, y + 0.5f, z + 0.5f);
		if (n >= borderThreshold) {
			tryToReplaceBlock(level, pos.offset(x, y, z), placementState);
		}
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState replacementState) {
		BlockState state = level.getBlockState(pos);
		if (isReplaceable(state)) {
			level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState state, BlockState replacementState) {
		if (isReplaceable(state)) {
			level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState state, Supplier<BlockState> replacementStateSupplier) {
		if (isReplaceable(state)) {
			level.setBlock(pos, replacementStateSupplier.get(), Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}

	public static boolean isReplaceable(BlockState state) {
		return state.canBeReplaced() || state.is(ModBlockTags.FLESH_REPLACEABLE);
	}

	public static boolean isReplaceableLog(BlockState state) {
		return isReplaceable(state) && state.is(BlockTags.OVERWORLD_NATURAL_LOGS);
	}

	public static RandomSource getRandomWithSeed(BlockPos pos) {
		random.setSeed(Mth.getSeed(pos));
		return random;
	}

	public static CellularNoise getCellularNoise(ServerLevel level) {
		return ((CellularNoiseProvider) level).biomancy$getCellularNoise();
	}

	public static CellularNoise createPreconfiguredCellularNoise(int seed) {
		FastNoiseLite cellularNoise = new FastNoiseLite(seed);
		cellularNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
		cellularNoise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
		cellularNoise.SetFrequency(0.05f);

		FastNoiseLite domainWarp = new FastNoiseLite(seed);
		domainWarp.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
		domainWarp.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
		domainWarp.SetDomainWarpAmp(50f);
		domainWarp.SetFrequency(0.005f);

		return new CellularNoiseWithDomainWarp(cellularNoise, domainWarp, 0.16f, 0.13f);
	}

}
