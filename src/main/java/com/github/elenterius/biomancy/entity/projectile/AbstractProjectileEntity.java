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
		setPosition(x, y, z);
	}

	protected AbstractProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world, LivingEntity shooter) {
		this(entityType, world, shooter.getPosX(), shooter.getPosYEye() - 0.1f, shooter.getPosZ());
		setShooter(shooter);
	}

	@Override
	protected void registerData() {}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		Entity shooter = getShooter();
		buffer.writeVarInt(shooter == null ? 0 : shooter.getEntityId());
//		buffer.writeDouble(accelerationX);
//		buffer.writeDouble(accelerationY);
//		buffer.writeDouble(accelerationZ);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		Entity shooter = world.getEntityByID(buffer.readVarInt());
		setShooter(shooter);
//		accelerationX = buffer.readDouble();
//		accelerationY = buffer.readDouble();
//		accelerationZ = buffer.readDouble();
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putFloat("damage", damage);
		compound.putByte("knockback", knockback);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
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
		Entity shooter = getShooter();
		if (world.isRemote || ((shooter == null || !shooter.removed) && world.isBlockLoaded(getPosition()))) {
			super.tick();

			if (isWet()) extinguish();

			RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
			if (raytraceresult.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				onImpact(raytraceresult);
			}
			doBlockCollisions();

			Vector3d motion = getMotion();
			double posX = getPosX() + motion.x;
			double posY = getPosY() + motion.y;
			double posZ = getPosZ() + motion.z;
			updatePitchAndYaw();

			float drag = getDrag();
			if (isInWater()) {
				for (int i = 0; i < 4; ++i) {
					world.addParticle(ParticleTypes.BUBBLE, posX - motion.x * 0.25f, posY - motion.y * 0.25f, posZ - motion.z * 0.25f, motion.x, motion.y, motion.z);
				}
				drag = getWaterDrag();
			}

			setMotion(motion.scale(drag));
			if (!hasNoGravity()) {
				setMotion(getMotion().add(0, -getGravity(), 0));
			}
			spawnParticle(posX, posY, posZ);
			setPosition(posX, posY, posZ);
		}
		else {
			remove();
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!world.isRemote) remove();
	}

	public void spawnParticle(double x, double y, double z) {
		world.addParticle(getParticle(), x, y + 0.5d, z, 0, 0, 0);
	}

	protected IParticleData getParticle() {
		return ParticleTypes.SMOKE;
	}

	@Override
	protected boolean func_230298_a_(Entity entityIn) {
		return super.func_230298_a_(entityIn) && !entityIn.noClip;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1f;
	}

	@Override
	public float getBrightness() {
		return 1f;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		double dist = getBoundingBox().getAverageEdgeLength() * 10d;
		if (Double.isNaN(dist)) dist = 1d;
		dist = dist * 64d * getRenderDistanceWeight();
		return distance < dist * dist;
	}
}
