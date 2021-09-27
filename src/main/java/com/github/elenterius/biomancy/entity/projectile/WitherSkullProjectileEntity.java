package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class WitherSkullProjectileEntity extends AbstractProjectileEntity {

	public WitherSkullProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public WitherSkullProjectileEntity(World world, double x, double y, double z) {
		super(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), world, x, y, z);
	}

	public WitherSkullProjectileEntity(World world, LivingEntity shooter) {
		super(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), world, shooter);
	}

	@Override
	public float getGravity() {
		return 0.001f;
	}

	@Override
	public float getDrag() {
		return 1f; //essentially move "forever" with given motion
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide) {
			Entity victim = result.getEntity();
			Entity shooter = getOwner();
			if (shooter instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) shooter;
				if (victim.hurt(ModDamageSources.createWitherSkullDamage(this, livingentity), getDamage())) {
					if (!victim.isAlive()) {
						livingentity.heal(0.625f * getDamage());
						return;
					}

					doEnchantDamageEffects(livingentity, victim);
					if (victim instanceof LivingEntity) {
						int duration = (level.getDifficulty().getId() - 1) * 20;
						if (duration > 0) {
							((LivingEntity) victim).addEffect(new EffectInstance(Effects.WITHER, 20 * duration, 1));
						}
					}
				}
			}
			else if (victim.hurt(DamageSource.MAGIC, 0.625f * getDamage()) && victim instanceof LivingEntity) {
				int duration = (level.getDifficulty().getId() - 1) * 20;
				if (duration > 0) {
					((LivingEntity) victim).addEffect(new EffectInstance(Effects.WITHER, 20 * duration, 1));
				}
			}
		}
	}

	@Override
	protected void onHit(RayTraceResult result) {
		super.onHit(result);
		if (!level.isClientSide) {
			Explosion.Mode explosionMode = ForgeEventFactory.getMobGriefingEvent(level, getOwner()) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
			level.explode(this, getX(), getY(), getZ(), 1.0F, false, explosionMode);
			remove();
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isOnFire() {
		return false;
	}
}
