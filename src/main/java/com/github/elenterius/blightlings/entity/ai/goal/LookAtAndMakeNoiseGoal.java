package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.SwarmGroupMemberEntity;
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
		if (entity.livingSoundTime >= -1) {
			entity.livingSoundTime = -40; //reset sound timer
		}

		float multiplier = (MathHelper.sin(0.1501f * entity.ticksExisted) * 0.5f + 0.5f); // from 0 to 1
		float angle = multiplier * (3.5f + MathHelper.abs(entity.rotationPitch) * 0.8f);
		if (entity.livingSoundTime > -20 && angle > 10f) {
			entity.livingSoundTime = -40; //reset sound timer
			swarmEntity.playSound(soundEvent, multiplier);
		}
	}
}
