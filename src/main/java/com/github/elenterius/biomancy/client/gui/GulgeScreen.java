package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.world.inventory.GulgeMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public class GulgeScreen extends AbstractContainerScreen<GulgeMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/gulge_gui.png");

	public GulgeScreen(GulgeMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.draw(poseStack, title, 10, 18 - FONT_Y_SPACING, Color.white.getRGB());

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		String count = df.format(menu.getStoredItemCount()) + "/" + df.format(menu.getMaxItemCount());
		int x = imageWidth - font.width(count) - 8;
		font.draw(poseStack, count, x, 26 - FONT_Y_SPACING, 0xFFFFFF);
	}

}
