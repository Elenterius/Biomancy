package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DigDurabilityEnchantment.class)
public class UnbreakingEnchantmentMixin {

	@Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
	private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof LivingTool) {
			cir.setReturnValue(false);
		}
	}

}
