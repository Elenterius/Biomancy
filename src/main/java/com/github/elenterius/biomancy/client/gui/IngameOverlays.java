package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.item.ShowKnowledgeOverlay;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.weapon.gun.Gun;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;

public final class IngameOverlays {

	public static final ResourceLocation INJECTOR_COOL_DOWN = BiomancyMod.createRL("textures/gui/indicator_injector_cooldown.png");
	public static final ResourceLocation ORNATE_CORNER_BOTTOM_RIGHT = BiomancyMod.createRL("textures/gui/ornate_corner_br.png");
	public static final ResourceLocation CHARGE_BAR = BiomancyMod.createRL("textures/gui/charge_bar.png");
	public static final ResourceLocation ATTACK_REACH = BiomancyMod.createRL("textures/gui/indicator_attack_reach.png");

	public static final IGuiOverlay GUN_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof Gun gun)) return;

			gui.setupOverlayRenderState(true, false);
			renderGunOverlay(gui, poseStack, screenWidth, screenHeight, -90, minecraft.player, itemStack, gun);
		}
	};

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

	public static final IGuiOverlay KNOWLEDGE_OVERLAY = (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.player == null || minecraft.level == null) return;
		if (minecraft.options.hideGui) return;
		if (!GuiUtil.isFirstPersonView()) return;

		BlockHitResult blockHitResult = getBlockHitResult(minecraft);
		if (blockHitResult == null) return;

		ItemStack itemStack = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
		if (itemStack.getItem() instanceof ShowKnowledgeOverlay knowledgeOverlay && knowledgeOverlay.canShowKnowledgeOverlay(itemStack, minecraft.player)) {
			BlockPos blockPos = blockHitResult.getBlockPos();
			BlockState blockState = minecraft.level.getBlockState(blockPos);
			if (blockState.getBlock() instanceof PrimordialCradleBlock && minecraft.level.getExistingBlockEntity(blockPos) instanceof PrimordialCradleBlockEntity cradle) {
				gui.setupOverlayRenderState(true, false);
				renderKnowledgeOverlay(gui, guiGraphics, screenWidth, screenHeight, cradle);
			}
		}
	};

	private IngameOverlays() {}

	static void renderKnowledgeOverlay(ForgeGui gui, GuiGraphics guiGraphics, int screenWidth, int screenHeight, PrimordialCradleBlockEntity cradle) {
		Font font = gui.getFont();

		int x = screenWidth / 2 + 64;
		int y = screenHeight / 2 - font.lineHeight - 2;

		drawValueWithLabel(guiGraphics, font, cradle.getSuccessChance(), "Success", x, y);
		y += font.lineHeight;
		drawValueWithLabel(guiGraphics, font, cradle.getBiomassPct(), "Biomass", x, y += font.lineHeight + 2);
		drawValueWithLabel(guiGraphics, font, cradle.getLifeEnergyPct(), "Life Energy", x, y += font.lineHeight + 2);
		drawValueWithLabel(guiGraphics, font, cradle.getDiseaseChance(), "Disease", x, y += font.lineHeight + 2);
		drawValueWithLabel(guiGraphics, font, cradle.getHostileChance(), "Hostile", x, y += font.lineHeight + 2);
		drawValueWithLabel(guiGraphics, font, cradle.getAnomalyChance(), "Anomaly", x, y + (font.lineHeight + 2));
	}

	static void drawValueWithLabel(GuiGraphics guiGraphics, Font font, String valueString, String label, int x, int y, Style numberStyle, Style labelStyle) {
		MutableComponent valueText = ComponentUtil.literal(valueString).withStyle(numberStyle);
		MutableComponent labelText = ComponentUtil.literal(label).withStyle(labelStyle);
		guiGraphics.drawString(font, valueText, x - font.width(valueText) - 4, y, 0xff_ffffff);
		guiGraphics.drawString(font, labelText, x, y, 0xff_ffffff);
	}

	private static void drawValueWithLabel(GuiGraphics guiGraphics, Font font, float value, String label, int x, int y) {
		drawValueWithLabel(guiGraphics, font, String.valueOf(value), label, x, y, TextStyles.PRIMORDIAL_RUNES_LIGHT_GRAY, TextStyles.PRIMORDIAL_RUNES_GRAY);
	}

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
		int x = screenWidth / 2 - 26 + screenWidth % 2; // 51 / 2 + 51 % 2 = 26
		int y = screenHeight / 2 + 16;

		guiGraphics.blit(CHARGE_BAR, x, y, zDepth, 6, 6, 51, 5, 64, 16); //background
		guiGraphics.blit(CHARGE_BAR, x, y, zDepth, 6, 11, (int) (chargePct * 51), 5, 64, 16); //foreground

		if (Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity crosshairTarget && crosshairTarget.isAlive()) {
			x = screenWidth / 2 - 24;
			y = screenHeight / 2 - 4;
			guiGraphics.blit(ATTACK_REACH, x, y, zDepth, 0, 0, 48, 16, 48, 16); //ornament
		}

		//		if (charge <= 0) return;
		//
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
		Gun.GunState gunState = gun.getGunState(stack);
		if (gunState == Gun.GunState.RELOADING) {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getReloadStartTime(stack);
			float reloadProgress = gun.getReloadProgress(elapsedTime, gun.getReloadDurationTicks(stack));
			GuiRenderUtil.drawSquareProgressBar(guiGraphics, screenWidth / 2, screenHeight / 2, zDepth, 10, reloadProgress);
		}
		else {
			long elapsedTime = player.clientLevel.getGameTime() - gun.getShootTimestamp(stack);
			renderAttackIndicator(guiGraphics, screenWidth, screenHeight, zDepth, player, elapsedTime, gun.getShootDelayTicks(stack));
		}
	}

	static void renderAmmoOverlay(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, int zDepth, ItemStack stack, Gun gun) {
		int maxAmmo = gun.getMaxAmmo(stack);
		int ammo = gun.getAmmo(stack);
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

	static @Nullable BlockHitResult getBlockHitResult(Minecraft minecraft) {
		if (minecraft.hitResult == null) return null;
		if (minecraft.hitResult.getType() != HitResult.Type.BLOCK) return null;
		if (minecraft.hitResult instanceof BlockHitResult blockHitResult) return blockHitResult;
		return null;
	}

}
