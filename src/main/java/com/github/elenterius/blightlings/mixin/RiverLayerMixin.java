package com.github.elenterius.blightlings.mixin;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.RiverLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RiverLayer.class)
public abstract class RiverLayerMixin
{
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    protected void injectApply(INoiseRandom context, int north, int west, int south, int east, int center, CallbackInfoReturnable<Integer> cir) {
        // doesn't work :(
        //        if (center == ModWorldGen.BLIGHT_BIOME_ID || north == ModWorldGen.BLIGHT_BIOME_ID || west == ModWorldGen.BLIGHT_BIOME_ID || south == ModWorldGen.BLIGHT_BIOME_ID || east == ModWorldGen.BLIGHT_BIOME_ID) {
        //            cir.setReturnValue(ModWorldGen.BLIGHT_BIOME_ID);
        //        }
    }
}
