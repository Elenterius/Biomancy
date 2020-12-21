package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.world.gen.InnerEdgeBiomeLayer;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.LongFunction;

@Mixin(LayerUtil.class)
public abstract class LayerUtilMixin {
	@Redirect(
			method = "func_237216_a_",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/gen/layer/EdgeBiomeLayer;apply(Lnet/minecraft/world/gen/IExtendedNoiseRandom;Lnet/minecraft/world/gen/area/IAreaFactory;)Lnet/minecraft/world/gen/area/IAreaFactory;"
			)
	)
	private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> redirectFunc_237216_a_(EdgeBiomeLayer edgeBiomeLayer, IExtendedNoiseRandom<T> context, IAreaFactory<T> areaFactory, boolean p_237216_0_, int p_237216_1_, int p_237216_2_, LongFunction<C> p_237216_3_) {
		areaFactory = InnerEdgeBiomeLayer.INSTANCE.apply(context, areaFactory);
		areaFactory = EdgeBiomeLayer.INSTANCE.apply(p_237216_3_.apply(1000L), areaFactory);
		return areaFactory;
	}
}
