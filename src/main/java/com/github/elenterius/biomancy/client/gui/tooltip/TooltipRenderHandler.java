package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRarities;
import com.github.elenterius.biomancy.init.client.ModScreens;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TooltipContents;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TooltipRenderHandler {

	private static final ResourceLocation TOOLTIP_OVERLAY_TEXTURE = BiomancyMod.createRL("textures/gui/ui_tooltip.png");
	private static final EmptyLineTooltipComponent EMPTY_LINE = new EmptyLineTooltipComponent();

	private TooltipRenderHandler() {}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color tooltipEvent) {
		ItemStack stack = tooltipEvent.getItemStack();

		if (stack.isEmpty() && ModScreens.isBiomancyScreen(Minecraft.getInstance().screen)) {
			ColorStyles.GENERIC_TOOLTIP.applyColorTo(tooltipEvent);
		}
		else if (stack.getItem() instanceof ICustomTooltip iTooltip) {
			iTooltip.getTooltipStyle().applyColorTo(tooltipEvent);
		}
	}

	@SubscribeEvent
	public static void onGatherTooltipComponents(final RenderTooltipEvent.GatherComponents event) {
		final boolean isTooltip = event.getItemStack().getItem() instanceof ICustomTooltip;

		List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
		for (int i = 0; i < tooltipElements.size(); i++) {
			Either<FormattedText, TooltipComponent> either = tooltipElements.get(i);
			final int index = i;

			//replace formattedText with TooltipComponent
			either.ifLeft(formattedText -> {
				if (formattedText instanceof Component component && component.getContents() instanceof TooltipContents contents) {
					tooltipElements.set(index, Either.right(contents.component()));
				}
				else if (isTooltip && formattedText == Component.EMPTY) { //vanilla bugfix: fixes empty lines disappearing when long text is wrapped
					tooltipElements.set(index, Either.right(EMPTY_LINE));
				}
			});
		}
	}

	public static void onPostRenderTooltip(ItemStack stack, List<ClientTooltipComponent> components, Screen screen, PoseStack poseStack, int posX, int posY, int tooltipWidth, int tooltipHeight) {
		if (!components.isEmpty()) {
			int color = stack.getItem() instanceof ICustomTooltip iTooltip ? iTooltip.getTooltipColorWithAlpha(stack) : ModRarities.getARGBColor(stack);

			int y = posY;
			for (int i = 0; i < components.size(); i++) {
				ClientTooltipComponent clientComponent = components.get(i);
				if (clientComponent instanceof HrTooltipClientComponent hrComponent) {
					hrComponent.renderLine(poseStack, posX, y, tooltipWidth, i, color);
				}
				y += clientComponent.getHeight() + (i == 0 ? 2 : 0);
			}
		}

		//drawTooltipOverlay(poseStack, posX, posY, tooltipWidth, tooltipHeight);
	}

	private static void drawTooltipOverlay(PoseStack poseStack, int posX, int posY, int tooltipWidth, int tooltipHeight) {
		int blitOffset = 400;

		int textureWidth = 64;
		int textureHeight = 32;

		int cornerWidth = 8;
		int cornerHeight = 8;
		int centerWidth = 48;
		int centerHeight = 8;

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TOOLTIP_OVERLAY_TEXTURE);

		//corner pieces
		int cornerOffset = 6;
		GuiComponent.blit(poseStack, posX - cornerOffset, posY - cornerOffset, blitOffset, 0, 0, cornerWidth, cornerHeight, textureWidth, textureHeight);
		GuiComponent.blit(poseStack, posX + tooltipWidth - cornerWidth + cornerOffset, posY - cornerOffset, blitOffset, centerWidth + cornerWidth, 0, cornerWidth, cornerHeight, textureWidth, textureHeight);
		GuiComponent.blit(poseStack, posX - cornerOffset, posY + tooltipHeight - cornerHeight + cornerOffset, blitOffset, 0, cornerHeight, cornerWidth, cornerHeight, textureWidth, textureHeight);
		GuiComponent.blit(poseStack, posX + tooltipWidth - cornerWidth + cornerOffset, posY + tooltipHeight - cornerHeight + cornerOffset, blitOffset, centerWidth + cornerWidth, cornerHeight, cornerWidth, cornerHeight, textureWidth, textureHeight);

		//top and bottom pieces
		if (tooltipWidth >= centerWidth) {
			int centerOffset = 9;
			GuiComponent.blit(poseStack, posX + tooltipWidth / 2 - centerWidth / 2, posY - centerOffset, blitOffset, cornerWidth, 0, centerWidth, centerHeight, textureWidth, textureHeight);
			GuiComponent.blit(poseStack, posX + tooltipWidth / 2 - centerWidth / 2, posY + tooltipHeight - centerHeight + centerOffset, blitOffset, cornerWidth, centerHeight, centerWidth, centerHeight, textureWidth, textureHeight);
		}
	}

}
