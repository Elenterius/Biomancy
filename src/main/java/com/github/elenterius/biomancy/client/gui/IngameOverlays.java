package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.weapon.IGun;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class IngameOverlays {

	public static final ResourceLocation COMMAND_ICONS = BiomancyMod.createRL("textures/gui/command_icons.png");
	public static final ResourceLocation INJECTOR_COOL_DOWN = BiomancyMod.createRL("textures/gui/indicator_injector_cooldown.png");
	public static final ResourceLocation ORNATE_CORNER_BOTTOM_RIGHT = BiomancyMod.createRL("textures/gui/ornate_corner_br.png");

	//	public static final IIngameOverlay CONTROL_STAFF_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
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
	//	};

	public static final IGuiOverlay GUN_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof IGun gun)) return;

			gui.setupOverlayRenderState(true, false);
			gui.setBlitOffset(-90);
			renderGunOverlay(gui, poseStack, screenWidth, screenHeight, minecraft.player, itemStack, gun);
		}
	};

	public static final IGuiOverlay INJECTOR_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof InjectorItem injector)) return;

			gui.setupOverlayRenderState(true, false);
			gui.setBlitOffset(-90);
			renderInjectorOverlay(gui, poseStack, partialTicks, screenWidth, screenHeight, minecraft.player, itemStack, injector);
		}
	};

	private IngameOverlays() {}



	static void renderCommandOverlay(PoseStack poseStack, int screenWidth, int screenHeight, IControllableMob.Command command) {
		//		if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK) {
		int x = screenWidth / 2 + 16;
		int y = screenHeight / 2 - 16;
		GuiComponent.blit(poseStack, x, y, command.serialize() * 32f, 0, 32, 32, 160, 32);
		GuiComponent.drawString(poseStack, Minecraft.getInstance().font, command.name(), x, y + 16 + 18, 0x55ffff);
	}

	static void renderGunOverlay(ForgeGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, ItemStack stack, IGun gun) {
		renderAmmoOverlay(poseStack, screenWidth, screenHeight, stack, gun);

		if (GuiUtil.isFirstPersonView()) {
			renderReloadIndicator(gui, poseStack, screenWidth, screenHeight, player, stack, gun);
		}
	}

	static void renderInjectorOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight, LocalPlayer player, ItemStack stack, InjectorItem injector) {
		if (GuiUtil.isFirstPersonView()) {
			float progress = 1f - player.getCooldowns().getCooldownPercent(injector, partialTicks);
			if (progress < 1f) {
				int x = screenWidth / 2 - 8;
				int y = screenHeight / 2 - 7 - 8;
				RenderSystem.setShaderTexture(0, INJECTOR_COOL_DOWN);
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GuiComponent.blit(poseStack, x, y, gui.getBlitOffset(), 0, 0, 16, 7, 16, 16);
				GuiComponent.blit(poseStack, x, y, gui.getBlitOffset(), 0, 7, (int) (progress * 16f), 7, 16, 16);
			}
		}
	}

	static void renderOrnateCorner(PoseStack poseStack, int x, int y) {
		RenderSystem.setShaderTexture(0, ORNATE_CORNER_BOTTOM_RIGHT);
		GuiComponent.blit(poseStack, x, y, 0, 0, 44, 28, 44, 28);
	}

	static void renderReloadIndicator(ForgeGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, ItemStack stack, IGun gun) {
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

	public static void renderAttackIndicator(ForgeGui gui, PoseStack poseStack, int screenWidth, int screenHeight, LocalPlayer player, long elapsedTime, int shootDelay) {
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
