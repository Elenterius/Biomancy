package com.github.elenterius.biomancy.client.renderer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.ToothProjectileEntity;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.InjectionDeviceItem;
import com.github.elenterius.biomancy.item.ItemStorageBagItem;
import com.github.elenterius.biomancy.item.weapon.shootable.BoomlingHiveGunItem;
import com.github.elenterius.biomancy.item.weapon.shootable.ProjectileWeaponItem;
import com.github.elenterius.biomancy.item.weapon.shootable.SinewBowItem;
import com.github.elenterius.biomancy.reagent.Reagent;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class HudRenderUtil {

	private static final ResourceLocation HUD_0_TEXTURE = BiomancyMod.createRL("textures/gui/hud_0.png");
	private static final ResourceLocation HUD_FLUID_0_TEXTURE = BiomancyMod.createRL("textures/gui/hud_fluid_0.png");
	private static final ResourceLocation ITEM_BAG_INDICATOR_TEX = BiomancyMod.createRL("textures/gui/item_bag_indicator.png");
	private static final Lazy<ItemStack> REAGENT_STACK = Lazy.of(() -> new ItemStack(ModItems.REAGENT.get()));
	private static final Lazy<ItemStack> WITHER_SKULL_STACK = Lazy.of(() -> new ItemStack(Items.WITHER_SKELETON_SKULL));

	private HudRenderUtil() {}

	public static boolean canDrawAttackIndicator(Minecraft mc, ClientPlayerEntity player) {
		if (mc.gameSettings.attackIndicator != AttackIndicatorStatus.CROSSHAIR) return true;
		boolean isVisible = false;
		float attackStrength = player.getCooledAttackStrength(0f);
		if (mc.pointedEntity instanceof LivingEntity && attackStrength >= 1f) {
			isVisible = player.getCooldownPeriod() > 5f && mc.pointedEntity.isAlive();
		}
		return !isVisible && attackStrength >= 1f;
	}

	public static void drawAttackIndicator(MatrixStack matrix, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, long elapsedTime, int shootDelay) {
		if (elapsedTime < shootDelay && canDrawAttackIndicator(mc, player)) {
			float progress = (float) elapsedTime / shootDelay;
			if (progress < 1f) {
				mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

				int x = scaledWidth / 2 - 8;
				int y = scaledHeight / 2 - 7 + 16;
				mc.ingameGUI.blit(matrix, x, y, 36, 94, 16, 4);
				mc.ingameGUI.blit(matrix, x, y, 52, 94, (int) (progress * 17f), 4);

				RenderSystem.defaultBlendFunc();
			}
		}
	}

	static void drawRectangularProgressIndicator(MatrixStack matrixStack, float cx, float cy, float lengthA, float progress, int color) {
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float alpha = (color >> 24 & 255) / 255f;
		float red = (color >> 16 & 255) / 255f;
		float green = (color >> 8 & 255) / 255f;
		float blue = (color & 255) / 255f;

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();

		RenderSystem.enableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		RenderSystem.lineWidth(4f);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

		float totalLength = lengthA * 4f;
		float halfLength = lengthA * 0.5f;
		float currentLength = progress * totalLength;

		// top right line
		float dist = Math.min(currentLength, halfLength);
		if (dist > 0) {
			float y = cy - halfLength;
			bufferbuilder.pos(matrix, cx, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(matrix, cx + halfLength, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// right line
		dist = Math.min(currentLength - halfLength, lengthA);
		if (dist > 0) {
			float x = cx + halfLength;
			float y = cy - halfLength;
			bufferbuilder.pos(matrix, x, y + dist, 0f).color(red, green, blue, alpha).endVertex();
		}

		// bottom line
		dist = Math.min(currentLength - 3f * halfLength, lengthA);
		if (dist > 0) {
			float x = cx + halfLength;
			float y = cy + halfLength;
			bufferbuilder.pos(matrix, x - dist, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// left line
		dist = Math.min(currentLength - 5f * halfLength, lengthA);
		if (dist > 0) {
			float x = cx - halfLength;
			float y = cy + halfLength;
			bufferbuilder.pos(matrix, x, y - dist, 0f).color(red, green, blue, alpha).endVertex();
		}

		// top left line
		dist = Math.min(currentLength - 7f * halfLength, halfLength);
		if (dist > 0) {
			float x = cx - halfLength;
			float y = cy - halfLength;
			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(matrix, x + halfLength, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		bufferbuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.defaultBlendFunc();
	}

	static void drawCircularProgressIndicator(MatrixStack matrixStack, float cx, float cy, float radius, float progress, int color) {
		drawArc(matrixStack, cx, cy, radius, 0f, progress * (float) Math.PI * 2f, color);
	}

	static void drawArc(MatrixStack matrixStack, float cx, float cy, float radius, float startAngle, float endAngle, int color) {
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float alpha = (color >> 24 & 255) / 255f;
		float red = (color >> 16 & 255) / 255f;
		float green = (color >> 8 & 255) / 255f;
		float blue = (color & 255) / 255f;

		float angleOffset = (float) (Math.PI * 0.5f);
		startAngle -= angleOffset;
		endAngle -= angleOffset;

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.enableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.lineWidth(5.1f);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

		float step = 0.1f;
		for (float theta = startAngle; theta < endAngle; theta += step) {
			float x = radius * MathHelper.cos(theta) + cx;
			float y = radius * MathHelper.sin(theta) + cy;
			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
		}
		float x = radius * MathHelper.cos(endAngle) + cx;
		float y = radius * MathHelper.sin(endAngle) + cy;
		bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();

		bufferbuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.defaultBlendFunc();
	}

	static void drawItemStorageBagOverlay(MatrixStack matrixStack, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, ItemStorageBagItem item) {
		ItemStorageBagItem.Mode mode = item.getMode(stack);
		ItemStack storedStack = item.getStoredItem(stack);

		mc.getTextureManager().bindTexture(HUD_0_TEXTURE);
		AbstractGui.blit(matrixStack, scaledWidth - 44, scaledHeight - 28, 0, 0, 44, 28, 44, 28);
		mc.getItemRenderer().renderItemAndEffectIntoGUI(player, storedStack, scaledWidth - 16 - 4, scaledHeight - 16 - 4);
		mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, storedStack, scaledWidth - 16 - 4, scaledHeight - 16 - 4, null);
		mc.getTextureManager().bindTexture(ITEM_BAG_INDICATOR_TEX);
		AbstractGui.blit(matrixStack, scaledWidth - 16 - 4, scaledHeight - 28 - 8, 16, mode.id * 16f, 16, 16, 32, 48);

		if (!canDrawAttackIndicator(mc, player)) return;
		if (storedStack.isEmpty() || (mode == ItemStorageBagItem.Mode.DEVOUR && item.getFullness(stack) >= 1f)) return;

		if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) mc.objectMouseOver;
			TileEntity tile = player.world.getTileEntity(rayTraceResult.getPos());
			if (tile != null) {
				LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (capability.isPresent()) {
//					mc.getTextureManager().bindTexture(ITEM_BAG_INDICATOR_TEX); //texture is already bound
					int x = scaledWidth / 2 - 16 - 8;
					int y = scaledHeight / 2 + 9;
					AbstractGui.blit(matrixStack, x, y, 0, mode.id * 16f, 32, 16, 32, 48);
					mc.getItemRenderer().renderItemAndEffectIntoGUI(player, storedStack, x + 32, y);
				}
			}
		}
	}

	static void drawBowOverlay(MatrixStack matrixStack, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, SinewBowItem item) {
		int timeLeft = player.getItemInUseCount();
		if (timeLeft == 0) timeLeft = stack.getUseDuration();
		int charge = stack.getUseDuration() - timeLeft;
		float pullProgress = Math.min(charge / item.drawTime, 1f);
		float velocity = item.getArrowVelocity(stack, charge) * item.baseVelocity;
		float x = scaledWidth * 0.5f;
		float y = scaledHeight * 0.5f;
		AbstractGui.drawString(matrixStack, mc.fontRenderer, String.format("V: %.1f", velocity), (int) x + 18, (int) y + 6, 0xFFFEFEFE);
		drawRectangularProgressIndicator(matrixStack, x, y, 25f, pullProgress, 0xFFFEFEFE);
	}

	static void drawInjectionDeviceOverlay(MatrixStack matrix, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, InjectionDeviceItem item) {
		byte amount = item.getReagentAmount(stack);
		if (amount < 1) return;

		Reagent reagent = item.getReagent(stack);
		if (reagent != null) {
			FontRenderer fontRenderer = mc.fontRenderer;
			String text = amount + "x";

			int x = scaledWidth - 16 - 4;
			int y = scaledHeight - 16 - 4;
			matrix.push();
			float scale = 1.5f; //make font bigger
			matrix.translate(x - fontRenderer.getStringWidth(text) * scale, y + 16 - fontRenderer.FONT_HEIGHT * scale, 0);
			matrix.scale(scale, scale, 0);
			AbstractGui.drawString(matrix, fontRenderer, text, 0, 0, 0xFFFEFEFE);
			matrix.pop();

			ItemStack reagentStack = REAGENT_STACK.get();
			Reagent.serialize(reagent, reagentStack.getOrCreateTag());
			mc.getItemRenderer().renderItemIntoGUI(reagentStack, x, y);
		}
	}

	static void drawGunOverlay(MatrixStack matrix, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, ProjectileWeaponItem item) {
		FontRenderer fontRenderer = mc.fontRenderer;

		mc.getTextureManager().bindTexture(HUD_0_TEXTURE);
		AbstractGui.blit(matrix, scaledWidth - 44, scaledHeight - 28, 0, 0, 44, 28, 44, 28);

		if (item == ModItems.TOOTH_GUN.get()) {
			mc.getItemRenderer().renderItemIntoGUI(ToothProjectileEntity.getItemForRendering(), scaledWidth - 16 - 4, scaledHeight - 28 - 8);
		}
		else if (item == ModItems.WITHERSHOT.get()) {
			mc.getItemRenderer().renderItemIntoGUI(WITHER_SKULL_STACK.get(), scaledWidth - 16 - 4, scaledHeight - 28 - 8);
		}
		else if (item == ModItems.BOOMLING_HIVE_GUN.get()) {
			drawBoomlingGunOverlay(matrix, scaledWidth, scaledHeight, mc, stack, fontRenderer);
		}

		drawAmmoCount(matrix, fontRenderer, scaledWidth, scaledHeight, item.getMaxAmmo(stack), item.getAmmo(stack), 0xFFFEFEFE, 0xFF9E9E9E);

		GameSettings gamesettings = mc.gameSettings;
		if (gamesettings.getPointOfView().func_243192_a() && !gamesettings.showDebugInfo) { // is in first person view
			ProjectileWeaponItem.State gunState = item.getState(stack);
			if (gunState == ProjectileWeaponItem.State.RELOADING) {
				long elapsedTime = player.worldClient.getGameTime() - item.getReloadStartTime(stack);
				float reloadProgress = item.getReloadProgress(elapsedTime, item.getReloadTime(stack));
				drawRectangularProgressIndicator(matrix, scaledWidth * 0.5f, scaledHeight * 0.5f, 20f, reloadProgress, 0xFFFFFFFF);
			}
			else {
				long elapsedTime = player.worldClient.getGameTime() - item.getShootTimestamp(stack);
				drawAttackIndicator(matrix, scaledWidth, scaledHeight, mc, player, elapsedTime, item.getShootDelay(stack));
			}
		}
	}

	private static void drawBoomlingGunOverlay(MatrixStack matrix, int scaledWidth, int scaledHeight, Minecraft mc, ItemStack stack, FontRenderer fontRenderer) {
		int x = scaledWidth - 23 - 1;
		int y = scaledHeight - 28 - 16 - 32 + 4;
		int count = ModItems.BOOMLING_HIVE_GUN.get().getPotionCount(stack);
		if (count > 0) {
			int color = ModItems.BOOMLING_HIVE_GUN.get().getPotionColor(stack);
			int offset = (int) ((25 - 8) * (1f - count / (float) BoomlingHiveGunItem.MAX_POTION_COUNT));
			if (offset == 25 - 8) offset--;
			AbstractGui.fill(matrix, x + 10, y + 8 + offset, x + 16, y + 25, color | 0xFF000000);

			ArrayList<ITextComponent> lines = new ArrayList<>();
			if (Screen.hasAltDown()) {
				PotionUtilExt.addPotionTooltip(stack, lines, 1f);
			}
			else {
				lines.add(new StringTextComponent("[").appendSibling(ClientTextUtil.getAltKey()).appendString("]").mergeStyle(TextFormatting.AQUA));
			}
			int hOffset = lines.size() * fontRenderer.FONT_HEIGHT;
			for (int i = 0; i < lines.size(); i++) {
				ITextComponent text = lines.get(i);
				AbstractGui.drawString(matrix, fontRenderer, text, x - fontRenderer.getStringPropertyWidth(text), y + 32 - hOffset + i * fontRenderer.FONT_HEIGHT, 0xFF9E9E9E);
			}
		}
		mc.getTextureManager().bindTexture(HUD_FLUID_0_TEXTURE);
		AbstractGui.blit(matrix, x, y, 0, 0, 23, 32, 23, 32);
		mc.getItemRenderer().renderItemIntoGUI(new ItemStack(ModItems.BOOMLING.get()), scaledWidth - 16 - 4, scaledHeight - 28 - 8);
		if (count > 0) {
			String countStr = String.valueOf(count);
			x = scaledWidth - fontRenderer.getStringWidth(countStr) - 2;
			y = y + 32 - fontRenderer.FONT_HEIGHT + 1;
			AbstractGui.drawString(matrix, fontRenderer, countStr, x, y, 0xFFFEFEFE);
		}
	}

	private static void drawAmmoCount(MatrixStack matrix, FontRenderer fontRenderer, int scaledWidth, int scaledHeight, int maxAmmoIn, int ammoIn, int primaryColor, int secondaryColor) {
		String maxAmmo = "/" + maxAmmoIn;
		String ammo = "" + ammoIn;
		int x = scaledWidth - fontRenderer.getStringWidth(maxAmmo) - 4;
		int y = scaledHeight - fontRenderer.FONT_HEIGHT - 4;
		AbstractGui.drawString(matrix, fontRenderer, maxAmmo, x, y, secondaryColor);
		matrix.push();
		float scale = 1.5f; //make font bigger
		matrix.translate(x - fontRenderer.getStringWidth(ammo) * scale, y - fontRenderer.FONT_HEIGHT * scale * 0.5f, 0);
		matrix.scale(scale, scale, 0);
		AbstractGui.drawString(matrix, fontRenderer, ammo, 0, 0, primaryColor);
		matrix.pop();
	}

}
