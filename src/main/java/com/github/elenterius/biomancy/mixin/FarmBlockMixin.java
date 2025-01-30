package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.block.WaterGelBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

	@WrapOperation(method = "isNearWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
	private static FluidState onIsNearWaterGetFluidState(LevelReader instance, BlockPos pos, Operation<FluidState> original) {
		if (instance.getBlockState(pos).getBlock() instanceof WaterGelBlock) {
			return Fluids.WATER.getSource(false);
		}
		return original.call(instance, pos);
	}

}
