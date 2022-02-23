package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.world.item.weapon.IGun;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public final class IngameOverlays {

	public static final ResourceLocation COMMAND_ICONS = BiomancyMod.createRL("textures/gui/command_icons.png");
	public static final ResourceLocation ORNATE_CORNER_BOTTOM_RIGHT = BiomancyMod.createRL("textures/gui/ornate_corner_br.png");

	public static final IIngameOverlay CONTROL_STAFF_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.options.getCameraType().isFirstPerson() && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !itemStack.is(ModItems.CONTROL_STAFF.get())) return;
			IControllableMob.Command command = ModItems.CONTROL_STAFF.get().getCommand(itemStack);

			gui.setupOverlayRenderState(true, false, COMMAND_ICONS);
			gui.setBlitOffset(-90);
			renderCommandOverlay(poseStack, screenWidth, screenHeight, command);
		}
	};

	public static final IIngameOverlay GUN_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof IGun gun)) return;

			gui.setupOverlayRenderState(true, false);
			gui.setBlitOffset(-90);
			renderGunOverlay(gui, poseStack, screenWidth, screenHeight, minecraft.player, itemStack, gun);
		}
	};

	private IngameOverlays() {}

	public static void registerGameOverlays() {
		OverlayRegistry.registerOverlayTop("Biomancy ControlStaff", CONTROL_STAFF_OVERLAY);
		OverlayRegistry.registerOverlayTop("Biomancy Gun", GUN_OVERLAY);
	}

	static void renderCommandOverlay(PoseStack poseStack, int screenWidth, int screenHeight, IControllableMob.Command command) {
//		if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK) {
		int x = screenWidth / 2 + 16;
		int y = screenHeight / 2 - 16;
		GuiComponent.blit(poseStack, x, y, command.serialize() * 32f, 0, 32, 32, 160, 32);
		GuiComponent.drawString(poseStack, Minecraft.getInstance().font, command.name(), x, y + 16 + 18, 0x55ffff);
	}

	static void renderGunOverlay(ForgeIngameGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, ItemStack stack, IGun gun) {
		renderAmmoOverlay(poseStack, screenWidth, screenHeight, stack, gun);

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.getCameraType().isFirstPerson()) {
			renderReloadIndicator(gui, poseStack, screenWidth, screenHeight, player, stack, gun);
		}
	}

	static void renderOrnateCorner(PoseStack poseStack, int x, int y) {
		RenderSystem.setShaderTexture(0, ORNATE_CORNER_BOTTOM_RIGHT);
		GuiComponent.blit(poseStack, x, y, 0, 0, 44, 28, 44, 28);
	}

	static void renderReloadIndicator(ForgeIngameGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, ItemStack stack, IGun gun) {
		IGun.State gunState = gun.getState(stack);
		if (gunState == IGun.State.RELOADING) {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getReloadStartTime(stack);
			float reloadProgress = gun.getReloadProgress(elapsedTime, gun.getReloadTime(stack));
			renderProgressIndicator(poseStack, screenWidth * 0.5f, screenHeight * 0.5f, 20f, reloadProgress, 0xFFFFFFFF);
		}
		else {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getShootTimestamp(stack);
			renderAttackIndicator(gui, poseStack, screenWidth, screenHeight, player, elapsedTime, gun.getShootDelay(stack));
		}
	}

	static void renderAmmoOverlay(PoseStack poseStack, int screenWidth, int screenHeight, ItemStack stack, IGun gun) {
		int maxAmmo = gun.getMaxAmmo(stack);
		int ammo = gun.getAmmo(stack);
		renderOrnateCorner(poseStack, screenWidth - 44, screenHeight - 28);
		Minecraft.getInstance().getItemRenderer().renderGuiItem(gun.getAmmoItemForOverlayRender(stack), screenWidth - 16 - 4, screenHeight - 28 - 8);
		renderAmmoCount(poseStack, Minecraft.getInstance().font, screenWidth, screenHeight, maxAmmo, ammo, 0xFFFEFEFE, 0xFF9E9E9E);
	}

	static void renderAmmoCount(PoseStack poseStack, Font font, int screenWidth, int screenHeight, int maxAmmoIn, int ammoIn, int primaryColor, int secondaryColor) {
		String maxAmmo = "/" + maxAmmoIn;
		String ammo = "" + ammoIn;
		int x = screenWidth - font.width(maxAmmo) - 4;
		int y = screenHeight - font.lineHeight - 4;
		GuiComponent.drawString(poseStack, font, maxAmmo, x, y, secondaryColor);
		poseStack.pushPose();
		float scale = 1.5f; //make font bigger
		poseStack.translate(x - font.width(ammo) * scale, y - font.lineHeight * scale * 0.5f, 0);
		poseStack.scale(scale, scale, 0);
		GuiComponent.drawString(poseStack, font, ammo, 0, 0, primaryColor);
		poseStack.popPose();
	}

	public static boolean canDrawAttackIndicator(LocalPlayer player) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.attackIndicator != AttackIndicatorStatus.CROSSHAIR) return true;
		boolean isVisible = false;
		float attackStrength = player.getAttackStrengthScale(0f);
		if (minecraft.crosshairPickEntity instanceof LivingEntity && attackStrength >= 1f) {
			isVisible = player.getCurrentItemAttackStrengthDelay() > 5f && minecraft.crosshairPickEntity.isAlive();
		}
		return !isVisible && attackStrength >= 1f;
	}

	public static void renderAttackIndicator(ForgeIngameGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, long elapsedTime, int shootDelay) {
		if (elapsedTime < shootDelay && canDrawAttackIndicator(player)) {
			float progress = (float) elapsedTime / shootDelay;
			if (progress < 1f) {
				RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				int x = screenWidth / 2 - 8;
				int y = screenHeight / 2 - 7 + 16;
				gui.blit(poseStack, x, y, 36, 94, 16, 4);
				gui.blit(poseStack, x, y, 52, 94, (int) (progress * 17f), 4);
			}
		}
	}

	static void renderProgressIndicator(PoseStack poseStack, float cx, float cy, float lengthA, float progress, int color) {
		Matrix4f matrix = poseStack.last().pose();
		float alpha = (color >> 24 & 255) / 255f;
		float red = (color >> 16 & 255) / 255f;
		float green = (color >> 8 & 255) / 255f;
		float blue = (color & 255) / 255f;

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.lineWidth(4f);
		bufferbuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

		float totalLength = lengthA * 4f;
		float halfLength = lengthA * 0.5f;
		float currentLength = progress * totalLength;

		// top right line
		float dist = Math.min(currentLength, halfLength);
		if (dist > 0) {
			float y = cy - halfLength;
			bufferbuilder.vertex(matrix, cx, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.vertex(matrix, cx + halfLength, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// right line
		dist = Math.min(currentLength - halfLength, lengthA);
		if (dist > 0) {
			float x = cx + halfLength;
			float y = cy - halfLength;
			bufferbuilder.vertex(matrix, x, y + dist, 0f).color(red, green, blue, alpha).endVertex();
		}

		// bottom line
		dist = Math.min(currentLength - 3f * halfLength, lengthA);
		if (dist > 0) {
			float x = cx + halfLength;
			float y = cy + halfLength;
			bufferbuilder.vertex(matrix, x - dist, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// left line
		dist = Math.min(currentLength - 5f * halfLength, lengthA);
		if (dist > 0) {
			float x = cx - halfLength;
			float y = cy + halfLength;
			bufferbuilder.vertex(matrix, x, y - dist, 0f).color(red, green, blue, alpha).endVertex();
		}

		// top left line
		dist = Math.min(currentLength - 7f * halfLength, halfLength);
		if (dist > 0) {
			float x = cx - halfLength;
			float y = cy - halfLength;
			bufferbuilder.vertex(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.vertex(matrix, x + halfLength, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		tesselator.end();
	}

}
