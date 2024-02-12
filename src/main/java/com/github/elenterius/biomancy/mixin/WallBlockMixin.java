package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.block.PaneBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {

	@Inject(method = "connectsTo", at = @At(value = "HEAD"), cancellable = true)
	private void onConnectsTo(BlockState state, boolean sideSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof PaneBlock && PaneBlock.canWallConnectToPane(state, direction)) {
			cir.setReturnValue(true);
		}
	}

}
