package com.github.elenterius.biomancy.world.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class AbsorptionSerum extends Serum {

	public AbsorptionSerum(int color) {
		super(color);
	}

	@Override
	public void affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		addAbsorption(target);
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, Player targetSelf) {
		addAbsorption(targetSelf);
	}

	private void addAbsorption(LivingEntity target) {
		if (!target.level.isClientSide) {
			target.setAbsorptionAmount(target.getAbsorptionAmount() + 8);
		}
	}

}
