package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.init.ModRarities;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TooltipContents;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FleshTongue;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TooltipHandler {

	//	private static final ResourceLocation TOOLTIP_OVERLAY_TEXTURE = BiomancyMod.createRL("textures/gui/ui_tooltip.png");
	private static final EmptyLineTooltipComponent EMPTY_LINE = new EmptyLineTooltipComponent();

	private TooltipHandler() {}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color tooltipEvent) {
		ItemStack stack = tooltipEvent.getItemStack();

		if (stack.isEmpty() && Minecraft.getInstance().screen instanceof ScreenTooltipStyleProvider screenStyleProvider) {
			screenStyleProvider.getTooltipStyle().applyColorTo(tooltipEvent);
		}
		else if (stack.getItem() instanceof ItemTooltipStyleProvider itemStyleProvider) {
			itemStyleProvider.getTooltipStyle().applyColorTo(tooltipEvent);
		}
	}

	@SubscribeEvent
	public static void onGetTooltipLines(ItemTooltipEvent event) {
		if (event.getEntity() == null) return;

		List<Component> fleshTongue = FleshTongue.getItemComment(event.getItemStack().getItem());
		if (fleshTongue != null) {
			event.getToolTip().addAll(1, fleshTongue);
		}
	}

	@SubscribeEvent
	public static void onGatherTooltipComponents(final RenderTooltipEvent.GatherComponents event) {
		ItemStack stack = event.getItemStack();
		final boolean isTooltip = stack.getItem() instanceof ItemTooltipStyleProvider;

		List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
		for (int i = 0; i < tooltipElements.size(); i++) {
			Either<FormattedText, TooltipComponent> either = tooltipElements.get(i);
			final int index = i;

			//replace formattedText with TooltipComponent
			either.ifLeft(formattedText -> {
				if (formattedText instanceof Component component) {
					ComponentContents componentContents = component.getContents();
					if (componentContents instanceof TooltipContents contents) {
						tooltipElements.set(index, Either.right(contents.component()));
					}
					else if (isTooltip && component == CommonComponents.EMPTY) { //vanilla bugfix: fixes empty lines disappearing when long text is wrapped
						tooltipElements.set(index, Either.right(EMPTY_LINE));
					}
				}
			});
		}

		if (Minecraft.getInstance().screen instanceof ScreenNutrientFuelConsumer) {
			int fuelValue = Nutrients.getFuelValue(stack);
			int repairValue = Nutrients.getRepairValue(stack);
			if (fuelValue > 0 || repairValue > 0) {
				tooltipElements.add(Either.right(EMPTY_LINE));
				tooltipElements.add(Either.left(ComponentUtil.translatable("tooltip.biomancy.nutrients_fuel").withStyle(TextStyles.NUTRIENTS)));
				if (fuelValue > 0) tooltipElements.add(Either.left(ComponentUtil.literal(" +" + fuelValue + " Fuel").withStyle(TextStyles.GRAY)));
				if (repairValue > 0) tooltipElements.add(Either.left(ComponentUtil.literal(" +" + repairValue + " Repair").withStyle(TextStyles.GRAY)));
			}
		}
	}

	public static void onPostRenderTooltip(ItemStack stack, List<ClientTooltipComponent> components, Font font, GuiGraphics guiGraphics, int posX, int posY, int tooltipWidth, int tooltipHeight) {
		if (!components.isEmpty()) {
			int color = stack.getItem() instanceof ItemTooltipStyleProvider provider ? provider.getTooltipColorWithAlpha(stack) : ModRarities.getARGBColor(stack);

			int y = posY;
			for (int i = 0; i < components.size(); i++) {
				ClientTooltipComponent clientComponent = components.get(i);
				if (clientComponent instanceof HrTooltipClientComponent hrComponent) {
					hrComponent.renderLine(guiGraphics, posX, y, tooltipWidth, i, color);
				}
				y += clientComponent.getHeight() + (i == 0 ? 2 : 0);
			}
		}

		//drawTooltipOverlay(poseStack, posX, posY, tooltipWidth, tooltipHeight);
	}

	//	private static void drawTooltipOverlay(PoseStack poseStack, int posX, int posY, int tooltipWidth, int tooltipHeight) {
	//		int blitOffset = 400;
	//
	//		int textureWidth = 64;
	//		int textureHeight = 32;
	//
	//		int cornerWidth = 8;
	//		int cornerHeight = 8;
	//		int centerWidth = 48;
	//		int centerHeight = 8;
	//
	//		RenderSystem.setShaderColor(1, 1, 1, 1);
	//		RenderSystem.setShaderTexture(0, TOOLTIP_OVERLAY_TEXTURE);
	//
	//		//corner pieces
	//		int cornerOffset = 6;
	//		GuiComponent.blit(poseStack, posX - cornerOffset, posY - cornerOffset, blitOffset, 0, 0, cornerWidth, cornerHeight, textureWidth, textureHeight);
	//		GuiComponent.blit(poseStack, posX + tooltipWidth - cornerWidth + cornerOffset, posY - cornerOffset, blitOffset, centerWidth + cornerWidth, 0, cornerWidth, cornerHeight, textureWidth, textureHeight);
	//		GuiComponent.blit(poseStack, posX - cornerOffset, posY + tooltipHeight - cornerHeight + cornerOffset, blitOffset, 0, cornerHeight, cornerWidth, cornerHeight, textureWidth, textureHeight);
	//		GuiComponent.blit(poseStack, posX + tooltipWidth - cornerWidth + cornerOffset, posY + tooltipHeight - cornerHeight + cornerOffset, blitOffset, centerWidth + cornerWidth, cornerHeight, cornerWidth, cornerHeight, textureWidth, textureHeight);
	//
	//		//top and bottom pieces
	//		if (tooltipWidth >= centerWidth) {
	//			int centerOffset = 9;
	//			GuiComponent.blit(poseStack, posX + tooltipWidth / 2 - centerWidth / 2, posY - centerOffset, blitOffset, cornerWidth, 0, centerWidth, centerHeight, textureWidth, textureHeight);
	//			GuiComponent.blit(poseStack, posX + tooltipWidth / 2 - centerWidth / 2, posY + tooltipHeight - centerHeight + centerOffset, blitOffset, cornerWidth, centerHeight, centerWidth, centerHeight, textureWidth, textureHeight);
	//		}
	//	}

}
