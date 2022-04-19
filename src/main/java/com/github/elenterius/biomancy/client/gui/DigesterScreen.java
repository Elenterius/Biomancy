package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.inventory.menu.DigesterMenu;
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
public class DigesterScreen extends AbstractContainerScreen<DigesterMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_digester.png");

	public DigesterScreen(DigesterMenu menu, Inventory playerInventory, Component title) {
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
		blit(poseStack, leftPos + 78, topPos + 17, 194, 0, uWidth, 2);
	}

	private void drawFuelBar(PoseStack poseStack, float fuelPct) {
		//fuel blob
		int vHeight = (int) (fuelPct * 18) + (fuelPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 44, topPos + 18 + 18 - vHeight, 176, 18 - vHeight, 18, vHeight);
		//glass highlight
		blit(poseStack, leftPos + 47, topPos + 21, 214, 0, 12, 13);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (GuiUtil.isInRect(leftPos + 52, topPos + 20, 17, 17, mouseX, mouseY)) {
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
