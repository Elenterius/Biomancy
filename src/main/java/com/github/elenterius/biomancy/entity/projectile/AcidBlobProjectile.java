package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcidBlobProjectile extends CorrosiveAcidProjectile implements GeoEntity {

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public AcidBlobProjectile(EntityType<? extends BaseProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public AcidBlobProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.ACID_BLOB_PROJECTILE.get(), level, x, y, z);
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		BlockPos pos = result.getBlockPos();
		BlockPos posRelative = pos.relative(result.getDirection());
		BlockState stateRelative = level().getBlockState(posRelative);

		if (stateRelative.canBeReplaced(ModFluids.ACID.get())) {
			BlockState stateBelow = level().getBlockState(posRelative.below());
			if (stateBelow.is(ModBlocks.ACID_FLUID_BLOCK.get()) || stateBelow.isFaceSturdy(level(), posRelative.below(), Direction.UP)) {
				if (!level().isClientSide) {
					level().setBlock(posRelative, ModBlocks.ACID_FLUID_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
				}
				playHitSound();
				return;
			}
			else if (stateRelative.getBlock() instanceof FleshVeinsBlock) {
				if (!level().isClientSide) {
					level().setBlock(posRelative, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
				}
				playHitSound();
				return;
			}
		}

		super.onHitBlock(result);
	}

	@Override
	protected ParticleOptions getParticle() {
		return ModParticleTypes.FALLING_ACID.get();
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
