package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.github.elenterius.biomancy.util.shooting.ProjectileEntityType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class AcidSpitProjectile extends BaseProjectile {

	public AcidSpitProjectile(ProjectileEntityType<? extends AcidSpitProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		//		if (isInvulnerableTo(source)) return false;

		//		markHurt();
		//		Entity attacker = source.getEntity();
		//		if (attacker != null) {
		//			if (!level.isClientSide) {
		//				setDeltaMovement(attacker.getLookAngle());
		//				setOwner(attacker);
		//			}
		//			return true;
		//		}

		//TODO: explode into AOE cloud when damage is physical
		return false;
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		playHitSound();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide) {
			Entity victim = result.getEntity();
			Entity owner = getOwner();

			DamageSource acidDamageSource = ModDamageSources.acidProjectile(level(), this, owner);
			victim.hurt(acidDamageSource, getDamage());

			if (victim instanceof LivingEntity livingVictim && !livingVictim.isInvulnerableTo(acidDamageSource)) {
				StatusEffectHandler.applyCorrosiveEffect(livingVictim, 4);
			}

			if (owner instanceof LivingEntity shooter) {
				doEnchantDamageEffects(shooter, victim);
			}

		}
		playHitSound();
	}

	protected void playHitSound() {
		playSound(SoundEvents.SLIME_BLOCK_BREAK, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected ParticleOptions getParticle() {
		return ParticleTypes.SPIT;
	}

}
