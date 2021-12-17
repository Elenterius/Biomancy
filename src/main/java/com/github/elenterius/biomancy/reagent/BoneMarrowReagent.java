package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;

public class BoneMarrowReagent extends DNASampleReagent {

	public BoneMarrowReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean isValidSamplingTarget(LivingEntity entity) {
		return entity.getMobType() == CreatureAttribute.UNDEAD && MobUtil.isSkeleton(entity);
	}

}
