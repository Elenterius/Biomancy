package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SuspiciousStewItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@Mixin(SuspiciousStewItem.class)
public interface SuspiciousStewItemAccessor {

	@Invoker("listPotionEffects")
	static void biomancy$ListPotionEffects(ItemStack stack, Consumer<MobEffectInstance> output) {}

}
