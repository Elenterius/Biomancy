package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nullable;

public class RottenBloodReagent extends DNASampleReagent {

	public RottenBloodReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean isValidSamplingTarget(LivingEntity entity) {
		return entity.getMobType() == CreatureAttribute.UNDEAD && !MobUtil.isSkeleton(entity);
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (!(target instanceof FleshBlobEntity)) {
			target.addEffect(new EffectInstance(Effects.HUNGER, 600));
			return true;
		}

		return super.affectEntity(nbt, source, target);
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		targetSelf.addEffect(new EffectInstance(Effects.HUNGER, 600));
		return true;
	}

}
