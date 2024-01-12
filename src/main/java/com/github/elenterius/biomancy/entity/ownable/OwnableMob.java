package com.github.elenterius.biomancy.entity.ownable;

import com.github.elenterius.biomancy.ownable.IOwnableMob;
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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableMob extends PathfinderMob implements IOwnableMob {

	protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(OwnableMob.class, EntityDataSerializers.OPTIONAL_UUID);
	private BlockPos targetBlockPos;

	protected OwnableMob(EntityType<? extends PathfinderMob> type, Level level) {
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
		getOptionalOwnerUUID().ifPresent(value -> compound.putUUID("OwnerUUID", value));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.hasUUID("OwnerUUID")) {
			setOwner(compound.getUUID("OwnerUUID"));
		}
		else removeOwner();
	}

	@Override
	public Optional<UUID> getOptionalOwnerUUID() {
		return entityData.get(OWNER_UNIQUE_ID);
	}

	@Override
	public void setOwner(UUID uuid) {
		entityData.set(OWNER_UNIQUE_ID, Optional.of(uuid));
	}

	@Override
	public void removeOwner() {
		entityData.set(OWNER_UNIQUE_ID, Optional.empty());
	}

	@Override
	public Optional<Player> getOwnerAsPlayer() {
		return getOptionalOwnerUUID().map(value -> level.getPlayerByUUID(value));
	}

	@Override
	public Optional<Entity> getOwnerAsEntity() {
		return getOptionalOwnerUUID().map(value -> level.getPlayerByUUID(value));
	}

	@Override
	public Team getTeam() {
		return getOwnerAsPlayer().map(Entity::getTeam).orElseGet(super::getTeam);
	}

	@Override
	public boolean isAlliedTo(Entity entityIn) {
		Optional<Player> optional = getOwnerAsPlayer();
		if (optional.isPresent()) {
			if (optional.get() == entityIn) return true;
			return optional.get().isAlliedTo(entityIn);
		}
		return super.isAlliedTo(entityIn);
	}

	@Override
	public void die(DamageSource cause) {
		if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
			Optional<Player> optional = getOwnerAsPlayer();
			if (optional.isPresent() && optional.get() instanceof ServerPlayer) {
				((ServerPlayer) optional.get()).sendMessage(getCombatTracker().getDeathMessage(), Util.NIL_UUID);
			}
		}
		super.die(cause);
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return !isLeashed() && player == getOwnerAsPlayer().orElse(null);
	}

	@Nullable
	public BlockPos getTargetBlockPos() {
		return targetBlockPos;
	}

	public void setTargetBlockPos(@Nullable BlockPos targetPos) {
		this.targetBlockPos = targetPos;
	}

}
