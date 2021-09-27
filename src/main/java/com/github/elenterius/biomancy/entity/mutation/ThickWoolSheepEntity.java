package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class ThickWoolSheepEntity extends SheepEntity {

	private static final DataParameter<Byte> WOOL_SIZE = EntityDataManager.defineId(ThickWoolSheepEntity.class, DataSerializers.BYTE);
	public static final byte MAX_WOOL_SIZE = 10;

	public ThickWoolSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 8d)
				.add(Attributes.MOVEMENT_SPEED, 0.23d);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(WOOL_SIZE, (byte) 1);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("WoolSize", (byte) getWoolSize());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		setWoolSize(compound.getByte("WoolSize"));
	}

	@Override
	public EntitySize getDimensions(Pose poseIn) {
		return super.getDimensions(poseIn).scale(1f + ((float) getWoolSize() / MAX_WOOL_SIZE) * 0.5f, 1f);
	}

	@Nullable
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData entityData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		setWoolSize(!isSheared() ? (byte) 1 : (byte) 0);
		return entityData;
	}

	public int getWoolSize() {
		return entityData.get(WOOL_SIZE);
	}

	protected void setWoolSize(byte size) {
		size = (byte) MathHelper.clamp(size, 0, Byte.MAX_VALUE);
		entityData.set(WOOL_SIZE, size);
		reapplyPosition();
		refreshDimensions();
		double pct = (double) size / MAX_WOOL_SIZE;
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23d - 0.16d * pct);
		getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08d + 0.16d * pct);
		getAttribute(Attributes.ARMOR).setBaseValue(MathHelper.clamp(16d * pct - 1.6d, 0d, 16d));
		getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(MathHelper.clamp(3d * pct - 1d, 0d, 16d));
	}

	@Override
	public void refreshDimensions() {
		double d0 = getX();
		double d1 = getY();
		double d2 = getZ();
		super.refreshDimensions();
		setPos(d0, d1, d2);
	}

	@Override
	public void onSyncedDataUpdated(DataParameter<?> key) {
		if (WOOL_SIZE.equals(key)) {
			refreshDimensions();
			yRot = yHeadRot;
			yBodyRot = yHeadRot;
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	public void ate() {
		if (isBaby()) {
			if (isSheared()) setSheared(false);
			else ageUp(60);
		}
		else {
			setSheared(false);
		}
	}

	@Override
	public void setSheared(boolean removeWool) {
		if (removeWool) {
			int woolSize = getWoolSize() - 1;
			setWoolSize((byte) woolSize);
			if (woolSize <= 0) {
				super.setSheared(true);
			}
		}
		else {
			int woolSize = getWoolSize();
			if (woolSize < MAX_WOOL_SIZE) setWoolSize((byte) (woolSize + 1));
			super.setSheared(false);
		}
	}

	@Override
	public ThickWoolSheepEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
		return ModEntityTypes.THICK_WOOL_SHEEP.get().create(world);
	}

}
