package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ToolTipHandler
{
    @SubscribeEvent
    public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
        ItemStack stack = event.getStack();
        if (!stack.isEmpty() && stack.getItem().getGroup() == BlightlingsMod.ITEM_GROUP) {
            event.setBackground(0xED000000);
            int borderColorStart = 0xFFAAAAAA;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            event.setBorderStart(borderColorStart);
            event.setBorderEnd(borderColorEnd);
        }
    }
}
