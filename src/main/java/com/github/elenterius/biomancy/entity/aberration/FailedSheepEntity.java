package com.github.elenterius.biomancy.entity.aberration;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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

	@Override
	public SheepEntity createChild(ServerWorld world, AgeableEntity mate) {
		if (mate.getClass() != getClass()) {
			float p = rand.nextFloat();
			if (p < 0.05f) return ModEntityTypes.SILKY_WOOL_SHEEP.get().create(world);
			if (p < 0.3f) return (SheepEntity) mate.createChild(world, this);
		}
		return ModEntityTypes.FAILED_SHEEP.get().create(world);
	}

	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		if (otherAnimal == this) return false;
		return otherAnimal instanceof SheepEntity && isInLove() && otherAnimal.isInLove();
	}

}
