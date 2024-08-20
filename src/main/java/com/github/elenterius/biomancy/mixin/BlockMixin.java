package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.item.UnstableCompoundItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

	@Inject(method = "dropFromExplosion(Lnet/minecraft/world/level/Explosion;)Z", at = @At(value = "HEAD"), cancellable = true)
	private void onDropFromExplosion(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
		Block thisBlock = (Block) (Object) this;
		if (thisBlock == Blocks.MAGMA_BLOCK && explosion instanceof UnstableCompoundItem.UnstableExplosion) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V", at = @At(value = "HEAD"))
	private void onWasExploded(Level level, BlockPos pos, Explosion explosion, CallbackInfo ci) {
		if (level.isClientSide) return;

		Block thisBlock = (Block) (Object) this;
		if (thisBlock == Blocks.MAGMA_BLOCK && explosion instanceof UnstableCompoundItem.UnstableExplosion) {
			level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		}
	}

}
