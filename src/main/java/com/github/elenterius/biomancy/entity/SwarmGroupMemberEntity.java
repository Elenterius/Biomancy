package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.entity.ai.goal.FollowSwarmLeaderGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SwarmGroupMemberEntity extends SpiderEntity implements ISwarmGroupMember<SwarmGroupMemberEntity> {
	private SwarmGroupMemberEntity leader;
	private int groupSize = 1;

	public SwarmGroupMemberEntity(EntityType<? extends SpiderEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		goalSelector.addGoal(4, new AttackGoal(this));
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));

		goalSelector.addGoal(6, new LookRandomlyGoal(this));
		goalSelector.addGoal(6, new FollowSwarmLeaderGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
	}

	@Nullable
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ModifiableAttributeInstance attribute = getAttribute(Attributes.FOLLOW_RANGE);
		if (attribute != null) {
			attribute.addPermanentModifier(new AttributeModifier("Random spawn bonus", random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
		}

		if (spawnDataIn == null) {
			spawnDataIn = new SwarmGroupData(this);
		}
		else {
			joinGroup((SwarmGroupMemberEntity) ((SwarmGroupData) spawnDataIn).leader);
		}

		return spawnDataIn;
	}

	@Override
	public SwarmGroupMemberEntity asMobEntity() {
		return this;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return getMaxGroupSize();
	}

	@Override
	public int getGroupSize() {
		return groupSize;
	}

	@Override
	public int getMaxGroupSize() {
		return 8;
	}

	@Override
	public ISwarmGroupMember<SwarmGroupMemberEntity> getLeader() {
		return leader;
	}

	@Override
	public void setLeader(@Nullable SwarmGroupMemberEntity groupLeader) {
		leader = groupLeader;
	}

	@Override
	public void increaseGroupSize() {
		++groupSize;
	}

	@Override
	public void decreaseGroupSize() {
		--groupSize;
	}

	@Override
	public void setTarget(@Nullable LivingEntity entityIn) {
		super.setTarget(entityIn);
		//noinspection ConstantConditions
		if (entityIn != null && entityIn.isAlive() && hasLeader() && getLeader().asMobEntity().getTarget() == null) {
			getLeader().asMobEntity().setTarget(entityIn);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (isLeader() && level.random.nextInt(200) == 1) {
			List<SwarmGroupMemberEntity> list = level.getEntitiesOfClass(getClass(), getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
			if (list.size() <= 1) groupSize = 1;
		}
	}

	public void playSound(@Nullable SoundEvent soundevent, float volumeMultiplier) {
		if (soundevent != null && volumeMultiplier > 0f) {
			playSound(soundevent, getSoundVolume() * volumeMultiplier, getVoicePitch());
		}
	}

	static class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(SwarmGroupMemberEntity entity) {
			super(entity, 1.0D, true);
		}

		public boolean canUse() {
			return super.canUse() && !mob.isVehicle();
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 4f + attackTarget.getBbWidth();
		}
	}
}