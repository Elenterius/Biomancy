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
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(OwnableCreatureEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private BlockPos targetBlockPos;

	protected OwnableCreatureEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (getOwnerUUID().isPresent()) compound.putUniqueId("OwnerUUID", getOwnerUUID().get());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.hasUniqueId("OwnerUUID")) setOwnerUUID(compound.getUniqueId("OwnerUUID"));
	}

	@Override
	public Optional<UUID> getOwnerUUID() {
		return dataManager.get(OWNER_UNIQUE_ID);
	}

	@Override
	public void setOwnerUUID(@Nullable UUID uuid) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
	}

	@Override
	public Optional<PlayerEntity> getOwner() {
		return getOwnerUUID().map(value -> world.getPlayerByUuid(value));
	}

	@Override
	public void setOwner(PlayerEntity entity) {
		setOwnerUUID(entity.getUniqueID());
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
	public boolean isOnSameTeam(Entity entityIn) {
		Optional<PlayerEntity> optional = getOwner();
		if (optional.isPresent()) {
			if (optional.get() == entityIn) return true;
			return optional.get().isOnSameTeam(entityIn);
		}
		return super.isOnSameTeam(entityIn);
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (!world.isRemote && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
			Optional<PlayerEntity> optional = getOwner();
			if (optional.isPresent() && optional.get() instanceof ServerPlayerEntity) {
				optional.get().sendMessage(getCombatTracker().getDeathMessage(), Util.DUMMY_UUID);
			}
		}
		super.onDeath(cause);
	}

	@Override
	public boolean canBeLeashedTo(PlayerEntity player) {
		return !getLeashed() && player == getOwner().orElse(null);
	}

	@Nullable
	public BlockPos getTargetBlockPos() {
		return targetBlockPos;
	}

	public void setTargetBlockPos(@Nullable BlockPos targetPos) {
		this.targetBlockPos = targetPos;
	}

}
