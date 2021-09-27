package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.ItemBagContainer;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.text.DecimalFormat;

public class ItemBagContainerScreen extends ContainerScreen<ItemBagContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/gulge_gui.png");

	public ItemBagContainerScreen(ItemBagContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		//texture size
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.draw(matrixStack, title, 10, 18 - FONT_Y_SPACING, Color.white.getRGB());

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		String count = df.format(menu.getStoredItemCount()) + "/" + df.format(menu.getMaxItemCount());
		int x = imageWidth - font.width(count) - 8;
		font.draw(matrixStack, count, x, 26 - FONT_Y_SPACING, 0xFFFFFF);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//noinspection ConstantConditions
		minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		int edgeSpacingX = (width - imageWidth) / 2;
		int edgeSpacingY = (height - imageHeight) / 2;
		blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, imageWidth, imageHeight);
	}
}
