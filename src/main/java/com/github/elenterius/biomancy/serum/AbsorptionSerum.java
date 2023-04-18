package com.github.elenterius.biomancy.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class AbsorptionSerum extends Serum {

	public AbsorptionSerum(int color) {
		super(color);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		addAbsorption(target);
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		addAbsorption(targetSelf);
	}

	private void addAbsorption(LivingEntity target) {
		target.setAbsorptionAmount(target.getAbsorptionAmount() + 8);
	}

}
