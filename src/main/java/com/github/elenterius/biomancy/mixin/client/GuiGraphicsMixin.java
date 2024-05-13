package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.client.gui.tooltip.TooltipHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

	@Shadow(remap = false)
	private ItemStack tooltipStack;

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
	private void onPostRenderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci, RenderTooltipEvent.Pre event, int width, int height, int w, int h, Vector2ic pos) {
		GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
		TooltipHandler.onPostRenderTooltip(tooltipStack, components, font, guiGraphics, pos.x(), pos.y(), width, height);
	}

	@Surrogate
	private void onPostRenderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci, Object event, int width, int height, int w, int h, Vector2ic pos) {
		onPostRenderTooltipInternal(font, components, mouseX, mouseY, positioner, ci, (RenderTooltipEvent.Pre) event, width, height, w, h, pos);
	}

}
