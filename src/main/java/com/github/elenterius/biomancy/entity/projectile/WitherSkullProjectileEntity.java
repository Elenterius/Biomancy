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
	protected void onEntityHit(EntityRayTraceResult result) {
		super.onEntityHit(result);
		if (!world.isRemote) {
			Entity victim = result.getEntity();
			Entity shooter = getShooter();
			if (shooter instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) shooter;
				if (victim.attackEntityFrom(ModDamageSources.createWitherSkullDamage(this, livingentity), getDamage())) {
					if (!victim.isAlive()) {
						livingentity.heal(0.625f * getDamage());
						return;
					}

					applyEnchantments(livingentity, victim);
					if (victim instanceof LivingEntity) {
						int duration = (world.getDifficulty().getId() - 1) * 20;
						if (duration > 0) {
							((LivingEntity) victim).addPotionEffect(new EffectInstance(Effects.WITHER, 20 * duration, 1));
						}
					}
				}
			}
			else if (victim.attackEntityFrom(DamageSource.MAGIC, 0.625f * getDamage()) && victim instanceof LivingEntity) {
				int duration = (world.getDifficulty().getId() - 1) * 20;
				if (duration > 0) {
					((LivingEntity) victim).addPotionEffect(new EffectInstance(Effects.WITHER, 20 * duration, 1));
				}
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!world.isRemote) {
			Explosion.Mode explosion$mode = ForgeEventFactory.getMobGriefingEvent(world, getShooter()) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
			world.createExplosion(this, getPosX(), getPosY(), getPosZ(), 1.0F, false, explosion$mode);
			remove();
		}
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
}
