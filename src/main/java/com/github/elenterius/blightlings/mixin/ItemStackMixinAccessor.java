package com.github.elenterius.blightlings.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ItemStackMixinAccessor {
    // client side
    @Invoker(value = "func_242393_J")
    int getHideFlags();

    //client side
    @Invoker(value = "func_242394_a")
    static boolean isToolTipVisible(int hideFlags, ItemStack.TooltipDisplayFlags tooltipFlag) {
        return false;
    }
}
