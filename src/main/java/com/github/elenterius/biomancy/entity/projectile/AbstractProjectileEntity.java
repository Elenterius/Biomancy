package com.github.elenterius.biomancy.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractProjectileEntity extends ProjectileEntity implements IEntityAdditionalSpawnData {

	public static final float DEFAULT_DRAG = 0.99f;
	public static final float DEFAULT_WATER_DRAG = 0.8f;

	private float damage = 2f;
	private byte knockback = 0;

//	private double accelerationX = 0;
//	private double accelerationY = 0;
//	private double accelerationZ = 0;

	protected AbstractProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected AbstractProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world, double x, double y, double z) {
		this(entityType, world);
		setPos(x, y, z);
	}

	protected AbstractProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world, LivingEntity shooter) {
		this(entityType, world, shooter.getX(), shooter.getEyeY() - 0.1f, shooter.getZ());
		setOwner(shooter);
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		Entity shooter = getOwner();
		buffer.writeVarInt(shooter == null ? 0 : shooter.getId());
//		buffer.writeDouble(accelerationX);
//		buffer.writeDouble(accelerationY);
//		buffer.writeDouble(accelerationZ);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		Entity shooter = level.getEntity(buffer.readVarInt());
		setOwner(shooter);
//		accelerationX = buffer.readDouble();
//		accelerationY = buffer.readDouble();
//		accelerationZ = buffer.readDouble();
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("damage", damage);
		compound.putByte("knockback", knockback);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		damage = compound.contains("damage") ? compound.getFloat("damage") : 5f;
		knockback = compound.getByte("knockback");
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

	public void tick() {
		Entity shooter = getOwner();
		if (level.isClientSide || ((shooter == null || !shooter.removed) && level.hasChunkAt(blockPosition()))) {
			super.tick();

			if (isInWaterOrRain()) clearFire();

			RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
			if (raytraceresult.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				onHit(raytraceresult);
			}
			checkInsideBlocks();

			Vector3d motion = getDeltaMovement();
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
			remove();
		}
	}

	@Override
	protected void onHit(RayTraceResult result) {
		super.onHit(result); //call onEntityHit and onBlockHit before removing the projectile
		if (!level.isClientSide) remove();
	}

	public void spawnParticle(double x, double y, double z) {
		level.addParticle(getParticle(), x, y + 0.5d, z, 0, 0, 0);
	}

	protected IParticleData getParticle() {
		return ParticleTypes.SMOKE;
	}

	@Override
	protected boolean canHitEntity(Entity entityIn) {
		return super.canHitEntity(entityIn) && !entityIn.noPhysics;
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
	public float getBrightness() {
		return 1f;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double dist = getBoundingBox().getSize() * 10d;
		if (Double.isNaN(dist)) dist = 1d;
		dist = dist * 64d * getViewScale();
		return distance < dist * dist;
	}
}
