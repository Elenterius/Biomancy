package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class BreedingSerum extends Serum {

	public BreedingSerum(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Animal animal && !animal.isBaby();
	}

	@Override
	public void affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Animal animal && !animal.isBaby()) {
			if (!animal.level.isClientSide) {
				animal.addEffect(new MobEffectInstance(ModMobEffects.LIBIDO.get(), 12 * 20, 1, false, true));
			}
		}
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, Player targetSelf) {}

}
