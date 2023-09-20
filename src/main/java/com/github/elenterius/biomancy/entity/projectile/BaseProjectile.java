package com.github.elenterius.biomancy.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

public abstract class BaseProjectile extends Projectile implements IEntityAdditionalSpawnData {

	public static final float DEFAULT_DRAG = 0.99f;
	public static final float DEFAULT_WATER_DRAG = 0.8f;

	private float damage = 2f;
	private byte knockback = 0;

	//	private double accelerationX = 0;
	//	private double accelerationY = 0;
	//	private double accelerationZ = 0;

	protected BaseProjectile(EntityType<? extends BaseProjectile> entityType, Level level) {
		super(entityType, level);
	}

	protected BaseProjectile(EntityType<? extends BaseProjectile> entityType, Level level, double x, double y, double z) {
		this(entityType, level);
		setPos(x, y, z);
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		Entity shooter = getOwner();
		buffer.writeVarInt(shooter == null ? 0 : shooter.getId());
		//		buffer.writeDouble(accelerationX);
		//		buffer.writeDouble(accelerationY);
		//		buffer.writeDouble(accelerationZ);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		Entity shooter = level.getEntity(buffer.readVarInt());
		setOwner(shooter);
		//		accelerationX = buffer.readDouble();
		//		accelerationY = buffer.readDouble();
		//		accelerationZ = buffer.readDouble();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("damage", damage);
		tag.putByte("knockback", knockback);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		damage = tag.contains("damage") ? tag.getFloat("damage") : 5f;
		knockback = tag.getByte("knockback");
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		super.shoot(x, y, z, velocity, inaccuracy);
		//		double magnitude = getMotion().length();
		//		if (magnitude != 0.0D) {
		//			accelerationX = getMotion().x / magnitude * 0.1d;
		//			accelerationY = getMotion().y / magnitude * 0.1d;
		//			accelerationZ = getMotion().z / magnitude * 0.1d;
		//		}
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damageIn) {
		damage = damageIn;
	}

	public byte getKnockback() {
		return knockback;
	}

	public void setKnockback(byte knockbackIn) {
		knockback = knockbackIn;
	}

	public float getDrag() {
		return DEFAULT_DRAG;
	}

	public float getWaterDrag() {
		return DEFAULT_WATER_DRAG;
	}

	public float getGravity() {
		return (float) (ForgeMod.ENTITY_GRAVITY.get().getDefaultValue() / 4d);
	}

	@Override
	public void tick() {
		Entity shooter = getOwner();
		if (level.isClientSide || ((shooter == null || !shooter.isRemoved()) && level.isAreaLoaded(blockPosition(), 1))) {
			super.tick();

			if (isInWaterOrRain()) clearFire();
			HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
			if (hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
				onHit(hitResult);
			}
			checkInsideBlocks();

			Vec3 motion = getDeltaMovement();
			double posX = getX() + motion.x;
			double posY = getY() + motion.y;
			double posZ = getZ() + motion.z;
			updateRotation();

			float drag = getDrag();
			if (isInWater()) {
				for (int i = 0; i < 4; ++i) {
					level.addParticle(ParticleTypes.BUBBLE, posX - motion.x * 0.25f, posY - motion.y * 0.25f, posZ - motion.z * 0.25f, motion.x, motion.y, motion.z);
				}
				drag = getWaterDrag();
			}

			setDeltaMovement(motion.scale(drag));
			if (!isNoGravity()) {
				setDeltaMovement(getDeltaMovement().add(0, -getGravity(), 0));
			}
			spawnParticle(posX, posY, posZ);
			setPos(posX, posY, posZ);
		}
		else {
			discard();
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result); //call onEntityHit and onBlockHit before removing the projectile
		if (!level.isClientSide) discard();
	}

	public void spawnParticle(double x, double y, double z) {
		level.addParticle(getParticle(), x, y + 0.5d, z, 0, 0, 0);
	}

	protected ParticleOptions getParticle() {
		return ParticleTypes.SMOKE;
	}

	@Override
	protected boolean canHitEntity(Entity entity) {
		return super.canHitEntity(entity) && !entity.noPhysics;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public float getPickRadius() {
		return 1f;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double dist = getBoundingBox().getSize() * 10d;
		if (Double.isNaN(dist)) dist = 1d;
		dist = dist * 64d * getViewScale();
		return distance < dist * dist;
	}

}
