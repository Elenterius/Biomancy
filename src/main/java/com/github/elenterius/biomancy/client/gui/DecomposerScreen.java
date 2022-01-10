package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.inventory.DecomposerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class DecomposerScreen extends AbstractContainerScreen<DecomposerMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/decomposer_gui.png");
	private final ProgressBar fuelBar = new ProgressBar(39, 17, 5, 60 - 17, 0xFFb8ba87);

	public DecomposerScreen(DecomposerMenu screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
		//texture size
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.draw(matrixStack, title, 10, 18 - FONT_Y_SPACING, 0xFFFFFF);

//		String craftingProgress = (int) (menu.getCraftingProgressNormalized() * 100) + "%";
//		font.draw(matrixStack, craftingProgress, 155f - font.width(craftingProgress), 52f + 6, 0xFFFFFF);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);

		int edgeSpacingX = (width - imageWidth) / 2;
		int edgeSpacingY = (height - imageHeight) / 2;
		blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, imageWidth, imageHeight);

		float craftingProgress = menu.getCraftingProgressNormalized();
		int uWidth = (int) (craftingProgress * 14) + (craftingProgress > 0 ? 1 : 0);
		blit(matrixStack, leftPos + 81, topPos + 22, 176, 0, uWidth, 7);

		fuelBar.setProgress(menu.getFuelAmountNormalized());
		fuelBar.draw(Objects.requireNonNull(minecraft), matrixStack, leftPos, topPos, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
		//noinspection ConstantConditions
		if (!minecraft.player.getInventory().getSelected().isEmpty()) return;

		List<Component> hoveringText = new ArrayList<>();

		if (fuelBar.isMouseInside(leftPos, topPos, mouseX, mouseY)) {
			int amount = menu.getFuelAmount();
			if (amount > 0) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				hoveringText.add(TextComponentUtil.getTooltipText("nutrients").append(": " + df.format(amount) + " u"));
			}
			else {
				hoveringText.add(TextComponentUtil.getTooltipText("empty"));
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
