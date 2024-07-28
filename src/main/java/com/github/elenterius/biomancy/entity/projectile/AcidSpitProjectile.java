package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class AcidSpitProjectile extends BaseProjectile {

	public AcidSpitProjectile(EntityType<? extends BaseProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public AcidSpitProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.CORROSIVE_ACID_PROJECTILE.get(), level, x, y, z);
	}

	public AcidSpitProjectile(EntityType<? extends AcidSpitProjectile> entityType, Level level, double x, double y, double z) {
		super(entityType, level, x, y, z);
	}

	@Override
	public float getGravity() {
		return 0.025f;
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

			victim.hurt(ModDamageSources.acidProjectile(level(), this, owner), getDamage());

			if (victim instanceof LivingEntity livingVictim) {
				CombatUtil.applyAcidEffect(livingVictim, 4);
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
