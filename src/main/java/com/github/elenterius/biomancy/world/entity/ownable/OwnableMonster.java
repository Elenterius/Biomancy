package com.github.elenterius.biomancy.world.entity.ownable;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableMonster extends Monster implements IOwnableMob {

	protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(OwnableMonster.class, EntityDataSerializers.OPTIONAL_UUID);

	protected OwnableMonster(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER_UNIQUE_ID, Optional.empty());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		getOwnerUUID().ifPresent(value -> compound.putUUID("OwnerUUID", value));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.hasUUID("OwnerUUID")) setOwnerUUID(compound.getUUID("OwnerUUID"));
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
		return 0f;
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
	public void setOwner(Player player) {
		setOwnerUUID(player.getUUID());
	}

	@Override
	public Optional<Player> getOwner() {
		return getOwnerUUID().map(value -> level.getPlayerByUUID(value));
	}

	@Override
	public Team getTeam() {
		Optional<Player> optional = getOwner();
		if (optional.isPresent()) {
			return optional.get().getTeam();
		}
		return super.getTeam();
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		Optional<Player> optional = getOwner();
		if (optional.isPresent()) {
			if (optional.get() == entity) return true;
			return optional.get().isAlliedTo(entity);
		}
		return super.isAlliedTo(entity);
	}

	@Override
	public void die(DamageSource cause) {
		if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
			Optional<Player> optional = getOwner();
			if (optional.isPresent() && optional.get() instanceof ServerPlayer) {
				optional.get().sendMessage(getCombatTracker().getDeathMessage(), Util.NIL_UUID);
			}
		}
		super.die(cause);
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return !isLeashed() && player == getOwner().orElse(null);
	}

}
