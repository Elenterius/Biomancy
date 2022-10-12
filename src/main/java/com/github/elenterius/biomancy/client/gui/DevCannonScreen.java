package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.world.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import software.bernie.geckolib3.core.util.Color;

public class DevCannonScreen extends Screen {

	public static final int CANCEL_ID = -1;
	static final float DIAGONAL_OF_ITEM = Mth.SQRT_OF_TWO * 32; // 16 * 2
	static final int DURATION = 10;
	private ItemStack cachedBarrierStack = ItemStack.EMPTY;
	private int ticks;
	private InteractionHand itemHoldingHand;

	public DevCannonScreen(InteractionHand hand) {
		super(new TranslatableComponent("biomancy.dev.wheel_menu"));
		itemHoldingHand = hand;
	}

	@Override
	protected void init() {
		ticks = 0;
		cachedBarrierStack = new ItemStack(Items.BARRIER);

		//move mouse up, to make cancel action selected by default
		double x = minecraft.getWindow().getScreenWidth() / 2d;
		double y = minecraft.getWindow().getScreenHeight() / 2d - 16;
		InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(), 212993, x, y);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		cachedBarrierStack = ItemStack.EMPTY;
		super.onClose();
	}

	@Override
	public void tick() {
		if (ticks < 0 || minecraft == null || minecraft.player == null) {
			onClose();
			return;
		}

		ItemStack stack = minecraft.player.getItemInHand(itemHoldingHand);
		if (stack.isEmpty() || !(stack.getItem() instanceof DevArmCannonItem)) {
			onClose();
			return;
		}

		//		if (Screen.hasControlDown()) {
		//			ticks++;
		//		}
		//		else {
		//			ticks -= 2;
		//		}
		ticks++;

		if (ticks > DURATION) ticks = DURATION;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (ModProjectiles.PRECONFIGURED_PROJECTILES.isEmpty()) {
			onClose();
			return false;
		}

		if (ticks < DURATION - 1) return false;

		final int segments = ModProjectiles.PRECONFIGURED_PROJECTILES.size() + 1;
		float angleIncrement = Mth.TWO_PI / segments;
		float x = width / 2f;
		float y = height / 2f;
		float upperBound = segments * angleIncrement - Mth.HALF_PI + angleIncrement / 2f;
		float lowerBound = -Mth.HALF_PI - angleIncrement / 2f;
		float mouseAngle = (float) Mth.atan2(mouseY - y, mouseX - x); //cartesian to polar
		if (mouseAngle > upperBound) mouseAngle -= Mth.TWO_PI;
		if (mouseAngle < lowerBound) mouseAngle += Mth.TWO_PI;


		for (int idx = 0; idx < segments; idx++) {
			float currentAngle = idx * angleIncrement - Mth.HALF_PI;
			boolean isMouseInSection = mouseAngle >= currentAngle - angleIncrement / 2f && mouseAngle < currentAngle + angleIncrement / 2f;
			if (isMouseInSection) {
				if (idx > 0) ModNetworkHandler.sendKeyBindPressToServer(itemHoldingHand, (byte) (idx - 1));
				break;
			}
		}

		onClose();
		return false;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (ticks < 0) return;
		float time = ticks + partialTick;
		if (time > DURATION) {
			time = DURATION;
		}
		renderWheel(poseStack, mouseX, mouseY, time / DURATION);
	}

	private void renderWheel(PoseStack poseStack, int mouseX, int mouseY, float pct) {
		if (ModProjectiles.PRECONFIGURED_PROJECTILES.isEmpty()) return;

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		final int segments = ModProjectiles.PRECONFIGURED_PROJECTILES.size() + 1;
		float angleIncrement = Mth.TWO_PI / segments;
		float baseRadius = DIAGONAL_OF_ITEM / angleIncrement;
		float x = width / 2f;
		float y = height / 2f;

		int radius = Mth.floor(baseRadius * pct);

		float upperBound = segments * angleIncrement - Mth.HALF_PI + angleIncrement / 2f;
		float lowerBound = -Mth.HALF_PI - angleIncrement / 2f;
		float mouseAngle = (float) Mth.atan2(mouseY - y, mouseX - x); //cartesian to polar
		if (mouseAngle > upperBound) mouseAngle -= Mth.TWO_PI;
		if (mouseAngle < lowerBound) mouseAngle += Mth.TWO_PI;

		ModProjectiles.ConfiguredProjectile<? extends BaseProjectile> configuredProjectile = null;
		float textAngle = 0;

		for (int idx = 0; idx < segments; idx++) {
			float currentAngle = idx * angleIncrement - Mth.HALF_PI;

			boolean isMouseInSection = radius > baseRadius - 1 && mouseAngle >= currentAngle - angleIncrement / 2f && mouseAngle < currentAngle + angleIncrement / 2f;

			int color = isMouseInSection ? ColorStyles.GENERIC_TOOLTIP.borderStartColor() & 0xFAFFFFFF : ColorStyles.GENERIC_TOOLTIP.backgroundColor() & 0xE0FFFFFF; //decrease alpha

			drawSegment(poseStack, x, y, radius, currentAngle - angleIncrement / 2f, currentAngle, color, getBlitOffset());
			drawSegment(poseStack, x, y, radius, currentAngle, currentAngle + angleIncrement / 2f, color, getBlitOffset());

			float v = x + radius * Mth.cos(currentAngle); //polar to cartesian
			float w = y + radius * Mth.sin(currentAngle);

			if (idx == 0) itemRenderer.renderAndDecorateFakeItem(cachedBarrierStack, Mth.floor(v - 8), Mth.floor(w - 8));
			else {
				int argb = Color.HSBtoRGB(idx / (float) segments, 0.75f, 0.5f);
				GuiRenderUtil.fill(poseStack, v - 8, w - 8, v + 8, w + 8, getBlitOffset(), argb);
			}

			if (isMouseInSection) {
				if (idx > 0) configuredProjectile = ModProjectiles.PRECONFIGURED_PROJECTILES.get(idx - 1);
				textAngle = currentAngle;
			}
		}

		if (radius <= baseRadius - 1) return;

		//draw text for selected section
		MutableComponent text = configuredProjectile == null ? new TextComponent("Cancel") : new TextComponent(configuredProjectile.name());

		int textRadius = radius + 16 + 8 + 2;
		float xt = x + textRadius * Mth.cos(textAngle);
		float yt = y + textRadius * Mth.sin(textAngle);
		int lineWidth = font.width(text);

		float offsetAngle = textAngle + Mth.HALF_PI;
		if (offsetAngle == 0f || offsetAngle == Mth.PI) { //we are in the middle of the screen
			xt -= lineWidth / 2f;
		}

		if (offsetAngle > Mth.PI) { //we are on the left screen side
			xt -= lineWidth;
		}

		poseStack.pushPose();
		float minX = xt - 3;
		float minY = yt - font.lineHeight / 2f - 3;
		float maxX = xt + lineWidth + 2;
		float maxY = yt + font.lineHeight / 2f + 2;
		GuiRenderUtil.fill(poseStack, minX, minY, maxX, maxY, getBlitOffset(), ColorStyles.GENERIC_TOOLTIP.backgroundColor() & 0xE0FFFFFF);
		font.drawShadow(poseStack, text, xt, yt - font.lineHeight / 2f, ColorStyles.WHITE_ARGB);
		poseStack.popPose();
	}

	public void drawSegment(PoseStack poseStack, float x, float y, float radius, float startAngle, float endAngle, int argbColor, int blitOffset) {
		Matrix4f matrix4f = poseStack.last().pose();

		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

		float innerRadius = Math.max(radius - 16, 0);
		bufferBuilder.vertex(matrix4f, x + innerRadius * Mth.cos(startAngle), y + innerRadius * Mth.sin(startAngle), blitOffset).color(argbColor).endVertex();
		bufferBuilder.vertex(matrix4f, x + innerRadius * Mth.cos(endAngle), y + innerRadius * Mth.sin(endAngle), blitOffset).color(argbColor).endVertex();

		float outerRadius = radius + 16;
		bufferBuilder.vertex(matrix4f, x + outerRadius * Mth.cos(endAngle), y + outerRadius * Mth.sin(endAngle), blitOffset).color(argbColor).endVertex();
		bufferBuilder.vertex(matrix4f, x + outerRadius * Mth.cos(startAngle), y + outerRadius * Mth.sin(startAngle), blitOffset).color(argbColor).endVertex();

		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}
