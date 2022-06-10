package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.inventory.menu.DecomposerMenu;
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
public class DecomposerScreen extends AbstractContainerScreen<DecomposerMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_decomposer.png");

	public DecomposerScreen(DecomposerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageHeight = 193;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		int posX = imageWidth / 2 - font.width(title) / 2;
		font.draw(poseStack, title, posX, -12, 0xFFFFFF);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);

		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		drawProgressBar(poseStack, menu.getCraftingProgressNormalized());
		drawFuelBar(poseStack, menu.getFuelAmountNormalized());
	}

	private void drawProgressBar(PoseStack poseStack, float craftingPct) {
		int uWidth = (int) (craftingPct * 20) + (craftingPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 64, topPos + 19, 194, 0, uWidth, 2);
	}

	private void drawFuelBar(PoseStack poseStack, float fuelPct) {
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 44, topPos + 26 + 36 - vHeight, 178, 36 - vHeight, 5, vHeight);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (GuiUtil.isInRect(leftPos + 44, topPos + 26, 5, 36, mouseX, mouseY)) {
				drawFuelTooltip(poseStack, mouseX, mouseY);
				return;
			}
		}

		super.renderTooltip(poseStack, mouseX, mouseY);
	}

	private void drawFuelTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		int maxFuel = menu.getMAxFuelAmount();
		int fuelAmount = menu.getFuelAmount();
		int totalFuelCost = menu.getTotalFuelCost();
		GuiRenderUtil.drawFuelTooltip(this, poseStack, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
	}

}
