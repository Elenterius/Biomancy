package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.client.gui.tooltip.TooltipRenderHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin {

	@Shadow
	private ItemStack tooltipStack;

	@Inject(method = "renderTooltipInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onRenderTooltipInternal(PoseStack poseStack, List<ClientTooltipComponent> tooltips, int mouseX, int mouseY, CallbackInfo ci, RenderTooltipEvent.Pre event, int width, int height, int posX, int posY) {
		Screen screen = (Screen) (Object) this;
		TooltipRenderHandler.onPostRenderTooltip(tooltipStack, tooltips, screen, poseStack, posX, posY, width, height);
	}

}
