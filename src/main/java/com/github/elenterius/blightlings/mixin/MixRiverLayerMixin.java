package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.init.ModBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.MixRiverLayer;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MixRiverLayer.class)
public abstract class MixRiverLayerMixin implements IDimOffset0Transformer {
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	protected void onApply(INoiseRandom p_215723_1_, IArea biomeArea, IArea riverArea, int posX, int posZ, CallbackInfoReturnable<Integer> cir) {
		int i = biomeArea.getValue(getOffsetX(posX), getOffsetZ(posZ));
		// prevents the generation of rivers in the following biomes
		if (i == ModBiomes.BLIGHT_BIOME_ID || i == ModBiomes.BLIGHT_BIOME_INNER_EDGE_ID || i == ModBiomes.BLIGHT_BIOME_OUTER_EDGE_ID) {
			cir.setReturnValue(i);
		}
	}
}
