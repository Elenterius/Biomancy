package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.ItemHandlerWrapper;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StorageSacTooltipClientComponent implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/gui/tooltip_storage_sac.png");
	private static final int OFFSET = 2;
	private final @Nullable ItemHandlerWrapper itemHandler;
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
	public void renderImage(Font font, int posX, int posY, GuiGraphics guiGraphics) {
		if (itemHandler == null || isStorageSacEmpty) return;

		guiGraphics.blit(TEXTURE, posX, posY + OFFSET, 0, 0, 92, 56, 128, 64);

		boolean drawHighlight = true;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack stack = itemHandler.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			int x = posX + 2 + 18 * (i % 5);
			int y = posY + OFFSET + 2 + 18 * (i / 5);
			guiGraphics.renderItem(stack, x, y, i);
			guiGraphics.renderItemDecorations(font, stack, x, y);
			if (drawHighlight) {
				AbstractContainerScreen.renderSlotHighlight(guiGraphics, x, y, 0);
				drawHighlight = false;
			}
		}
	}

}
