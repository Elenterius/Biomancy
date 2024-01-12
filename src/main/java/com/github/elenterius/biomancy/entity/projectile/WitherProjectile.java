package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

@Deprecated(forRemoval = true)
public class WitherProjectile extends BaseProjectile {

	public WitherProjectile(EntityType<? extends BaseProjectile> entityType, Level world) {
		super(entityType, world);
	}

	public WitherProjectile(Level world, double x, double y, double z) {
		super(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), world, x, y, z);
	}

	@Override
	public float getGravity() {
		return 0.001f;
	}

	@Override
	public float getDrag() {
		return 1f; //essentially move "forever" with the given motion
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide) {
			Entity victim = result.getEntity();
			Entity shooter = getOwner();
			if (shooter instanceof LivingEntity livingShooter) {
				if (victim.hurt(ModDamageSources.createWitherSkullDamage(this, livingShooter), getDamage())) {
					if (!victim.isAlive()) {
						livingShooter.heal(0.625f * getDamage());
						return;
					}

					doEnchantDamageEffects(livingShooter, victim);
					if (victim instanceof LivingEntity livingVictim) {
						livingVictim.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 40, 1));
					}
				}
			}
			else if (victim.hurt(DamageSource.MAGIC, 0.625f * getDamage()) && victim instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 40, 1));
			}
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!level.isClientSide) {
			Explosion.BlockInteraction explosionMode = ForgeEventFactory.getMobGriefingEvent(level, getOwner()) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
			level.explode(this, getX(), getY(), getZ(), 1.0F, false, explosionMode);
			discard();
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
