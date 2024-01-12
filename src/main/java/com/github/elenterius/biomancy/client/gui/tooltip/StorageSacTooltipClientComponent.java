package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.itemhandler.EnhancedItemHandler;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StorageSacTooltipClientComponent implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/gui/tooltip_storage_sac.png");
	private static final int OFFSET = 2;
	private final @Nullable EnhancedItemHandler itemHandler;
	private final boolean isStorageSacEmpty;

	public StorageSacTooltipClientComponent(StorageSacTooltipComponent tooltip) {
		itemHandler = tooltip.getItemHandler();
		isStorageSacEmpty = itemHandler == null || itemHandler.isEmpty();
	}

	@Override
	public int getHeight() {
		return isStorageSacEmpty ? 0 : 56 + OFFSET * 2;
	}

	@Override
	public int getWidth(Font font) {
		return isStorageSacEmpty ? 0 : 92;
	}

	@Override
	public void renderImage(Font font, int posX, int posY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
		if (itemHandler == null || isStorageSacEmpty) return;

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TEXTURE);
		GuiComponent.blit(poseStack, posX, posY + OFFSET, blitOffset, 0, 0, 92, 56, 128, 64);

		boolean drawHighlight = true;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack stack = itemHandler.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			int x = posX + 2 + 18 * (i % 5);
			int y = posY + OFFSET + 2 + 18 * (i / 5);
			itemRenderer.renderAndDecorateItem(stack, x, y, i);
			itemRenderer.renderGuiItemDecorations(font, stack, x, y);
			if (drawHighlight) {
				AbstractContainerScreen.renderSlotHighlight(poseStack, x, y, blitOffset);
				drawHighlight = false;
			}
		}
	}

}
