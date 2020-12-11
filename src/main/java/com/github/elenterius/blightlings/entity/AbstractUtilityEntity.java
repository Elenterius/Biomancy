package com.github.elenterius.blightlings.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractUtilityEntity extends CreatureEntity {
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(AbstractUtilityEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private BlockPos targetBlockPos;

    protected AbstractUtilityEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
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

    public Optional<UUID> getOwnerUUID() {
        return dataManager.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity entity) {
        setOwnerUUID(entity.getUniqueID());
    }

    public Optional<PlayerEntity> getOwner() {
        return getOwnerUUID().map(value -> world.getPlayerByUuid(value));
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
