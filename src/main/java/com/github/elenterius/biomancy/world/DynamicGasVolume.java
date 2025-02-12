package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

public class DynamicGasVolume {

	private final RandomSource random;

	//	private final Long2ObjectMap<Voxel> volume = new Long2ObjectOpenHashMap<>();
	private final LongSet volume = new LongOpenHashSet();

	private BlockPos origin;
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;


	public DynamicGasVolume(RandomSource random) {
		this(BlockPos.ZERO, random);
	}

	public DynamicGasVolume(BlockPos origin, RandomSource random) {
		this.random = random;
		this.origin = origin;
		this.minX = origin.getX();
		this.minY = origin.getY();
		this.minZ = origin.getZ();
		this.maxX = origin.getX();
		this.maxY = origin.getY();
		this.maxZ = origin.getZ();
	}

	protected final long getKey(BlockPos pos) {
		return pos.asLong();
	}

	protected final long getKey(int x, int y, int z) {
		return BlockPos.asLong(x, y, z);
	}

	public LongSet getPackedPositions() {
		return volume;
	}

	public boolean isEmpty() {
		return volume.isEmpty();
	}

	public boolean contains(Vec3 pos) {
		return contains(pos.x, pos.y, pos.z);
	}

	public boolean contains(double x, double y, double z) {
		return contains(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	public boolean contains(Vec3i pos) {
		return contains(pos.getX(), pos.getY(), pos.getZ());
	}

	private boolean contains(int x, int y, int z) {
		if (volume.isEmpty()) return false;

		if (x >= minX && x <= maxX && z >= minZ && z <= maxZ && y >= minY && y <= maxY) {
			return volume.contains(getKey(x, y, z));
		}

		return false;
	}

	public boolean intersects(AABB aabb) {
		if (volume.isEmpty()) return false;

		int minX = Mth.floor(aabb.minX);
		int minY = Mth.floor(aabb.minY);
		int minZ = Mth.floor(aabb.minZ);
		int maxX = Mth.floor(aabb.maxX);
		int maxY = Mth.floor(aabb.maxY);
		int maxZ = Mth.floor(aabb.maxZ);

		boolean isBoundsIntersected = this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ && this.maxY >= minY && this.minY <= maxY;
		if (!isBoundsIntersected) return false;

		minX = Math.max(this.minX, minX);
		minY = Math.max(this.minY, minY);
		minZ = Math.max(this.minZ, minZ);
		maxX = Math.min(this.maxX, maxX);
		maxY = Math.min(this.maxY, maxY);
		maxZ = Math.min(this.maxZ, maxZ);

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (volume.contains(getKey(x, y, z))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public int sizeX() {
		return maxX - minX + 1;
	}

	public int sizeY() {
		return maxY - minY + 1;
	}

	public int sizeZ() {
		return maxZ - minZ + 1;
	}

	public void clear() {
		volume.clear();
		minX = origin.getX();
		minY = origin.getY();
		minZ = origin.getZ();
		maxX = origin.getX();
		maxY = origin.getY();
		maxZ = origin.getZ();
	}

	public AABB toAABB() {
		return new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
	}

	public void update(Level level, BlockPos origin, float radius, float propagationProgress) {
		this.origin = origin;

		float a = radius, c = radius * 0.8f;
		float spheroidVolume = (4f / 3f) * Mth.PI * (a * a * c);
		float a2 = a * a, c2 = c * c;

		int desiredVolume = Mth.floor(spheroidVolume * propagationProgress);

		if (desiredVolume <= 0) {
			clear();
			return;
		}

		propagateGas(new OcclusionCache(level), desiredVolume, (sourceVoxel, pos, depth) -> {
			int dx = this.origin.getX() - pos.getX();
			int dy = this.origin.getY() - pos.getY();
			int dz = this.origin.getZ() - pos.getZ();
			float cost = (dx * dx + dz * dz) / a2 + (dy * dy) / c2;

			if (depth > Mth.ceil(radius * 2)) {
				cost = depth;
			}

			if (pos.getY() < this.origin.getY()) {
				cost *= 1.001f;
			}

			if (random.nextFloat() < 0.4f) {
				cost *= 1.002f;
			}

			return cost;
		});
	}

	protected void propagateGas(OcclusionCache obstacleCache, int desiredVolume, CostFunction costFunc) {
		clear();

		LongSet visited = new LongOpenHashSet();
		PriorityQueue<Voxel> priorityQueue = new PriorityQueue<>(Voxel.INCREASING_COST_COMPARATOR);

		Voxel originVoxel = new Voxel(origin, 0, 0);
		priorityQueue.add(originVoxel);

		while (volume.size() < desiredVolume && !priorityQueue.isEmpty()) {
			Voxel voxel = priorityQueue.poll();

			volume.add(getKey(voxel.pos));

			//update bounding box
			minX = Math.min(minX, voxel.x());
			minY = Math.min(minY, voxel.y());
			minZ = Math.min(minZ, voxel.z());
			maxX = Math.max(maxX, voxel.x());
			maxY = Math.max(maxY, voxel.y());
			maxZ = Math.max(maxZ, voxel.z());

			for (Direction direction : obstacleCache.getTraversableDirectionsInsideOfBlock(voxel.pos, random)) {
				BlockPos neighborPos = voxel.pos.relative(direction);
				long key = getKey(neighborPos);

				if (visited.contains(key)) continue;
				visited.add(key);

				if (obstacleCache.isTraversableFrom(voxel.pos, direction, neighborPos)) {
					int depth = voxel.depth + 1;
					priorityQueue.add(new Voxel(neighborPos, costFunc.apply(voxel, neighborPos, depth), depth));
				}
			}
		}
	}

	interface CostFunction {
		float apply(Voxel sourcePos, BlockPos pos, int depth);
	}

	protected record OcclusionCache(Level level, Long2ObjectMap<OcclusionData> obstacleCache) {

		OcclusionCache(Level level) {
			this(level, new Long2ObjectOpenHashMap<>());
		}

		public Collection<Direction> getTraversableDirectionsInsideOfBlock(BlockPos pos, RandomSource random) {
			long key = pos.asLong();

			if (!obstacleCache.containsKey(key)) {
				return Direction.allShuffled(random);
			}

			ObjectArrayList<Direction> traversableDirections = new ObjectArrayList<>();
			OcclusionData obstacle = obstacleCache.get(key);

			for (Direction direction : Direction.values()) {
				if (obstacle.faceData[direction.get3DDataValue()] == 0) {
					traversableDirections.add(direction);
				}
			}
			Util.shuffle(traversableDirections, random);

			return traversableDirections;
		}

		public boolean isTraversableFrom(BlockPos origin, Direction direction, BlockPos pos) {
			OcclusionData obstacle = obstacleCache.computeIfAbsent(pos.asLong(), key -> new OcclusionData());
			return obstacle.isDirectionUnobstructed(level, origin, direction, pos);
		}

		static class OcclusionData {
			private byte[] faceData = null;

			boolean isDirectionUnobstructed(Level level, BlockPos origin, Direction direction, BlockPos pos) {
				if (faceData == null) {
					computeFaceData(level, pos);
				}

				Direction facing = direction.getOpposite();
				return faceData[facing.get3DDataValue()] == 0;
			}

			private void computeFaceData(Level level, BlockPos pos) {
				BlockState state = level.getBlockState(pos);

				if (state.isAir() || state.is(ModBlockTags.ALLOW_GAS_TO_PASS_THROUGH)) {
					faceData = new byte[6];
					return;
				}

				if (level.getFluidState(pos).getHeight(level, pos) >= 0.75f) {
					faceData = new byte[]{1, 1, 1, 1, 1, 1};
					return;
				}

				if (state.isCollisionShapeFullBlock(level, pos)) {
					faceData = new byte[]{1, 1, 1, 1, 1, 1};
					return;
				}

				VoxelShape collisionShape = state.getCollisionShape(level, pos);
				if (collisionShape.isEmpty()) {
					faceData = new byte[6];
					return;
				}

				BlockPos.MutableBlockPos adjacentPos = new BlockPos.MutableBlockPos();
				faceData = new byte[6];
				for (Direction facing : Direction.values()) {
					adjacentPos.setWithOffset(pos, facing);
					VoxelShape adjacentCollisionShape = level.getBlockState(adjacentPos).getCollisionShape(level, adjacentPos);

					boolean canPassThroughBlockFace = !Shapes.mergedFaceOccludes(collisionShape, adjacentCollisionShape, facing);
					if (canPassThroughBlockFace) {
						if (collisionShape.getFaceShape(facing).isEmpty()) {
							//glass panes, walls, biomancy doors, etc.
							faceData[facing.get3DDataValue()] = shapeOccludesPlane(collisionShape, facing) ? (byte) 1 : 0;
						}
						else faceData[facing.get3DDataValue()] = (byte) 0;
					}
					else faceData[facing.get3DDataValue()] = (byte) 1;
				}
			}

			/// only applicable if there exists no face shape
			private static boolean shapeOccludesPlane(VoxelShape shape, Direction planeNormal) {
				return switch (planeNormal.getAxis()) {
					case X -> shape.min(Direction.Axis.Y) <= 0 && shape.max(Direction.Axis.Y) >= 1 && shape.min(Direction.Axis.Z) <= 0 && shape.max(Direction.Axis.Z) >= 1;
					case Y -> shape.min(Direction.Axis.X) <= 0 && shape.max(Direction.Axis.X) >= 1 && shape.min(Direction.Axis.Z) <= 0 && shape.max(Direction.Axis.Z) >= 1;
					case Z -> shape.min(Direction.Axis.X) <= 0 && shape.max(Direction.Axis.X) >= 1 && shape.min(Direction.Axis.Y) <= 0 && shape.max(Direction.Axis.Y) >= 1;
				};
			}
		}
	}


	public static class Voxel {
		public static Comparator<Voxel> INCREASING_COST_COMPARATOR = Comparator.comparing(Voxel::cost, Float::compare);

		private final BlockPos pos;
		private float cost;
		private int depth;


		public Voxel(BlockPos pos, float cost, int depth) {
			this.pos = pos;
			this.cost = cost;
			this.depth = depth;
		}

		public float cost() {
			return cost;
		}

		public int depth() {
			return depth;
		}

		public int x() {
			return pos.getX();
		}

		public int y() {
			return pos.getY();
		}

		public int z() {
			return pos.getZ();
		}

		public BlockPos pos() {
			return pos;
		}

		@Override
		public int hashCode() {
			return Objects.hash(pos.asLong(), cost);
		}

	}

}
