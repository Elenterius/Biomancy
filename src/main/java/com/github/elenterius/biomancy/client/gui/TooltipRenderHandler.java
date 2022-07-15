package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.init.ModRarities;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TooltipRenderHandler {

	private static final ResourceLocation TOOLTIP_OVERLAY_TEXTURE = BiomancyMod.createRL("textures/gui/ui_tooltip.png");

	private TooltipRenderHandler() {}

	private static boolean isBiomancyItem(ItemStack stack) {
		if (stack.getItem() instanceof IBiomancyItem) return true;
		ResourceLocation id = stack.getItem().getRegistryName();
		return id != null && id.getNamespace().equals(BiomancyMod.MOD_ID);
	}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
		ItemStack stack = event.getItemStack();

		if (stack.isEmpty() && ModMenuTypes.isBiomancyScreen(Minecraft.getInstance().screen)) {
			event.setBackground(ColorTheme.TOOLTIP_BACKGROUND_ARGB);
			event.setBorderStart(ColorTheme.TOOLTIP_BORDER_START_ARGB);
			event.setBorderEnd(ColorTheme.TOOLTIP_BORDER_END_ARGB);
		} else if (isBiomancyItem(stack)) {
			event.setBackground(ColorTheme.TOOLTIP_BACKGROUND_ARGB);
			int customColor = ModRarities.getRGBColor(stack);
			if (customColor > -1) {
				//convert rgb to argb color
				event.setBorderStart(0xFE_000000 | customColor); //fake color difference with lower alpha value
				event.setBorderEnd(0xFF_000000 | customColor);
			} else {
				event.setBorderStart(ColorTheme.TOOLTIP_BORDER_START_ARGB);
				event.setBorderEnd(ColorTheme.TOOLTIP_BORDER_END_ARGB);
			}
		}
	}

	//	@SubscribeEvent
	//	public static void onRenderTooltipComponent(final RenderTooltipEvent.GatherComponents event) {
	//		ItemStack stack = event.getItemStack();
	//		if (stack.isEmpty() || event.getTooltipElements().isEmpty()) return;
	//
	//		int customColor = ModRarities.getRGBColor(stack);
	//		if (customColor > -1) {
	//			//we assume the first text line we find is the item display name and modify its color if possible
	//			for (int i = 0; i < event.getTooltipElements().size(); i++) {
	//				Optional<FormattedText> left = event.getTooltipElements().get(i).left();
	//				if (left.isPresent()) {
	//					FormattedText formattedText = left.get();
	//					if (formattedText instanceof MutableComponent mutableComponent) mutableComponent.withStyle(style -> style.withColor(customColor));
	//					FormattedCharSequence visualOrderText = formattedText instanceof Component component ? component.getVisualOrderText() : Language.getInstance().getVisualOrder(formattedText);
	//					event.getTooltipElements().set(i, Either.right(new TabTooltipComponent(visualOrderText)));
	//					break;
	//				}
	//			}
	//		}
	//	}

	public static void onPostRenderTooltip(ItemStack stack, Screen screen, PoseStack poseStack, int posX, int posY, int tooltipWidth, int tooltipHeight) {
		if (isBiomancyItem(stack) && stack.getRarity() != Rarity.COMMON) {
			drawTooltipOverlay(poseStack, posX, posY, tooltipWidth, tooltipHeight);
		}
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
