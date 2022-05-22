package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.world.item.ISerumProvider;
import com.github.elenterius.biomancy.world.item.InjectorItem;
import com.github.elenterius.biomancy.world.item.weapon.IGun;
import com.github.elenterius.biomancy.world.serum.Serum;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

	public static final IIngameOverlay INJECTOR_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.options.hideGui && minecraft.player != null) {
			ItemStack itemStack = minecraft.player.getMainHandItem();
			if (itemStack.isEmpty() || !(itemStack.getItem() instanceof InjectorItem injector)) return;

			gui.setupOverlayRenderState(true, false);
			gui.setBlitOffset(-90);

			//TODO: enable when ready
//			renderInjectorOverlay(gui, poseStack, partialTicks, screenWidth, screenHeight, minecraft.player, itemStack, injector);
		}
	};

	private IngameOverlays() {}

	public static void registerGameOverlays() {
		OverlayRegistry.registerOverlayTop("Biomancy ControlStaff", CONTROL_STAFF_OVERLAY);
		OverlayRegistry.registerOverlayTop("Biomancy Gun", GUN_OVERLAY);
		OverlayRegistry.registerOverlayTop("Biomancy Injector", INJECTOR_OVERLAY);
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

	static WheelMenuHelper wheelMenuRenderer = new WheelMenuHelper();

	static void renderInjectorOverlay(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight, LocalPlayer player, ItemStack injectorStack, InjectorItem injectorItem) {
		if (GuiUtil.isFirstPersonView() && Screen.hasControlDown()) {

			List<ItemStack> foundStacks = new ArrayList<>();
			Object2IntArrayMap<Serum> foundSerums = new Object2IntArrayMap<>();

			Inventory inventory = player.getInventory();
			int slots = inventory.getContainerSize();
			for (int i = 0; i < slots; i++) {
				ItemStack stack = inventory.getItem(i);
				Item item = stack.getItem();
				if (item instanceof ISerumProvider serumProvider && !(item instanceof InjectorItem)) {
					Serum serum = serumProvider.getSerum(stack);
					if (serum != null) {
						if (!foundSerums.containsKey(serum)) foundStacks.add(stack);
						foundSerums.mergeInt(serum, stack.getCount(), Integer::sum);
					}
				}
			}

			wheelMenuRenderer.ticks++;
			wheelMenuRenderer.render(poseStack, partialTicks, screenWidth, screenHeight, foundStacks);
		}
		else {
			wheelMenuRenderer.ticks -= 2;
			wheelMenuRenderer.render(poseStack, partialTicks, screenWidth, screenHeight);
		}
	}

	static class WheelMenuHelper {
		static final float DIAGONAL_OF_ITEM = Mth.SQRT_OF_TWO * 32; // 16 * 2
		static final int HALF_OFFSET = 8;
		static final int DURATION = 25;
		int ticks = 0;
		List<ItemStack> cachedStacks;

		void render(PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
			render(poseStack, partialTicks, screenWidth, screenHeight, cachedStacks);
		}

		void render(PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight, @Nullable List<ItemStack> stacks) {
			if (stacks == null || stacks.isEmpty()) return;

			float time = ticks + partialTicks;

			if (time < 0) {
				cachedStacks = null;
				ticks = 0;
				return;
			}
			if (time > DURATION) {
				ticks = DURATION;
				time = DURATION;
			}
			cachedStacks = stacks;

			int segments = stacks.size();
			float angleIncrement = Mth.TWO_PI / segments;
			float baseRadius = DIAGONAL_OF_ITEM / angleIncrement;
			int radius = Mth.floor((baseRadius + HALF_OFFSET) * (time / DURATION));

			int x = screenWidth / 2;
			int y = screenHeight / 2;
			GuiComponent.fill(poseStack, x - radius, y - radius, x + radius, y + radius, ColorTheme.TOOLTIP_BACKGROUND_ARGB);

			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			radius = Mth.floor(baseRadius * (time / DURATION));
			for (int i = 0; i < segments; i++) {
				int v = Mth.floor(radius * Mth.cos(i * angleIncrement - Mth.HALF_PI));
				int w = Mth.floor(radius * Mth.sin(i * angleIncrement - Mth.HALF_PI));
				itemRenderer.renderAndDecorateFakeItem(stacks.get(i), x + v - HALF_OFFSET, y + w - HALF_OFFSET);
			}


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
