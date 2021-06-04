package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ToothProjectileEntity extends DamagingProjectileEntity implements IRendersAsItem, IEntityAdditionalSpawnData {

	private float damage = 5f;
	private byte knockback = 0;

	public ToothProjectileEntity(EntityType<? extends DamagingProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public ToothProjectileEntity(World world, double x, double y, double z) {
		this(ModEntityTypes.TOOTH_PROJECTILE.get(), world);
		setPosition(x, y, z);
	}

	public ToothProjectileEntity(World world, LivingEntity shooter) {
		this(world, shooter.getPosX(), shooter.getPosYEye() - 0.1f, shooter.getPosZ());
		setShooter(shooter);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		Entity shooter = getShooter();
		buffer.writeVarInt(shooter == null ? 0 : shooter.getEntityId());
		buffer.writeDouble(accelerationX);
		buffer.writeDouble(accelerationY);
		buffer.writeDouble(accelerationZ);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		Entity shooter = world.getEntityByID(buffer.readVarInt());
		setShooter(shooter);
		accelerationX = buffer.readDouble();
		accelerationY = buffer.readDouble();
		accelerationZ = buffer.readDouble();
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

	public void setDamage(float damageIn) {
		damage = damageIn;
	}

	public float getDamage() {
		return damage;
	}

	public void setKnockback(byte knockbackIn) {
		knockback = knockbackIn;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		super.shoot(x, y, z, velocity, inaccuracy);
		double magnitude = getMotion().length();
		if (magnitude != 0.0D) {
			accelerationX = getMotion().x / magnitude * 0.1d;
			accelerationY = getMotion().y / magnitude * 0.1d;
			accelerationZ = getMotion().z / magnitude * 0.1d;
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!world.isRemote) {
			remove();
		}
	}

	@Override
	protected void onEntityHit(EntityRayTraceResult result) {
		super.onEntityHit(result);
		Entity victim = result.getEntity();
		Entity shooter = getShooter();
		if (shooter instanceof LivingEntity) {
			((LivingEntity) shooter).setLastAttackedEntity(victim);
		}
		boolean success = victim.attackEntityFrom(ModDamageSources.createToothProjectileDamage(this, shooter != null ? shooter : this), damage);
		if (success && victim instanceof LivingEntity) {
			if (!world.isRemote) {
				if (knockback > 0) {
					Vector3d vector3d = getMotion().mul(1d, 0d, 1d).normalize().scale((double) knockback * 0.6d);
					if (vector3d.lengthSquared() > 0d) {
						victim.addVelocity(vector3d.x, 0.1d, vector3d.z);
					}
				}
				if (shooter instanceof LivingEntity) {
					applyEnchantments((LivingEntity) shooter, victim); //thorn & arthropod damage
				}
				if (!isSilent() && victim != shooter && victim instanceof PlayerEntity && shooter instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) shooter).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.HIT_PLAYER_ARROW, 0f));
				}
			}
		}
		playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1.2f / (rand.nextFloat() * 0.2f + 0.9f));
	}

	//onBlockHit
	@Override
	protected void func_230299_a_(BlockRayTraceResult result) {
		super.func_230299_a_(result);
		playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isBurning() {
		return false;
	}

	@Override
	protected boolean isFireballFiery() {
		return false;
	}

	@Override
	protected IParticleData getParticle() {
		return ParticleTypes.POOF;
	}

	@OnlyIn(Dist.CLIENT)
	private static final ItemStack ITEM_TO_RENDER = getItemForRendering();

	@OnlyIn(Dist.CLIENT)
	private static ItemStack getItemForRendering() {
		ItemStack stack = new ItemStack(ModItems.BONE_SCRAPS.get());
		stack.getOrCreateTag().putInt("ScrapType", 1);
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getItem() {
		return ITEM_TO_RENDER;
	}

}
