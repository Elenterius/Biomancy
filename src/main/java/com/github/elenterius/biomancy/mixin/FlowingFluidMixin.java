package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

	@Unique
	private boolean biomancy$isLavaFluid() {
		FlowingFluid fluid = (FlowingFluid) (Object) this;
		return fluid instanceof LavaFluid;
	}

	@Inject(method = "canPassThrough", at = @At(value = "HEAD"), cancellable = true)
	private void onCanPassThrough(BlockGetter level, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, CallbackInfoReturnable<Boolean> cir) {
		if (biomancy$isLavaFluid() && toBlockState.is(ModBlockTags.LAVA_DESTRUCTIBLE)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "canSpreadTo", at = @At(value = "HEAD"), cancellable = true)
	private void onCanSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
		if (biomancy$isLavaFluid() && toBlockState.is(ModBlockTags.LAVA_DESTRUCTIBLE)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "spreadTo", at = @At(value = "HEAD"), cancellable = true)
	private void onSpreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci) {
		if (biomancy$isLavaFluid() && state.is(ModBlockTags.LAVA_DESTRUCTIBLE)) {
			level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);
			ci.cancel();
		}
	}

}
