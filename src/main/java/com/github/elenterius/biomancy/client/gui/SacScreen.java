package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.world.inventory.menu.SacMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SacScreen extends AbstractContainerScreen<SacMenu> {

	//	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/decomposer_gui.png");
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");

	public SacScreen(SacMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageHeight = 133;
		inventoryLabelY = imageHeight - 94;
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

}
