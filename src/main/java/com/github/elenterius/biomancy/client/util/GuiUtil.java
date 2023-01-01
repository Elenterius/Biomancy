package com.github.elenterius.biomancy.client.util;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class GuiUtil {

	private GuiUtil() {}

	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public static boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public static boolean isFirstPersonView() {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	public static boolean canDrawAttackIndicator(LocalPlayer player) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.attackIndicator().get() != AttackIndicatorStatus.CROSSHAIR) return true;
		boolean isVisible = false;
		float attackStrength = player.getAttackStrengthScale(0f);
		if (minecraft.crosshairPickEntity instanceof LivingEntity && attackStrength >= 1f) {
			isVisible = player.getCurrentItemAttackStrengthDelay() > 5f && minecraft.crosshairPickEntity.isAlive();
		}
		return !isVisible && attackStrength >= 1f;
	}

}
