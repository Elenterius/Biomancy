package com.github.elenterius.blightlings.util;

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
		this.targetPos = rayTraceResult.getPos().offset(rayTraceResult.getFace());
		this.horizontalFacing = horizontalFacing;
	}

	public BlockPlacementTarget copy() {
		Vector3d hitVec = new Vector3d(rayTraceResult.getHitVec().x, rayTraceResult.getHitVec().y, rayTraceResult.getHitVec().z);
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS)
			return new BlockPlacementTarget(BlockRayTraceResult.createMiss(hitVec, rayTraceResult.getFace(), new BlockPos(rayTraceResult.getPos())), horizontalFacing);
		else
			return new BlockPlacementTarget(new BlockRayTraceResult(hitVec, rayTraceResult.getFace(), new BlockPos(rayTraceResult.getPos()), rayTraceResult.isInside()), horizontalFacing);
	}
}
