package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.util.random.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class PrimordialEcosystem {

	public static final IntSupplier MAX_CHARGE_SUPPLIER = () -> 15;

	private PrimordialEcosystem() {}

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
		float n = noise.getValue(x, y, z);
		if (n >= borderThreshold) {
			tryToReplaceBlock(level, pos.offset(x, y, z), placementState);
		}
	}

	private static void trySetBlock(ServerLevel level, BlockPos pos, BlockState placementState, CellularNoise noise, int x, int y, int z) {
		float borderThreshold = 0.12f;
		float n = noise.getValue(x, y, z);
		if (n >= borderThreshold) {
			tryToReplaceBlock(level, pos.offset(x, y, z), placementState);
		}
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState replacementState) {
		BlockState state = level.getBlockState(pos);
		if (state.getMaterial().isReplaceable() || state.is(ModBlockTags.PRIMORDIAL_ECO_SYSTEM_REPLACEABLE)) {
			level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState state, BlockState replacementState) {
		if (state.getMaterial().isReplaceable() || state.is(ModBlockTags.PRIMORDIAL_ECO_SYSTEM_REPLACEABLE)) {
			level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}

	public static boolean tryToReplaceBlock(ServerLevel level, BlockPos pos, BlockState state, Supplier<BlockState> replacementStateSupplier) {
		if (state.getMaterial().isReplaceable() || state.is(ModBlockTags.PRIMORDIAL_ECO_SYSTEM_REPLACEABLE)) {
			level.setBlock(pos, replacementStateSupplier.get(), Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
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

		return new CellularNoiseWithDomainWarp(cellularNoise, domainWarp);
	}
}
