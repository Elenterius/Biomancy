package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.enchantment.AttunedDamageEnchantment;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneContainer.class)
public abstract class GrindstoneContainerMixin {

	@Inject(method = "removeEnchantments", at = @At(value = "TAIL"))
	protected void onRemoveEnchantments(ItemStack stack, int damage, int count, CallbackInfoReturnable<ItemStack> cir) {
		cir.getReturnValue().removeChildTag(AttunedDamageEnchantment.NBT_KEY);
	}

}
