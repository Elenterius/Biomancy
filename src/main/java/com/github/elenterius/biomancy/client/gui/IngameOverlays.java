package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.world.item.weapon.IGun;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public final class IngameOverlays {

	public static final ResourceLocation COMMAND_ICONS = BiomancyMod.createRL("textures/gui/command_icons.png");
	public static final ResourceLocation ORNATE_CORNER_BOTTOM_RIGHT = BiomancyMod.createRL("textures/gui/ornate_corner_br.png");

	public static final IIngameOverlay WATERMARK = (gui, poseStack, partialTick, width, height) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && !minecraft.options.renderDebug) {
			gui.setupOverlayRenderState(true, false);
			gui.setBlitOffset(-90);
			Font font = Minecraft.getInstance().font;
			ModList.get().getModContainerById(BiomancyMod.MOD_ID).ifPresent(modContainer -> {
				String text = "Biomancy Dev Build";
				String version = "Version: " + MavenVersionStringHelper.artifactVersionToString(modContainer.getModInfo().getVersion());
				int textWidth = font.width(text);
				int versionWidth = font.width(version);
				int totalWidth = Math.max(textWidth, versionWidth);
				int y = 32;
				int x = width - 2;
				GuiComponent.fill(poseStack, x - totalWidth - 4, y - 4, width, y + font.lineHeight * 2 + 2 + 1, 0xAA111111);
				GuiComponent.drawString(poseStack, font, text, x - textWidth, y, 0xEEEEEE);
				GuiComponent.drawString(poseStack, font, version, x - versionWidth, y + font.lineHeight + 1, 0xDDDDDD);
			});
		}
	};

	public static final IIngameOverlay CONTROL_STAFF_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
//		Minecraft minecraft = Minecraft.getInstance();
//		if (!minecraft.options.hideGui && minecraft.options.getCameraType().isFirstPerson() && minecraft.player != null) {
//			ItemStack itemStack = minecraft.player.getMainHandItem();
//			if (itemStack.isEmpty() || !itemStack.is(ModItems.CONTROL_STAFF.get())) return;
//			IControllableMob.Command command = ModItems.CONTROL_STAFF.get().getCommand(itemStack);
//
//			gui.setupOverlayRenderState(true, false, COMMAND_ICONS);
//			gui.setBlitOffset(-90);
//			renderCommandOverlay(poseStack, screenWidth, screenHeight, command);
//		}
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
//		OverlayRegistry.registerOverlayTop("Biomancy ControlStaff", CONTROL_STAFF_OVERLAY);
		OverlayRegistry.registerOverlayTop("Biomancy Gun", GUN_OVERLAY);
		OverlayRegistry.registerOverlayTop("Biomancy Alpha Watermark", WATERMARK);
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

		if (GuiUtil.isFirstPersonView()) {
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
			GuiRenderUtil.drawSquareProgressBar(poseStack, screenWidth / 2, screenHeight / 2, gui.getBlitOffset(), 10, reloadProgress);
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
		Minecraft.getInstance().getItemRenderer().renderGuiItem(gun.getAmmoIcon(stack), screenWidth - 16 - 4, screenHeight - 28 - 8);
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

	public static void renderAttackIndicator(ForgeIngameGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, long elapsedTime, int shootDelay) {
		if (elapsedTime < shootDelay && GuiUtil.canDrawAttackIndicator(player)) {
			float progress = (float) elapsedTime / shootDelay;
			if (progress < 1f) {
				int x = screenWidth / 2 - 8;
				int y = screenHeight / 2 - 7 + 16;
				GuiRenderUtil.drawAttackIndicator(gui, poseStack, x, y, progress);
			}
		}
	}

}
