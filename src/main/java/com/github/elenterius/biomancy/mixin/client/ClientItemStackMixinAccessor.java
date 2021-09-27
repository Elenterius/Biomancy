package com.github.elenterius.biomancy.mixin.client;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ClientItemStackMixinAccessor {
	// client side
	@Invoker("getHideFlags")
	int biomancy_getHideFlags();

	//client side
	@Invoker("shouldShowInTooltip")
	static boolean biomancy_isToolTipVisible(int hideFlags, ItemStack.TooltipDisplayFlags tooltipFlag) {
		return false;
	}
}
