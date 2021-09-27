package com.github.elenterius.biomancy.entity.hybrid;

import com.github.elenterius.biomancy.entity.SwarmGroupData;
import com.github.elenterius.biomancy.entity.SwarmGroupMemberEntity;
import com.github.elenterius.biomancy.entity.ai.goal.LookAtAndMakeNoiseGoal;
import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CrocospiderEntity extends SwarmGroupMemberEntity {
	private byte timer;

	public CrocospiderEntity(EntityType<? extends CrocospiderEntity> entityType, World world) {
		super(entityType, world);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(6, new LookAtAndMakeNoiseGoal(this, PlayerEntity.class, 8.0F, ModSoundEvents.WAH_WAH.get()));
	}

	@Override
	public boolean isLeader() {
		return true;
	}

	@Override
	public void aiStep() {
		if (!level.isClientSide() && tickCount % 20 == 0) {
			if (canGroupGrow() && getHealth() < getMaxHealth() * 0.8f) {
				if (++timer > 6 && getTarget() != null) {
					timer = 0;
					spawnBlobling();
				}
			}
		}
		super.aiStep();
	}

	private void spawnBlobling() {
		BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(level);
		if (entity != null) {
			Vector3d pos = position().add(4D * random.nextDouble() - 2D, 0.5D, 4D * random.nextDouble() - 2D);
			entity.moveTo(pos.x, pos.y, pos.z, random.nextFloat() * 360.0F, 0.0F);
			entity.finalizeSpawn((IServerWorld) level, level.getCurrentDifficultyAt(new BlockPos(pos)), SpawnReason.REINFORCEMENT, new SwarmGroupData(this), null);

			if (isPersistenceRequired()) {
				entity.setPersistenceRequired();
			}
			entity.setNoAi(isNoAi());
			entity.setInvulnerable(isInvulnerable());
			if (hasCustomName()) {
				//noinspection ConstantConditions
				entity.setCustomName(new StringTextComponent("Little ").append(getCustomName()));
				entity.setCustomNameVisible(isCustomNameVisible());
			}

			level.addFreshEntity(entity);
			entity.playSound(SoundEvents.SLIME_SQUISH_SMALL, 0.4f, 1f);
		}
	}

	@Override
	protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
		return 0.28F;
	}

	@Override
	public void playAmbientSound() {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getVoicePitch() * 0.5f);
		}
	}
}
