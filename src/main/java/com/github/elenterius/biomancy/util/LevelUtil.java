package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class LevelUtil {

	private LevelUtil() {}

	public static LazyOptional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		BlockState state = level.getBlockState(pos);
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				return blockEntity.getCapability(ModCapabilities.ITEM_HANDLER, direction);
			}
		}
		return LazyOptional.empty();
	}

	public static int getNumOfBlocksAbove(BlockGetter level, BlockPos pos, Block targetBlock, int maxHeight) {
		int i = 0;
		while (i < maxHeight && level.getBlockState(pos.above(i + 1)).getBlock() == targetBlock) i++;
		return i;
	}

	public static int getNumOfBlocksBelow(BlockGetter level, BlockPos pos, Block targetBlock, int maxHeight) {
		int i = 0;
		while (i < maxHeight && level.getBlockState(pos.below(i + 1)).getBlock() == targetBlock) i++;
		return i;
	}

	public static boolean isBlockNearby(ServerLevel level, BlockPos pos, int rangeXZ, int rangeY, Predicate<BlockState> predicate) {
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (int y = 0; y <= rangeY; y = y > 0 ? -y : 1 - y) {
			for (int xz = 0; xz < rangeXZ; ++xz) {
				for (int x = 0; x <= xz; x = x > 0 ? -x : 1 - x) {
					for (int z = x < xz && x > -xz ? xz : 0; z <= xz; z = z > 0 ? -z : 1 - z) {
						mutablePos.setWithOffset(pos, x, y - 1, z);
						if (predicate.test(level.getBlockState(mutablePos))) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean isChunkCloserToPosThan(int chunkX, int chunkZ, BlockPos pos, double distanceSquared) {
		int minBlockX = SectionPos.sectionToBlockCoord(chunkX);
		int minBlockZ = SectionPos.sectionToBlockCoord(chunkZ);
		int maxBlockX = SectionPos.sectionToBlockCoord(chunkX, 15);
		int maxBlockZ = SectionPos.sectionToBlockCoord(chunkZ, 15);

		double closestX = Mth.clamp(pos.getX(), minBlockX, maxBlockX);
		double closestZ = Mth.clamp(pos.getZ(), minBlockZ, maxBlockZ);

		double dx = pos.getX() - closestX;
		double dz = pos.getZ() - closestZ;

		return (dx * dx + dz * dz) < distanceSquared;
	}

	/**
	 * performance: vroom vroom
	 */
	@Nullable
	public static <T extends BlockEntity> T findNearestBlockEntity(ServerLevel level, BlockPos pos, int searchDist, Class<T> clazz) {
		if (searchDist <= 0) return null;

		final int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		final int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());

		final int searchDistSqr = searchDist * searchDist;
		final int chunkSearchDist = Mth.ceil(searchDist / 16f);

		@Nullable T nearestBlockEntity = null;
		double nearestDistSqr = searchDistSqr + 0.1d;

		for (int chunkDist = 0; chunkDist <= chunkSearchDist; chunkDist++) {

			for (int x = 0; x <= chunkDist; x = x > 0 ? -x : 1 - x) {
				for (int z = x < chunkDist && x > -chunkDist ? chunkDist : 0; z <= chunkDist; z = z > 0 ? -z : 1 - z) {

					if (!level.hasChunk(chunkX + x, chunkZ + z)) continue;

					if (!isChunkCloserToPosThan(chunkX + x, chunkZ + z, pos, nearestDistSqr)) continue;

					LevelChunk chunk = level.getChunk(chunkX + x, chunkZ + z);
					for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
						if (!blockEntity.isRemoved() && clazz.isInstance(blockEntity)) {
							double distSqr = blockEntity.getBlockPos().distSqr(pos);
							if (distSqr <= searchDistSqr && distSqr < nearestDistSqr) {
								nearestBlockEntity = clazz.cast(blockEntity);
								nearestDistSqr = distSqr;
							}
						}
					}
				}
			}
		}

		return nearestBlockEntity;
	}

}
