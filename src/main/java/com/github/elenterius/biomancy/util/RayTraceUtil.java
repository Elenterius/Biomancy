package com.github.elenterius.biomancy.util;

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
		Vector3d lookVec = entity.getLookAngle();
		Vector3d directionVec = lookVec.scale(distance);
		Vector3d endVec = startVec.add(directionVec);

		RayTraceResult rayTrace = entity.level.clip(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
		if (rayTrace.getType() != RayTraceResult.Type.MISS) endVec = rayTrace.getLocation();

		RayTraceResult entityRayTrace = ProjectileHelper.getEntityHitResult(entity.level, entity, startVec, endVec, entity.getBoundingBox().expandTowards(directionVec).inflate(1d), predicate);
		if (entityRayTrace != null) rayTrace = entityRayTrace;

		if (startVec.distanceToSqr(rayTrace.getLocation()) > distance * distance) {
			return BlockRayTraceResult.miss(rayTrace.getLocation(), Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(rayTrace.getLocation()));
		}
		else return rayTrace;
	}

	public static boolean canEntitySeePosition(Entity entity, Vector3d targetPos) {
		Vector3d startVec = entity.getEyePosition(1f);
		BlockRayTraceResult rayTraceResult = entity.level.clip(new RayTraceContext(startVec, targetPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return true;
		return rayTraceResult.getBlockPos().closerThan(targetPos, 1.2d);
	}

	@OnlyIn(Dist.CLIENT)
	public static RayTraceResult clientRayTrace(Entity renderViewEntity, float partialTicks, double distance) {
		RayTraceResult blockRayTrace = renderViewEntity.pick(distance, partialTicks, false);
		Vector3d startVec = renderViewEntity.getEyePosition(partialTicks);
		Vector3d lookVec = renderViewEntity.getViewVector(1.0F);
		Vector3d directionVec = lookVec.scale(distance);
		Vector3d endVec = startVec.add(directionVec);

		double blockDistSq = blockRayTrace.getLocation().distanceToSqr(startVec);
		EntityRayTraceResult entityRayTrace = ProjectileHelper.getEntityHitResult(renderViewEntity, startVec, endVec, renderViewEntity.getBoundingBox().expandTowards(directionVec).inflate(1d), (target) -> !target.isSpectator() && target.isPickable() && target instanceof LivingEntity, blockDistSq);
		if (entityRayTrace != null) {
			Vector3d hitVec = entityRayTrace.getLocation();
			double entityDistSq = startVec.distanceToSqr(hitVec);
			if (entityDistSq > distance * distance) {
				return BlockRayTraceResult.miss(hitVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(hitVec));
			}
			else if (entityDistSq < blockDistSq) {
				return entityRayTrace;
			}
		}
		return blockRayTrace;
	}
}
