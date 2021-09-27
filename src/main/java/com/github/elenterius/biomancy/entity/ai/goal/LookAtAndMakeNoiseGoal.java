package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.entity.SwarmGroupMemberEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class LookAtAndMakeNoiseGoal extends LookAtGoal {
	private final SwarmGroupMemberEntity swarmEntity;
	private final SoundEvent soundEvent;

	public LookAtAndMakeNoiseGoal(SwarmGroupMemberEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, SoundEvent soundEvent) {
		this(entityIn, watchTargetClass, maxDistance, 0.02F, soundEvent);
	}

	public LookAtAndMakeNoiseGoal(SwarmGroupMemberEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn, SoundEvent soundEvent) {
		super(entityIn, watchTargetClass, maxDistance, chanceIn);
		swarmEntity = entityIn;
		this.soundEvent = soundEvent;
	}

	@Override
	public void tick() {
		super.tick();
		if (mob.ambientSoundTime >= -1) {
			mob.ambientSoundTime = -40; //reset sound timer
		}

		float multiplier = (MathHelper.sin(0.1501f * mob.tickCount) * 0.5f + 0.5f); // from 0 to 1
		float angle = multiplier * (3.5f + MathHelper.abs(mob.xRot) * 0.8f);
		if (mob.ambientSoundTime > -20 && angle > 10f) {
			mob.ambientSoundTime = -40; //reset sound timer
			swarmEntity.playSound(soundEvent, multiplier);
		}
	}
}
