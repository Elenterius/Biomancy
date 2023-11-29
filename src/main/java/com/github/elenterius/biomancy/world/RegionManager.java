package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionManager {

	private RegionManager() {}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel) {
			RegionStore regionStore = RegionStore.loadOrGet(serverLevel);
			regionStore.unloadChunk(event.getChunk());
		}
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel) {
			RegionStore regionStore = RegionStore.loadOrGet(serverLevel);
			regionStore.loadChunk(event.getChunk());
		}
	}

	public static @Nullable Shape getOrCreateShapeRegion(ServerLevel level, BlockPos regionOrigin, Supplier<Shape> supplier) {
		RegionStore regionStore = RegionStore.loadOrGet(level);
		Region region = regionStore.getOrCreate(regionOrigin, pos -> new ShapeRegion(pos, supplier.get()));
		return region instanceof ShapeRegion shapeRegion ? shapeRegion.getShape() : null;
	}

	public static void remove(ServerLevel level, BlockPos regionOrigin) {
		RegionStore regionStore = RegionStore.loadOrGet(level);
		regionStore.remove(regionOrigin);
	}

	@Nullable
	public static Shape getClosestShape(ServerLevel level, BlockPos blockPos) {
		RegionStore regionStore = RegionStore.loadOrGet(level);

		Vec3 position = Vec3.atCenterOf(blockPos);
		double minDistSqr = Double.MAX_VALUE;
		Shape closestShape = null;

		Set<Region> regions = regionStore.getRegionsAt(blockPos);
		if (regions == null) return null;

		for (Region region : regions) {
			if (region.contains(position) && region instanceof ShapeRegion shapeRegion) {
				Shape shape = shapeRegion.getShape();
				double distSqr = shape.distanceToSqr(position.x, position.y, position.z);
				if (distSqr < minDistSqr) {
					closestShape = shape;
					minDistSqr = distSqr;
				}
			}
		}

		return closestShape;
	}

}
