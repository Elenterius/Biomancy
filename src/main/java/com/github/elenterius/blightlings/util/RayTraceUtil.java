package com.github.elenterius.blightlings.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

public final class RayTraceUtil {
	private RayTraceUtil() {}

	public static RayTraceResult rayTrace(Entity entity, Predicate<Entity> predicate, double distance) {
		Vector3d startVec = entity.getEyePosition(1f);
		Vector3d lookVec = entity.getLookVec();
		Vector3d directionVec = lookVec.scale(distance);
		Vector3d endVec = startVec.add(directionVec);

		RayTraceResult rayTrace = entity.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
		if (rayTrace.getType() != RayTraceResult.Type.MISS) endVec = rayTrace.getHitVec();

		RayTraceResult entityRayTrace = ProjectileHelper.rayTraceEntities(entity.world, entity, startVec, endVec, entity.getBoundingBox().expand(directionVec).grow(1d), predicate);
		if (entityRayTrace != null) rayTrace = entityRayTrace;

		if (startVec.squareDistanceTo(rayTrace.getHitVec()) > distance * distance) {
			return BlockRayTraceResult.createMiss(rayTrace.getHitVec(), Direction.getFacingFromVector(lookVec.x, lookVec.y, lookVec.z), new BlockPos(rayTrace.getHitVec()));
		} else return rayTrace;
	}

	public static boolean canEntitySeePosition(Entity entity, Vector3d targetPos) {
		Vector3d startVec = entity.getEyePosition(1f);
		BlockRayTraceResult rayTraceResult = entity.world.rayTraceBlocks(new RayTraceContext(startVec, targetPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return true;
		return rayTraceResult.getPos().withinDistance(targetPos, 1.2d);
	}

	@OnlyIn(Dist.CLIENT)
	public static RayTraceResult clientRayTrace(Entity renderViewEntity, float partialTicks, double distance) {
		RayTraceResult blockRayTrace = renderViewEntity.pick(distance, partialTicks, false);
		Vector3d startVec = renderViewEntity.getEyePosition(partialTicks);
		Vector3d lookVec = renderViewEntity.getLook(1.0F);
		Vector3d directionVec = lookVec.scale(distance);
		Vector3d endVec = startVec.add(directionVec);

		double blockDistSq = blockRayTrace.getHitVec().squareDistanceTo(startVec);
		EntityRayTraceResult entityRayTrace = ProjectileHelper.rayTraceEntities(renderViewEntity, startVec, endVec, renderViewEntity.getBoundingBox().expand(directionVec).grow(1d), (target) -> !target.isSpectator() && target.canBeCollidedWith() && target instanceof LivingEntity, blockDistSq);
		if (entityRayTrace != null) {
			Vector3d hitVec = entityRayTrace.getHitVec();
			double entityDistSq = startVec.squareDistanceTo(hitVec);
			if (entityDistSq > distance * distance) {
				return BlockRayTraceResult.createMiss(hitVec, Direction.getFacingFromVector(lookVec.x, lookVec.y, lookVec.z), new BlockPos(hitVec));
			} else if (entityDistSq < blockDistSq) {
				return entityRayTrace;
			}
		}
		return blockRayTrace;
	}
}
