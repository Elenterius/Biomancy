package com.github.elenterius.biomancy.world.section;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public abstract class Region {

	protected final BlockPos origin;
	protected boolean isValid = false;

	protected Region(BlockPos origin) {
		this.origin = origin;
	}

	public abstract SABB getSABB();

	public BlockPos getRegionOrigin() {
		return origin;
	}

	public final long getId() {
		return origin.asLong();
	}

	abstract boolean contains(double x, double y, double z);

	abstract boolean contains(int x, int y, int z);

	boolean contains(BlockPos pos) {
		return contains(pos.getX(), pos.getY(), pos.getZ());
	}

	abstract double distanceToSqr(double x, double y, double z);

	public boolean contains(Vec3 pos) {
		return contains(pos.x, pos.y, pos.z);
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean flag) {
		isValid = flag;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Region otherRegion)) return false;
		return getId() == otherRegion.getId();
	}

	@Override
	public int hashCode() {
		return Long.hashCode(getId());
	}

}
