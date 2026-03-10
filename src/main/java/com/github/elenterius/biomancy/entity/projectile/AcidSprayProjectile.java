package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.github.elenterius.biomancy.util.shooting.ProjectileEntityType;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;

public class AcidSprayProjectile extends BaseProjectile {

	public static final int LIFESPAN = 5 * SharedConstants.TICKS_PER_SECOND;
	protected static final int MAX_TRAIL_LENGTH = 8;

	private final Deque<Vec3> trailPositions = new ArrayDeque<>(MAX_TRAIL_LENGTH);

	public AcidSprayProjectile(ProjectileEntityType<? extends AcidSprayProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void tick() {
		super.tick();
		if (tickCount > LIFESPAN && !level().isClientSide) discard();

		if (level().isClientSide) {
			trailPositions.addFirst(position());
			while (trailPositions.size() > MAX_TRAIL_LENGTH) {
				trailPositions.removeLast();
			}
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (level().isClientSide) return;

		Entity victim = result.getEntity();
		Entity owner = getOwner();

		DamageSource acidDamageSource = ModDamageSources.acidProjectile(level(), this, owner);
		victim.hurt(acidDamageSource, getDamage());

		if (victim instanceof LivingEntity livingVictim && !livingVictim.isInvulnerableTo(acidDamageSource)) {
			StatusEffectHandler.applyCorrosiveEffect(livingVictim, 1);
		}

		if (owner instanceof LivingEntity shooter) {
			doEnchantDamageEffects(shooter, victim);
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		super.onHitBlock(hitResult);
		if (level() instanceof ServerLevel serverLevel && random.nextFloat() < 0.4) {
			ModBlocks.ACID_SPLATTER.get().propagateSplatters(serverLevel, getImpactPos(hitResult), 0, random);
		}
	}

	protected BlockPos getImpactPos(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
			return blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
		}
		return blockPosition();
	}

	@Override
	protected void spawnParticle(double x, double y, double z) {
		//do nothing
	}

	public Vec3[] getTrailPositions() {
		return trailPositions.toArray(Vec3[]::new);
	}

}
