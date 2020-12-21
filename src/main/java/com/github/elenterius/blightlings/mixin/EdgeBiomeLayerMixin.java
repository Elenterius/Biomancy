package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.init.ModBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EdgeBiomeLayer.class)
public abstract class EdgeBiomeLayerMixin {
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	protected void injectApply(INoiseRandom context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
		if (center == ModBiomes.BLIGHT_BIOME_ID) {
			int outerEdge = ModBiomes.BLIGHT_BIOME_OUTER_EDGE_ID;
			if (north == outerEdge || west == outerEdge || east == outerEdge || south == outerEdge) {
				cir.setReturnValue(ModBiomes.BLIGHT_BIOME_INNER_EDGE_ID);
			} else {
				cir.setReturnValue(ModBiomes.BLIGHT_BIOME_ID);
			}
		} else if (center == ModBiomes.BLIGHT_BIOME_OUTER_EDGE_ID) {
			int main = ModBiomes.BLIGHT_BIOME_ID;
			if (toInt(north == main) + toInt(west == main) + toInt(east == main) + toInt(south == main) > 2) {
				cir.setReturnValue(ModBiomes.BLIGHT_BIOME_INNER_EDGE_ID);
			}
		}
	}

	private int toInt(boolean flag) {
		return flag ? 1 : 0;
	}
}
