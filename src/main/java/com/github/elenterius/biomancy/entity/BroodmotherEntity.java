package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.entity.ai.goal.LookAtAndMakeNoiseGoal;
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

public class BroodmotherEntity extends SwarmGroupMemberEntity {
	private byte timer;

	public BroodmotherEntity(EntityType<? extends BroodmotherEntity> entityType, World world) {
		super(entityType, world);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
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
	public void livingTick() {
		if (!world.isRemote() && ticksExisted % 20 == 0) {
			if (canGroupGrow() && getHealth() < getMaxHealth() * 0.8f) {
				if (++timer > 6 && getAttackTarget() != null) {
					timer = 0;
					spawnBlobling();
				}
			}
		}
		super.livingTick();
	}

	private void spawnBlobling() {
		BloblingEntity entity = ModEntityTypes.BLOBLING.get().create(world);
		if (entity != null) {
			Vector3d pos = getPositionVec().add(4D * rand.nextDouble() - 2D, 0.5D, 4D * rand.nextDouble() - 2D);
			entity.setLocationAndAngles(pos.x, pos.y, pos.z, rand.nextFloat() * 360.0F, 0.0F);
			entity.onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(new BlockPos(pos)), SpawnReason.REINFORCEMENT, new SwarmGroupData(this), null);

			if (isNoDespawnRequired()) {
				entity.enablePersistence();
			}
			entity.setNoAI(isAIDisabled());
			entity.setInvulnerable(isInvulnerable());
			if (hasCustomName()) {
				//noinspection ConstantConditions
				entity.setCustomName(new StringTextComponent("Little ").append(getCustomName()));
				entity.setCustomNameVisible(isCustomNameVisible());
			}

			world.addEntity(entity);
			entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH_SMALL, entity.getSoundVolume(), 1f);
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
			playSound(soundevent, getSoundVolume(), getSoundPitch() * 0.5f);
		}
	}
}
