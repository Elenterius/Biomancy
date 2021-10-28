package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.handler.event.StatusEffectHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;eat(IF)V", shift = At.Shift.AFTER))
	protected void onCakeFoodEaten(IWorld level, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResultType> cir) {
		if (!level.isClientSide()) {
			StatusEffectHandler.reduceAdrenalFatigue(2, player);
		}
	}

}
