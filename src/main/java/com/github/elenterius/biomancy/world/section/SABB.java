package com.github.elenterius.biomancy.world.section;

import com.github.elenterius.biomancy.world.ChunkPosConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Section Aligned Bounding Box
 *
 * @param packedPosMin inclusie
 * @param packedPosMax inclusive
 * @implNote we store max and min SectionPos as packed long to safe 64 bits
 */
public record SABB(long packedPosMin, long packedPosMax) {

	public static SABB from(SectionPos posA, SectionPos posB) {
		int minX = Math.min(posA.x(), posB.x());
		int minY = Math.min(posA.y(), posB.y());
		int minZ = Math.min(posA.z(), posB.z());
		int maxX = Math.max(posA.x(), posB.x());
		int maxY = Math.max(posA.y(), posB.y());
		int maxZ = Math.max(posA.z(), posB.z());

		long min = SectionPos.asLong(minX, minY, minZ);
		long max = SectionPos.asLong(maxX, maxY, maxZ);

		return new SABB(min, max);
	}

	public static SABB from(BlockPos posA, BlockPos posB) {
		int minX = Math.min(posA.getX(), posB.getX());
		int minY = Math.min(posA.getY(), posB.getY());
		int minZ = Math.min(posA.getZ(), posB.getZ());
		int maxX = Math.max(posA.getX(), posB.getX());
		int maxY = Math.max(posA.getY(), posB.getY());
		int maxZ = Math.max(posA.getZ(), posB.getZ());

		long min = SectionPos.asLong(
				SectionPos.blockToSectionCoord(minX),
				SectionPos.blockToSectionCoord(minY),
				SectionPos.blockToSectionCoord(minZ)
		);
		long max = SectionPos.asLong(
				SectionPos.blockToSectionCoord(maxX),
				SectionPos.blockToSectionCoord(maxY),
				SectionPos.blockToSectionCoord(maxZ)
		);

		return new SABB(min, max);
	}

	public static SABB from(AABB boundingBox) {
		long min = SectionPos.asLong(
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.minX)),
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.minY)),
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.minZ))
		);
		long max = SectionPos.asLong(
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.maxX)),
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.maxY)),
				SectionPos.blockToSectionCoord(Mth.floor(boundingBox.maxZ))
		);

		return new SABB(min, max);
	}

	public int getMinX() {
		return SectionPos.x(packedPosMin);
	}

	public int getMinY() {
		return SectionPos.y(packedPosMin);
	}

	public int getMinZ() {
		return SectionPos.z(packedPosMin);
	}

	public int getMaxX() {
		return SectionPos.x(packedPosMax);
	}

	public int getMaxY() {
		return SectionPos.y(packedPosMax);
	}

	public int getMaxZ() {
		return SectionPos.z(packedPosMax);
	}

	public SectionPos getMinPos() {
		return SectionPos.of(packedPosMin);
	}

	public SectionPos getMaxPos() {
		return SectionPos.of(packedPosMax);
	}

	public long getSize() {
		long sizeX = getSizeX();
		long sizeY = getSizeY();
		long sizeZ = getSizeZ();
		return sizeX * sizeY * sizeZ;
	}

	public int getSizeX() {
		int minX = SectionPos.x(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		return (maxX - minX + 1);
	}

	public int getSizeY() {
		int minY = SectionPos.y(packedPosMin);
		int maxY = SectionPos.y(packedPosMax);
		return (maxY - minY + 1);
	}

	public int getSizeZ() {
		int minZ = SectionPos.z(packedPosMin);
		int maxZ = SectionPos.z(packedPosMax);
		return (maxZ - minZ + 1);
	}

	public boolean contains(BlockPos pos) {
		return containsSection(
				SectionPos.blockToSectionCoord(pos.getX()),
				SectionPos.blockToSectionCoord(pos.getY()),
				SectionPos.blockToSectionCoord(pos.getZ())
		);
	}

	public boolean containsSection(int x, int y, int z) {
		int minX = SectionPos.x(packedPosMin);
		int minY = SectionPos.y(packedPosMin);
		int minZ = SectionPos.z(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		int maxY = SectionPos.y(packedPosMax);
		int maxZ = SectionPos.z(packedPosMax);
		return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
	}

	public void forEachSection(SectionPosConsumer action) {
		int minX = SectionPos.x(packedPosMin);
		int minY = SectionPos.y(packedPosMin);
		int minZ = SectionPos.z(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		int maxY = SectionPos.y(packedPosMax);
		int maxZ = SectionPos.z(packedPosMax);

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					action.accept(x, y, z);
				}
			}
		}
	}

	public void forEachChunk(ChunkPosConsumer action) {
		int minX = SectionPos.x(packedPosMin);
		int minZ = SectionPos.z(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		int maxZ = SectionPos.z(packedPosMax);

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				action.accept(x, z);
			}
		}
	}

	public Stream<SectionPos> stream() {
		int minX = SectionPos.x(packedPosMin);
		int minY = SectionPos.y(packedPosMin);
		int minZ = SectionPos.z(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		int maxY = SectionPos.y(packedPosMax);
		int maxZ = SectionPos.z(packedPosMax);
		long size = (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

		return StreamSupport.stream(new Spliterators.AbstractSpliterator<>(size, Spliterator.SIZED) {
			final Cursor3D cursor = new Cursor3D(minX, minY, minZ, maxX, maxY, maxZ);

			public boolean tryAdvance(Consumer<? super SectionPos> action) {
				if (cursor.advance()) {
					action.accept(SectionPos.of(cursor.nextX(), cursor.nextY(), cursor.nextZ()));
					return true;
				}
				return false;
			}
		}, false);
	}

	@Override
	public String toString() {
		int minX = SectionPos.x(packedPosMin);
		int minY = SectionPos.y(packedPosMin);
		int minZ = SectionPos.z(packedPosMin);
		int maxX = SectionPos.x(packedPosMax);
		int maxY = SectionPos.y(packedPosMax);
		int maxZ = SectionPos.z(packedPosMax);
		long size = ((long) maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

		return "SectionABB{" +
				"minX=" + minX +
				"minY=" + minY +
				"minZ=" + minZ +
				"maxX=" + maxX +
				"maxY=" + maxY +
				"maxZ=" + maxZ +
				", size=" + size +
				'}';
	}

}
