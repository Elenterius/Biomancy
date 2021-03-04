package com.github.elenterius.biomancy.entity.gmo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;

public class FailedSheepEntity extends SheepEntity {

	public FailedSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public void setSheared(boolean sheared) {
		if (sheared) super.setSheared(true);
	}

	@Override
	public boolean getSheared() {
		return true; // the failed sheep is hairless
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() - 1f;
	}
}
