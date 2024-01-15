package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class BloomberryProjectile extends BaseProjectile implements IAnimatable {

	protected final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

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
		if (level instanceof ServerLevel serverLevel) {
			Direction direction = result.getDirection();
			PrimordialEcosystem.placeBloomOrBlocks(serverLevel, result.getBlockPos(), direction);
		}
		playSound(SoundEvents.SLIME_BLOCK_BREAK, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (level instanceof ServerLevel serverLevel) {
			Direction direction = Direction.orderedByNearest(this)[0];
			BlockPos pos = new BlockPos(result.getLocation());
			PrimordialEcosystem.placeBloomOrBlocks(serverLevel, pos, direction);
		}
		playSound(SoundEvents.SLIME_BLOCK_BREAK, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected ParticleOptions getParticle() {
		return ParticleTypes.SPIT;
	}

	@Override
	public void registerControllers(AnimationData data) {
		//do nothing
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}
}
