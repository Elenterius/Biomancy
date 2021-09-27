package com.github.elenterius.biomancy.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class BlockPlacementTarget {
	public final BlockRayTraceResult rayTraceResult;
	public final BlockPos targetPos;
	public final Direction horizontalFacing;

	public BlockPlacementTarget(BlockRayTraceResult rayTraceResult, Direction horizontalFacing) {
		this.rayTraceResult = rayTraceResult;
		this.targetPos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
		this.horizontalFacing = horizontalFacing;
	}

	public BlockPlacementTarget copy() {
		Vector3d hitVec = new Vector3d(rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z);
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS)
			return new BlockPlacementTarget(BlockRayTraceResult.miss(hitVec, rayTraceResult.getDirection(), new BlockPos(rayTraceResult.getBlockPos())), horizontalFacing);
		else
			return new BlockPlacementTarget(new BlockRayTraceResult(hitVec, rayTraceResult.getDirection(), new BlockPos(rayTraceResult.getBlockPos()), rayTraceResult.isInside()), horizontalFacing);
	}
}
