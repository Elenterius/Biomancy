package com.github.elenterius.biomancy.entity.mutation;

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
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class ThickWoolSheepEntity extends SheepEntity {

	private static final DataParameter<Byte> WOOL_SIZE = EntityDataManager.createKey(ThickWoolSheepEntity.class, DataSerializers.BYTE);
	public static final byte MAX_WOOL_SIZE = 10;

	public ThickWoolSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 8d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23d);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(WOOL_SIZE, (byte) 1);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("WoolSize", (byte) getWoolSize());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		setWoolSize(compound.getByte("WoolSize"));
	}

	@Override
	public EntitySize getSize(Pose poseIn) {
		return super.getSize(poseIn).scale(1f + ((float) getWoolSize() / MAX_WOOL_SIZE) * 0.5f, 1f);
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData entityData = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		setWoolSize(!getSheared() ? (byte) 1 : (byte) 0);
		return entityData;
	}

	public int getWoolSize() {
		return dataManager.get(WOOL_SIZE);
	}

	protected void setWoolSize(byte size) {
		size = (byte) MathHelper.clamp(size, 0, Byte.MAX_VALUE);
		dataManager.set(WOOL_SIZE, size);
		recenterBoundingBox();
		recalculateSize();
		double pct = (double) size / MAX_WOOL_SIZE;
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23d - 0.16d * pct);
		getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08d + 0.16d * pct);
		getAttribute(Attributes.ARMOR).setBaseValue(MathHelper.clamp(16d * pct - 1.6d, 0d, 16d));
		getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(MathHelper.clamp(3d * pct - 1d, 0d, 16d));
	}

	@Override
	public void recalculateSize() {
		double d0 = getPosX();
		double d1 = getPosY();
		double d2 = getPosZ();
		super.recalculateSize();
		setPosition(d0, d1, d2);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (WOOL_SIZE.equals(key)) {
			recalculateSize();
			rotationYaw = rotationYawHead;
			renderYawOffset = rotationYawHead;
		}
		super.notifyDataManagerChange(key);
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

}
