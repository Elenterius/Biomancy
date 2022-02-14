package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class DecomposerScreen extends AbstractContainerScreen<DecomposerMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/decomposer_gui.png");
	private final ProgressBar fuelBar = new ProgressBar(39, 17, 5, 60 - 17, 0xFFb8ba87);

	public DecomposerScreen(DecomposerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.draw(poseStack, title, 10, 18 - FONT_Y_SPACING, 0xFFFFFF);

//		String craftingProgress = (int) (menu.getCraftingProgressNormalized() * 100) + "%";
//		font.draw(poseStack, craftingProgress, 155f - font.width(craftingProgress), 52f + 6, 0xFFFFFF);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);

		int edgeSpacingX = (width - imageWidth) / 2;
		int edgeSpacingY = (height - imageHeight) / 2;
		blit(poseStack, edgeSpacingX, edgeSpacingY, 0, 0, imageWidth, imageHeight);

		float craftingProgress = menu.getCraftingProgressNormalized();
		int uWidth = (int) (craftingProgress * 14) + (craftingProgress > 0 ? 1 : 0);
		blit(poseStack, leftPos + 81, topPos + 22, 176, 0, uWidth, 7);

		fuelBar.setProgress(menu.getFuelAmountNormalized());
		fuelBar.draw(Objects.requireNonNull(minecraft), poseStack, leftPos, topPos, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			List<Component> hoveringText = new ArrayList<>();

			if (fuelBar.isMouseInside(leftPos, topPos, mouseX, mouseY)) {
				int amount = menu.getFuelAmount();
				if (amount > 0) {
					DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
					hoveringText.add(TextComponentUtil.getTooltipText("nutrients_fuel").append(": " + df.format(amount) + " u"));
				}
				else {
					hoveringText.add(TextComponentUtil.getTooltipText("empty"));
				}
			}

			if (!hoveringText.isEmpty()) {
				renderComponentTooltip(poseStack, hoveringText, mouseX, mouseY);
				return;
			}
		}

		super.renderTooltip(poseStack, mouseX, mouseY);
	}

}
