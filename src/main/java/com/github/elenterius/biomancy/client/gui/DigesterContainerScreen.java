package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.drawable.FluidTankBar;
import com.github.elenterius.biomancy.inventory.DigesterContainer;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DigesterContainerScreen extends ContainerScreen<DigesterContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/digester_gui.png");
	private final FluidTankBar fuelTankBar = new FluidTankBar(39, 17, 5, 60 - 17);
	private final FluidTankBar outputTankBar = new FluidTankBar(120, 17, 5, 60 - 17);

	public DigesterContainerScreen(DigesterContainer container, PlayerInventory inv, ITextComponent titleIn) {
		super(container, inv, titleIn);
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
		font.draw(matrixStack, title, 10, 18 - FONT_Y_SPACING, 0xFFFFFF);

		String craftingProgress = (int) (menu.getCraftingProgressNormalized() * 100) + "%";
		font.draw(matrixStack, craftingProgress, 155 - font.width(craftingProgress), 52 + 6, 0xFFFFFF);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1f, 1f, 1f, 1f);
		//noinspection ConstantConditions
		minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		int edgeSpacingX = (width - imageWidth) / 2;
		int edgeSpacingY = (height - imageHeight) / 2;
		blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, imageWidth, imageHeight);

		fuelTankBar.update(menu.getFuelTank());
		fuelTankBar.draw(minecraft, matrixStack, leftPos, topPos, mouseX, mouseY);
		outputTankBar.update(menu.getOutputTank());
		outputTankBar.draw(minecraft, matrixStack, leftPos, topPos, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		//noinspection ConstantConditions
		if (!minecraft.player.inventory.getCarried().isEmpty()) return;

		List<ITextComponent> hoveringText = new ArrayList<>();

		if (fuelTankBar.isMouseInside(leftPos, topPos, mouseX, mouseY)) {
			int amount = menu.getFuelTank().getFluidAmount();
			if (amount > 0) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				hoveringText.add(new TranslationTextComponent(menu.getFuelTank().getFluid().getTranslationKey()).append(": " + df.format(amount) + " mb"));
			}
			else {
				hoveringText.add(ClientTextUtil.getTooltipText("empty"));
			}
		}
		else if (outputTankBar.isMouseInside(leftPos, topPos, mouseX, mouseY)) {
			int amount = menu.getOutputTank().getFluidAmount();
			if (amount > 0) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				hoveringText.add(new TranslationTextComponent(menu.getOutputTank().getFluid().getTranslationKey()).append(": " + df.format(amount) + " mb"));
			}
			else {
				hoveringText.add(ClientTextUtil.getTooltipText("empty"));
			}
		}

		if (!hoveringText.isEmpty()) {
			renderComponentTooltip(matrixStack, hoveringText, mouseX, mouseY);
		}
		else {
			super.renderTooltip(matrixStack, mouseX, mouseY);
		}
	}
}
