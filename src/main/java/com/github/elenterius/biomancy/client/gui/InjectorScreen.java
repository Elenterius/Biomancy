package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

public class InjectorScreen extends Screen {

	public static final ResourceLocation ICONS = BiomancyMod.createRL("textures/gui/wheel_icons.png");

	public static final int CANCEL_ID = -1;
	public static final int CLEAR_ID = -2;
	static final float DIAGONAL_OF_ITEM = Mth.SQRT_OF_TWO * 32; // 16 * 2
	static final int DURATION = 10;
	private Object2IntMap<ItemStack> cachedStacks;
	private int ticks;
	private int refreshCacheTicks;
	private final InteractionHand itemHoldingHand;

	public InjectorScreen(InteractionHand hand) {
		super(ComponentUtil.translatable("biomancy.injector.wheel_menu"));
		itemHoldingHand = hand;
	}

	@Override
	protected void init() {
		ticks = 0;
		refreshCacheTicks = 0;
		cachedStacks = null;

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
		cachedStacks = null;
		super.onClose();
	}

	@Override
	public void tick() {
		if (ticks < 0 || minecraft == null || minecraft.player == null) {
			onClose();
			return;
		}

		ItemStack stack = minecraft.player.getItemInHand(itemHoldingHand);
		if (stack.isEmpty() || !(stack.getItem() instanceof InjectorItem)) {
			onClose();
			return;
		}

		if (++refreshCacheTicks % 8 == 0 || cachedStacks == null || cachedStacks.isEmpty()) {
			cachedStacks = findSerumStacks(minecraft.player);
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
		if (cachedStacks == null || cachedStacks.isEmpty()) {
			onClose();
			return false;
		}

		if (ticks < DURATION - 1) return false;

		ObjectSet<Object2IntMap.Entry<ItemStack>> stackEntries = cachedStacks.object2IntEntrySet();
		int segments = stackEntries.size();
		float angleIncrement = Mth.TWO_PI / segments;
		float x = width / 2f;
		float y = height / 2f;
		float upperBound = segments * angleIncrement - Mth.HALF_PI + angleIncrement / 2f;
		float lowerBound = -Mth.HALF_PI - angleIncrement / 2f;
		float mouseAngle = (float) Mth.atan2(mouseY - y, mouseX - x); //cartesian to polar
		if (mouseAngle > upperBound) mouseAngle -= Mth.TWO_PI;
		if (mouseAngle < lowerBound) mouseAngle += Mth.TWO_PI;

		int i = 0;
		for (Object2IntMap.Entry<ItemStack> entry : stackEntries) {
			float currentAngle = i * angleIncrement - Mth.HALF_PI;
			boolean isMouseInSection = mouseAngle >= currentAngle - angleIncrement / 2f && mouseAngle < currentAngle + angleIncrement / 2f;
			if (isMouseInSection) {
				int idx = cachedStacks.getOrDefault(entry.getKey(), CANCEL_ID);
				if (idx != CANCEL_ID) {
					ModNetworkHandler.sendKeyBindPressToServer(itemHoldingHand, (byte) idx);
				}

				break;
			}
			i++;
		}

		onClose();
		return false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (ticks < 0) return;
		float time = ticks + partialTick;
		if (time > DURATION) {
			time = DURATION;
		}
		renderWheel(guiGraphics, mouseX, mouseY, time / DURATION);
	}

	private void renderWheel(GuiGraphics guiGraphics, int mouseX, int mouseY, float pct) {
		if (cachedStacks == null || cachedStacks.isEmpty()) return;

		int blitOffset = 0;

		ObjectSet<Object2IntMap.Entry<ItemStack>> stackEntries = cachedStacks.object2IntEntrySet();

		int segments = stackEntries.size();
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

		ItemStack stack = ItemStack.EMPTY;
		float textAngle = 0;

		int i = 0;
		for (Object2IntMap.Entry<ItemStack> entry : stackEntries) {
			float currentAngle = i * angleIncrement - Mth.HALF_PI;

			boolean isMouseInSection = radius > baseRadius - 1 && mouseAngle >= currentAngle - angleIncrement / 2f && mouseAngle < currentAngle + angleIncrement / 2f;

			int color = isMouseInSection ? ColorStyles.GENERIC_TOOLTIP.borderStartColor() & 0xFA_FFFFFF : ColorStyles.GENERIC_TOOLTIP.backgroundColor() & 0xE0_FFFFFF; //decrease alpha

			drawSegment(guiGraphics, x, y, radius, currentAngle - angleIncrement / 2f, currentAngle, color, blitOffset);
			drawSegment(guiGraphics, x, y, radius, currentAngle, currentAngle + angleIncrement / 2f, color, blitOffset);

			float v = x + radius * Mth.cos(currentAngle); //polar to cartesian
			float w = y + radius * Mth.sin(currentAngle);

			ItemStack currentStack = entry.getKey();
			if (currentStack.isEmpty()) {
				guiGraphics.blit(ICONS, Mth.floor(v - 8), Mth.floor(w - 8), 0, 0, 16, 16, 32, 16);
			}
			else if (currentStack.getItem() == Items.BARRIER) {
				guiGraphics.blit(ICONS, Mth.floor(v - 8), Mth.floor(w - 8), 16, 0, 16, 16, 32, 16);
			}
			else {
				guiGraphics.renderFakeItem(currentStack, Mth.floor(v - 8), Mth.floor(w - 8));
			}

			if (isMouseInSection) {
				stack = currentStack;
				textAngle = currentAngle;
			}

			i++;
		}

		if (radius <= baseRadius - 1) return;

		//draw text for selected section
		MutableComponent text;
		if (stack.isEmpty()) {
			text = ComponentUtil.literal("Clear").withStyle(TextStyles.ERROR);
		}
		else if (stack.getItem() == Items.BARRIER) {
			text = ComponentUtil.literal("Cancel");
		}
		else {
			text = ComponentUtil.mutable().append(stack.getHoverName());
			if (stack.hasCustomHoverName()) text.withStyle(ChatFormatting.ITALIC);
		}

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

		drawItemLabel(guiGraphics, xt, yt, blitOffset, text, lineWidth);
	}

	private void drawItemLabel(GuiGraphics guiGraphics, float x, float y, int zDepth, Component text, int lineWidth) {
		guiGraphics.pose().pushPose();

		int minX = (int) x;
		int minY = (int) (y - font.lineHeight / 2f);
		int maxX = minX + lineWidth;
		int maxY = (int) (y + font.lineHeight / 2f);

		GuiRenderUtil.fill(guiGraphics, minX - 3f, minY - 3f, maxX + 2f, maxY + 1.5f, zDepth, ColorStyles.GENERIC_TOOLTIP.backgroundColor() & 0xE0_FFFFFF);
		guiGraphics.drawString(font, text, minX, minY, ColorStyles.WHITE_ARGB, true);

		guiGraphics.pose().popPose();
	}

	public void drawSegment(GuiGraphics guiGraphics, float x, float y, float radius, float startAngle, float endAngle, int argbColor, int blitOffset) {
		Matrix4f matrix4f = guiGraphics.pose().last().pose();

		RenderSystem.enableBlend();
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

		BufferUploader.drawWithShader(bufferBuilder.end());

		RenderSystem.disableBlend();
	}

	private Object2IntMap<ItemStack> findSerumStacks(LocalPlayer player) {
		Object2IntMap<ItemStack> foundStacks = new Object2IntArrayMap<>();
		Object2IntMap<Serum> foundSerums = new Object2IntArrayMap<>();

		foundStacks.put(new ItemStack(Items.BARRIER), CANCEL_ID);

		Inventory inventory = player.getInventory();
		int slots = inventory.getContainerSize();
		for (int idx = 0; idx < slots; idx++) {
			ItemStack stack = inventory.getItem(idx);
			Item item = stack.getItem();
			if (item instanceof SerumContainer vial) {
				Serum serum = vial.getSerum();
				if (!serum.isEmpty()) {
					if (!foundSerums.containsKey(serum)) {
						foundStacks.put(stack, idx);
					}
					foundSerums.mergeInt(serum, stack.getCount(), Integer::sum);
				}
			}
		}

		//		if (foundStacks.size() > 1) {
		foundStacks.put(ItemStack.EMPTY, CLEAR_ID);
		return foundStacks;
		//		}

		//not items were found
		//		return Object2IntMaps.emptyMap();
	}

}
