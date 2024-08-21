package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BloomberryProjectile extends BaseProjectile implements GeoEntity {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public BloomberryProjectile(EntityType<? extends BloomberryProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public BloomberryProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.BLOOMBERRY_PROJECTILE.get(), level, x, y, z);
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
		return false; //explode on death?
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (level() instanceof ServerLevel serverLevel) {
			Direction direction = result.getDirection();
			PrimordialEcosystem.placeBloomOrBlocks(serverLevel, result.getBlockPos(), direction);
		}
		playSound(SoundEvents.SLIME_BLOCK_BREAK, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (level() instanceof ServerLevel serverLevel) {
			if (result.getEntity() instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION.get(), 20 * 60), this);
			}
			else {
				Direction direction = Direction.orderedByNearest(this)[0];
				BlockPos pos = BlockPos.containing(result.getLocation());
				PrimordialEcosystem.placeBloomOrBlocks(serverLevel, pos, direction);
			}
		}
		playSound(SoundEvents.SLIME_BLOCK_BREAK, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected ParticleOptions getParticle() {
		return ParticleTypes.SPIT;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//do nothing
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
