package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.item.AttackReachIndicator;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.weapon.Gun;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class IngameOverlays {

	//	public static final ResourceLocation COMMAND_ICONS = BiomancyMod.createRL("textures/gui/command_icons.png");
	public static final ResourceLocation INJECTOR_COOL_DOWN = BiomancyMod.createRL("textures/gui/indicator_injector_cooldown.png");
	public static final ResourceLocation ATTACK_REACH = BiomancyMod.createRL("textures/gui/indicator_attack_reach.png");
	public static final ResourceLocation ORNATE_CORNER_BOTTOM_RIGHT = BiomancyMod.createRL("textures/gui/ornate_corner_br.png");
	public static final ResourceLocation CHARGE_BAR = BiomancyMod.createRL("textures/gui/charge_bar.png");

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

	//	public static final IGuiOverlay GUN_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
	//		Minecraft minecraft = Minecraft.getInstance();
	//		if (!minecraft.options.hideGui && minecraft.player != null) {
	//			ItemStack itemStack = minecraft.player.getMainHandItem();
	//			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof IGun gun)) return;
	//
	//			gui.setupOverlayRenderState(true, false);
	//			gui.setBlitOffset(-90);
	//			renderGunOverlay(gui, poseStack, screenWidth, screenHeight, minecraft.player, itemStack, gun);
	//		}
	//	};

	public static final IGuiOverlay INJECTOR_OVERLAY = (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof InjectorItem injector)) return;

			gui.setupOverlayRenderState(true, false);
			renderInjectorOverlay(guiGraphics, gui.getFont(), partialTicks, screenWidth, screenHeight, -90, minecraft.player, itemStack, injector);
		}
	};

	public static final IGuiOverlay CHARGE_BAR_OVERLAY = (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack stack = minecraft.player.getMainHandItem();
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCharge abilityCharge)) return;

			if (GuiUtil.isFirstPersonView()) {
				gui.setupOverlayRenderState(true, false);
				renderChargeBar(guiGraphics, gui.getFont(), screenWidth, screenHeight, -90, abilityCharge.getCharge(stack), abilityCharge.getChargePct(stack));
			}
		}
	};

	public static final IGuiOverlay ATTACK_REACH_OVERLAY = (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		Options options = minecraft.options;

		if (!options.hideGui) {
			gui.setupOverlayRenderState(true, false);

			if (options.getCameraType().isFirstPerson() && minecraft.player != null) {
				ItemStack stack = minecraft.player.getMainHandItem();
				if (stack.isEmpty() || !(stack.getItem() instanceof AttackReachIndicator)) return;

				if (minecraft.crosshairPickEntity instanceof LivingEntity crosshairTarget && crosshairTarget.isAlive()) {
					int x = screenWidth / 2 - 8;
					int y = screenHeight / 2 - 16 - 8;
					RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					guiGraphics.blit(ATTACK_REACH, x, y, -90, 0, 0, 16, 16, 16, 16);
					RenderSystem.defaultBlendFunc();
				}
			}
		}
	};

	private IngameOverlays() {}

	//	static void renderCommandOverlay(GuiGraphics guiGraphics, int screenWidth, int screenHeight, IControllableMob.Command command) {
	//		//		if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK) {
	//		int x = screenWidth / 2 + 16;
	//		int y = screenHeight / 2 - 16;
	//		guiGraphics.blit(COMMAND_ICONS, x, y, command.serialize() * 32f, 0, 32, 32, 160, 32);
	//		guiGraphics.drawString(Minecraft.getInstance().font, command.name(), x, y + 16 + 18, 0x55ffff);
	//	}

	static void renderGunOverlay(ForgeGui gui, GuiGraphics guiGraphics, int screenWidth, int screenHeight, int zDepth, LocalPlayer player, ItemStack stack, Gun gun) {
		renderAmmoOverlay(guiGraphics, gui.getFont(), screenWidth, screenHeight, zDepth, stack, gun);

		if (GuiUtil.isFirstPersonView()) {
			renderReloadIndicator(guiGraphics, screenWidth, screenHeight, zDepth, player, stack, gun);
		}
	}

	static void renderInjectorOverlay(GuiGraphics guiGraphics, Font font, float partialTicks, int screenWidth, int screenHeight, int zDepth, LocalPlayer player, ItemStack stack, InjectorItem injector) {
		if (GuiUtil.isFirstPersonView()) {
			float progress = 1f - player.getCooldowns().getCooldownPercent(injector, partialTicks);
			if (progress < 1f) {
				int x = screenWidth / 2 - 8;
				int y = screenHeight / 2 - 7 - 8;
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				guiGraphics.blit(INJECTOR_COOL_DOWN, x, y, zDepth, 0, 0, 16, 7, 16, 16);
				guiGraphics.blit(INJECTOR_COOL_DOWN, x, y, zDepth, 0, 7, (int) (progress * 16f), 7, 16, 16);
				RenderSystem.defaultBlendFunc();
			}

			ItemStack serumItemStack = injector.getSerumItemStack(stack);
			if (serumItemStack.getItem() instanceof SerumContainer container && !container.getSerum().isEmpty()) {
				short maxAmmo = InjectorItem.MAX_SLOT_SIZE;
				renderAmmoCount(guiGraphics, font, screenWidth, screenHeight, zDepth, maxAmmo, serumItemStack.getCount(), 0xFFFEFEFE, 0xFF9E9E9E);
			}
		}
	}

	static void renderOrnateCorner(GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.blit(ORNATE_CORNER_BOTTOM_RIGHT, x, y, 0, 0, 44, 28, 44, 28);
	}

	static void renderChargeBar(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, int zDepth, int charge, float chargePct) {
		int x = screenWidth / 2 - 26 + screenWidth % 2;
		int y = screenHeight / 2 + 8 + 4;

		guiGraphics.blit(CHARGE_BAR, x, y, zDepth, 6, 6, 51, 5, 64, 16); //background
		guiGraphics.blit(CHARGE_BAR, x, y, zDepth, 6, 11, (int) (chargePct * 51), 5, 64, 16); //foreground

		if (Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity crosshairTarget && crosshairTarget.isAlive()) {
			guiGraphics.blit(CHARGE_BAR, x, y - 5, zDepth, 6, 0, 51, 6, 64, 16); //ornament
		}

		if (charge <= 0) return;

		//		String number = String.valueOf(charge);
		//		int pX = x + 26 - font.width(number) / 2;
		//		int pY = y - 5 - 4;
		//
		//		guiGraphics.drawString(font, number, pX + 1, pY, 0);
		//		guiGraphics.drawString(font, number, pX - 1, pY, 0);
		//		guiGraphics.drawString(font, number, pX, pY + 1, 0);
		//		guiGraphics.drawString(font, number, pX, pY - 1, 0);
		//		guiGraphics.drawString(font, number, pX, pY, 0xac0404);
	}

	static void renderReloadIndicator(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int zDepth, LocalPlayer player, ItemStack stack, Gun gun) {
		Gun.State gunState = gun.getState(stack);
		if (gunState == Gun.State.RELOADING) {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getReloadStartTime(stack);
			float reloadProgress = gun.getReloadProgress(elapsedTime, gun.getReloadTime(stack));
			GuiRenderUtil.drawSquareProgressBar(guiGraphics, screenWidth / 2, screenHeight / 2, zDepth, 10, reloadProgress);
		}
		else {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getShootTimestamp(stack);
			renderAttackIndicator(guiGraphics, screenWidth, screenHeight, zDepth, player, elapsedTime, gun.getShootDelay(stack));
		}
	}

	static void renderAmmoOverlay(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, int zDepth, ItemStack stack, Gun gun) {
		int maxAmmo = gun.getMaxAmmo(stack);
		int ammo = gun.getAmmo(stack);
		renderOrnateCorner(guiGraphics, screenWidth - 44, screenHeight - 28);
		guiGraphics.renderItem(gun.getAmmoIcon(stack), screenWidth - 16 - 4, screenHeight - 28 - 8);
		renderAmmoCount(guiGraphics, font, screenWidth, screenHeight, zDepth, maxAmmo, ammo, 0xFFFEFEFE, 0xFF9E9E9E);
	}

	static void renderAmmoCount(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, int zDepth, int maxAmmoIn, int ammoIn, int primaryColor, int secondaryColor) {
		String maxAmmo = "/" + maxAmmoIn;
		String ammo = String.valueOf(ammoIn);
		int x = screenWidth - font.width(maxAmmo) - 4;
		int y = screenHeight - font.lineHeight - 4;
		guiGraphics.drawString(font, maxAmmo, x, y, secondaryColor);
		guiGraphics.pose().pushPose();
		float scale = 1.5f; //make font bigger
		guiGraphics.pose().translate(x - font.width(ammo) * scale, y - font.lineHeight * scale * 0.5f, zDepth);
		guiGraphics.pose().scale(scale, scale, 0);
		guiGraphics.drawString(font, ammo, 0, 0, primaryColor);
		guiGraphics.pose().popPose();
	}

	public static void renderAttackIndicator(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int zDepth, LocalPlayer player, long elapsedTime, int shootDelay) {
		if (elapsedTime < shootDelay && GuiUtil.canDrawAttackIndicator(player)) {
			float progress = (float) elapsedTime / shootDelay;
			if (progress < 1f) {
				int x = screenWidth / 2 - 8;
				int y = screenHeight / 2 - 7 + 16;
				GuiRenderUtil.drawAttackIndicator(guiGraphics, x, y, progress);
			}
		}
	}

}
