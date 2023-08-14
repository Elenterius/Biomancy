package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.FleshVeinsBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class PrimordialEcosystem {
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

	public static void spreadMalignantVeinsFromSource(ServerLevel level, BlockPos pos) {
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		BlockState state = level.getBlockState(pos);
		RandomSource random = level.random;

		if (random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
		if (random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
		if (random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
		if (random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
		level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);

		increaseMalignantVeinsChargeAroundPos(level, pos, random);
	}

	public static void increaseMalignantVeinsChargeAroundPos(ServerLevel level, BlockPos pos, RandomSource random) {
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		for (Direction subDirection : Direction.allShuffled(random)) {
			BlockPos neighborPos = pos.relative(subDirection);
			BlockState neighborState = level.getBlockState(neighborPos);
			veinsBlock.increaseCharge(level, neighborPos, neighborState, random.nextIntBetweenInclusive(1, 3));
		}
	}

}
