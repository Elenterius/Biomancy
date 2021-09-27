package com.github.elenterius.biomancy.entity.golem;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableCreatureEntity extends CreatureEntity implements IOwnableCreature {
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.defineId(OwnableCreatureEntity.class, DataSerializers.OPTIONAL_UUID);
	private BlockPos targetBlockPos;

	protected OwnableCreatureEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER_UNIQUE_ID, Optional.empty());
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		if (getOwnerUUID().isPresent()) compound.putUUID("OwnerUUID", getOwnerUUID().get());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		if (compound.hasUUID("OwnerUUID")) setOwnerUUID(compound.getUUID("OwnerUUID"));
	}

	@Override
	public Optional<UUID> getOwnerUUID() {
		return entityData.get(OWNER_UNIQUE_ID);
	}

	@Override
	public void setOwnerUUID(@Nullable UUID uuid) {
		entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
	}

	@Override
	public Optional<PlayerEntity> getOwner() {
		return getOwnerUUID().map(value -> level.getPlayerByUUID(value));
	}

	@Override
	public void setOwner(PlayerEntity entity) {
		setOwnerUUID(entity.getUUID());
	}

	@Override
	public Team getTeam() {
		Optional<PlayerEntity> optional = getOwner();
		if (optional.isPresent()) {
			return optional.get().getTeam();
		}
		return super.getTeam();
	}

	@Override
	public boolean isAlliedTo(Entity entityIn) {
		Optional<PlayerEntity> optional = getOwner();
		if (optional.isPresent()) {
			if (optional.get() == entityIn) return true;
			return optional.get().isAlliedTo(entityIn);
		}
		return super.isAlliedTo(entityIn);
	}

	@Override
	public void die(DamageSource cause) {
		if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
			Optional<PlayerEntity> optional = getOwner();
			if (optional.isPresent() && optional.get() instanceof ServerPlayerEntity) {
				optional.get().sendMessage(getCombatTracker().getDeathMessage(), Util.NIL_UUID);
			}
		}
		super.die(cause);
	}

	@Override
	public boolean canBeLeashed(PlayerEntity player) {
		return !isLeashed() && player == getOwner().orElse(null);
	}

	@Nullable
	public BlockPos getTargetBlockPos() {
		return targetBlockPos;
	}

	public void setTargetBlockPos(@Nullable BlockPos targetPos) {
		this.targetBlockPos = targetPos;
	}

}
