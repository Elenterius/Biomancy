package com.github.elenterius.blightlings.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class RayTraceUtil
{
    public static Optional<RayTraceResult> rayTrace(Entity entity, Predicate<Entity> predicate, double distance) {
        Vector3d startVec = entity.getEyePosition(1f);
        Vector3d directionVec = entity.getLookVec().scale(distance);
        Vector3d endVec = startVec.add(directionVec);

        RayTraceResult rayTraceResult = entity.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
            endVec = rayTraceResult.getHitVec();
        }

        RayTraceResult entityRayTrace = ProjectileHelper.rayTraceEntities(entity.world, entity, startVec, endVec, entity.getBoundingBox().expand(directionVec).grow(1d), predicate);
        if (entityRayTrace != null) {
            rayTraceResult = entityRayTrace;
        }

        return startVec.squareDistanceTo(rayTraceResult.getHitVec()) > distance * distance ? Optional.empty() : Optional.of(rayTraceResult);
    }
}
