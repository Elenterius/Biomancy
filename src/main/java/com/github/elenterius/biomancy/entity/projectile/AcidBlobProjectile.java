package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.block.cauldron.AcidCauldron;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcidBlobProjectile extends AcidSpitProjectile implements GeoEntity {

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	protected boolean canPlaceAcidFluid = true;

	public AcidBlobProjectile(EntityType<? extends BaseProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public AcidBlobProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.ACID_BLOB_PROJECTILE.get(), level, x, y, z);
	}

	public AcidBlobProjectile(Level level, double x, double y, double z, boolean canPlaceAcidFluid) {
		super(ModEntityTypes.ACID_BLOB_PROJECTILE.get(), level, x, y, z);
		this.canPlaceAcidFluid = canPlaceAcidFluid;
	}

	public void setCanPlaceAcidFluid(boolean flag) {
		canPlaceAcidFluid = flag;
	}

	public boolean canPlaceAcidFluid() {
		return canPlaceAcidFluid;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		canPlaceAcidFluid = tag.getBoolean("place_acid_fluid");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("place_acid_fluid", canPlaceAcidFluid);
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {

		if (canPlaceAcidFluid) {
			if (placeAcidFluid(result)) return;
		}

		super.onHitBlock(result);
	}

	@Override
	protected void onInsideBlock(BlockState state) {
		if (!level().isClientSide && canPlaceAcidFluid) {
			if (state.getBlock() == Blocks.CAULDRON || (state.getBlock() instanceof AcidCauldron cauldron && !cauldron.isFull(state))) {
				level().setBlockAndUpdate(blockPosition(), ModBlocks.ACID_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
			}
		}
	}

	private boolean placeAcidFluid(BlockHitResult result) {
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
				return true;
			}
			else if (stateRelative.getBlock() instanceof FleshVeinsBlock) {
				if (!level().isClientSide) {
					level().setBlock(posRelative, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
				}
				playHitSound();
				return true;
			}
		}
		return false;
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
