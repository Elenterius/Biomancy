package com.github.elenterius.biomancy.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 * disables the drawing of the background but keeps the paddings/margins intact
 */
public class CustomEditBox extends EditBox {

	private Component hint = TextComponent.EMPTY;
	private final Font font;
	private boolean isBackgroundDisabled = false;

	public CustomEditBox(Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
		setBordered(true); //redundant, only for sanity
		this.font = font;
	}

	public void setTextHint(Component hint) {
		this.hint = hint;
	}

	@Override
	public void setBordered(boolean enableBackgroundDrawing) {
		// do nothing, as the **bordered** boolean field should always be true
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (visible) {
			if (!isFocused() && getValue().isEmpty()) {
				drawString(poseStack, font, hint, x + 4, y + (height - 8) / 2, -1);
			} else super.render(poseStack, mouseX, mouseY, partialTick);
		}
	}

	@Override
	public int getInnerWidth() {
		return width - 8;
	}

	@Override
	protected boolean isBordered() {
		return !isBackgroundDisabled;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		isBackgroundDisabled = true;
		super.renderButton(poseStack, mouseX, mouseY, partialTick);
		isBackgroundDisabled = false;
	}

}
