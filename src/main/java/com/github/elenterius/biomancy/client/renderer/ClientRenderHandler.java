package com.github.elenterius.biomancy.client.renderer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.item.IAreaHarvestingItem;
import com.github.elenterius.biomancy.item.IHighlightRayTraceResultItem;
import com.github.elenterius.biomancy.item.ItemStorageBagItem;
import com.github.elenterius.biomancy.item.weapon.shootable.ProjectileWeaponItem;
import com.github.elenterius.biomancy.item.weapon.shootable.SinewBowItem;
import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import com.github.elenterius.biomancy.util.RayTraceUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientRenderHandler {

	public static Entity HIGHLIGHTED_ENTITY = null;
	public static int COLOR_ENEMY = 0xffffff;
	public static int COLOR_FRIENDLY = 0xffffff;
	public static BlockPos HIGHLIGHTED_BLOCK_POS = null;

	private ClientRenderHandler() {}

	@SubscribeEvent
	public static void handleRenderWorldLast(RenderWorldLastEvent event) {
		HIGHLIGHTED_ENTITY = null;
		HIGHLIGHTED_BLOCK_POS = null;
		COLOR_ENEMY = 0xffffff;
		COLOR_FRIENDLY = 0xffffff;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || player.isSpectator()) return;

		ItemStack heldStack = player.getHeldItemMainhand();
		if (!heldStack.isEmpty() && heldStack.getItem() instanceof IHighlightRayTraceResultItem) {
			IHighlightRayTraceResultItem iHighlighter = (IHighlightRayTraceResultItem) heldStack.getItem();
			boolean canHighlightEntities = iHighlighter.canHighlightLivingEntities(heldStack);
			boolean canHighlightBlocks = iHighlighter.canHighlightBlocks(heldStack);
			if (!canHighlightEntities && !canHighlightBlocks) return;

			RayTraceResult rayTraceResult;
			if (!canHighlightEntities) {
				rayTraceResult = player.pick(iHighlighter.getMaxRayTraceDistance(), event.getPartialTicks(), false);
			}
			else {
				rayTraceResult = RayTraceUtil.clientRayTrace(player, event.getPartialTicks(), iHighlighter.getMaxRayTraceDistance());
			}

			if (canHighlightBlocks && rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
				BlockRayTraceResult traceResult = (BlockRayTraceResult) rayTraceResult;
				BlockPos blockPos = traceResult.getPos().offset(traceResult.getFace());
				Vector3d pos = Vector3d.copy(blockPos);
				HIGHLIGHTED_BLOCK_POS = blockPos;

				Vector3d pView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
				AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromVector(pos).offset(-pView.x, -pView.y, -pView.z);
				IRenderTypeBuffer.Impl iRenderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
				IVertexBuilder buffer = iRenderTypeBuffer.getBuffer(RenderType.getLines());

				int color = iHighlighter.getColorForBlock(heldStack, blockPos);
				float red = (float) (color >> 16 & 255) / 255f;
				float green = (float) (color >> 8 & 255) / 255f;
				float blue = (float) (color & 255) / 255f;
				float alpha = 0.5f;
				WorldRenderer.drawBoundingBox(event.getMatrixStack(), buffer, axisAlignedBB, red, green, blue, alpha);
				iRenderTypeBuffer.finish(RenderType.getLines());
//                iRenderTypeBuffer.finish();
			}
			else if (canHighlightEntities && rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
				HIGHLIGHTED_ENTITY = ((EntityRayTraceResult) rayTraceResult).getEntity();
				COLOR_ENEMY = iHighlighter.getColorForEnemyEntity(heldStack, HIGHLIGHTED_ENTITY);
				COLOR_FRIENDLY = iHighlighter.getColorForFriendlyEntity(heldStack, HIGHLIGHTED_ENTITY);
			}
		}
	}

	@SubscribeEvent
	public static void onPreRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
			if (player == null || player.isSpectator()) return;

			ItemStack heldStack = player.getHeldItemMainhand();
			Item item = heldStack.getItem();

			if (item instanceof IHighlightRayTraceResultItem && (HIGHLIGHTED_ENTITY != null || HIGHLIGHTED_BLOCK_POS != null)) {
				event.setCanceled(true);
			}

			MatrixStack matrix = event.getMatrixStack();
			final int scaledWidth = event.getWindow().getScaledWidth();
			final int scaledHeight = event.getWindow().getScaledHeight();

			if (item instanceof SinewBowItem) {
				renderBowOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (SinewBowItem) item);
			}
			else if (item instanceof ProjectileWeaponItem) {
				renderGunOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (ProjectileWeaponItem) item);
			}
			else if (item instanceof ItemStorageBagItem) {
				renderItemStorageBagOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (ItemStorageBagItem) item);
			}
		}
	}

	private static final ResourceLocation ITEM_BAG_INDICATOR_TEX = BiomancyMod.createRL("textures/gui/item_bag_indicator.png");

	private static void renderItemStorageBagOverlay(MatrixStack matrixStack, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, ItemStorageBagItem item) {
		ItemStorageBagItem.Mode mode = item.getMode(stack);
		if (mode == ItemStorageBagItem.Mode.NONE || !canDrawAttackIndicator(mc, player)) return;
		float fullness = item.getFullness(stack);
		if (fullness <= 0f || (mode == ItemStorageBagItem.Mode.DEVOUR && fullness >= 1f)) return;

		if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) mc.objectMouseOver;
			TileEntity tile = player.world.getTileEntity(rayTraceResult.getPos());
			if (tile != null) {
				LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (capability.isPresent()) {
					mc.getTextureManager().bindTexture(ITEM_BAG_INDICATOR_TEX);
					int x = scaledWidth / 2 - 16 - 8;
					int y = scaledHeight / 2 + 9;
					AbstractGui.blit(matrixStack, x, y, 0, mode == ItemStorageBagItem.Mode.DEVOUR ? 0 : 16, 32, 16, 32, 32);

					CompoundNBT nbt = stack.getOrCreateTag();
					if (nbt.contains(ItemStorageBagItem.NBT_KEY_INVENTORY)) {
						ItemStack storedStack = ItemStack.read(nbt.getCompound(ItemStorageBagItem.NBT_KEY_INVENTORY).getCompound(ItemStorageBagItem.NBT_KEY_ITEM));
						mc.getItemRenderer().renderItemIntoGUI(storedStack, x + 32, y);
					}
				}
			}
		}
	}

	private static void renderBowOverlay(MatrixStack matrixStack, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, SinewBowItem item) {
		int timeLeft = player.getItemInUseCount();
		if (timeLeft == 0) timeLeft = stack.getUseDuration();
		int charge = stack.getUseDuration() - timeLeft;
		float pullProgress = Math.min((float) charge / item.drawTime, 1f);
		float velocity = item.getArrowVelocity(stack, charge) * item.baseVelocity;
		float x = scaledWidth * 0.5f;
		float y = scaledHeight * 0.5f;
		AbstractGui.drawString(matrixStack, mc.fontRenderer, String.format("V: %.1f", velocity), (int) x + 18, (int) y + 6, 0xFFFEFEFE);
//		drawCircularProgressIndicator(matrixStack, x, y, 13f, pullProgress, 0xDDF0F0F0);
		drawRectangularProgressIndicator(matrixStack, x, y, 25f, pullProgress, 0xFFFEFEFE);
	}

	private static void renderGunOverlay(MatrixStack matrix, int scaledWidth, int scaledHeight, Minecraft mc, ClientPlayerEntity player, ItemStack stack, ProjectileWeaponItem item) {
		FontRenderer fontRenderer = mc.fontRenderer;

		//draw ammunition count
		String maxAmmo = "/" + item.getMaxAmmo(stack);
		String ammo = "" + item.getAmmo(stack);
		int x = scaledWidth - fontRenderer.getStringWidth(maxAmmo) - 4;
		int y = scaledHeight - fontRenderer.FONT_HEIGHT - 4;
		AbstractGui.drawString(matrix, fontRenderer, maxAmmo, x, y, 0xFF9E9E9E);
		matrix.push();
		float scale = 1.5f; //make font bigger
		matrix.translate(x - fontRenderer.getStringWidth(ammo) * scale, y - fontRenderer.FONT_HEIGHT * scale * 0.5f, 0);
		matrix.scale(scale, scale, 0);
		AbstractGui.drawString(matrix, fontRenderer, ammo, 0, 0, 0xFFFEFEFE);
		matrix.pop();

		GameSettings gamesettings = mc.gameSettings;
		if (gamesettings.getPointOfView().func_243192_a() && !gamesettings.showDebugInfo) { // is in first person view
			ProjectileWeaponItem.State gunState = item.getState(stack);
			if (gunState == ProjectileWeaponItem.State.RELOADING) {
				long elapsedTime = player.worldClient.getGameTime() - item.getReloadStartTime(stack);
				float reloadProgress = item.getReloadProgress(elapsedTime, item.getReloadTime(stack));
				drawRectangularProgressIndicator(matrix, scaledWidth * 0.5f, scaledHeight * 0.5f, 20f, reloadProgress, 0xFF9E9E9E);
			}
			else {
				long elapsedTime = player.worldClient.getGameTime() - stack.getOrCreateTag().getLong(ProjectileWeaponItem.NBT_KEY_SHOOT_TIMESTAMP);
				int shootDelay = item.getShootDelay(stack);
				if (elapsedTime < shootDelay) {
					if (canDrawAttackIndicator(mc, player)) {
						float progress = (float) elapsedTime / shootDelay;
						if (progress < 1f) {
							mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
							RenderSystem.enableBlend();
							RenderSystem.enableAlphaTest();
							RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

							x = scaledWidth / 2 - 8;
							y = scaledHeight / 2 - 7 + 16;
							mc.ingameGUI.blit(matrix, x, y, 36, 94, 16, 4);
							mc.ingameGUI.blit(matrix, x, y, 52, 94, (int) (progress * 17f), 4);

							RenderSystem.defaultBlendFunc();
						}
					}
				}
			}
		}
	}

	private static boolean canDrawAttackIndicator(Minecraft mc, ClientPlayerEntity player) {
		if (mc.gameSettings.attackIndicator != AttackIndicatorStatus.CROSSHAIR) return true;
		boolean isVisible = false;
		float attackStrength = player.getCooledAttackStrength(0f);
		if (mc.pointedEntity instanceof LivingEntity && attackStrength >= 1f) {
			isVisible = player.getCooldownPeriod() > 5f & mc.pointedEntity.isAlive();
		}
		return !isVisible && attackStrength >= 1f;
	}

	private static void drawRectangularProgressIndicator(MatrixStack matrixStack, float cx, float cy, float lengthA, float progress, int color) {
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float alpha = (float) (color >> 24 & 255) / 255f;
		float red = (float) (color >> 16 & 255) / 255f;
		float green = (float) (color >> 8 & 255) / 255f;
		float blue = (float) (color & 255) / 255f;

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
		float d = currentLength;
		d = Math.min(d, halfLength);
		if (d > 0) {
			float x = cx;
			float y = cy - halfLength;
			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(matrix, x + halfLength, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// right line
		d = currentLength - halfLength;
		d = Math.min(d, lengthA);
		if (d > 0) {
			float x = cx + halfLength;
			float y = cy - halfLength;
			bufferbuilder.pos(matrix, x, y + d, 0f).color(red, green, blue, alpha).endVertex();
		}

		// bottom line
		d = currentLength - 3f * halfLength;
		d = Math.min(d, lengthA);
		if (d > 0) {
			float x = cx + halfLength;
			float y = cy + halfLength;
			bufferbuilder.pos(matrix, x - d, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// left line
		d = currentLength - 5f * halfLength;
		d = Math.min(d, lengthA);
		if (d > 0) {
			float x = cx - halfLength;
			float y = cy + halfLength;
			bufferbuilder.pos(matrix, x, y - d, 0f).color(red, green, blue, alpha).endVertex();
		}

		// top left line
		d = currentLength - 7f * halfLength;
		d = Math.min(d, halfLength);
		if (d > 0) {
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

	private static void drawCircularProgressIndicator(MatrixStack matrixStack, float cx, float cy, float radius, float progress, int color) {
		drawArc(matrixStack, cx, cy, radius, 0f, progress * (float) Math.PI * 2f, color);
	}

	private static void drawArc(MatrixStack matrixStack, float cx, float cy, float radius, float startAngle, float endAngle, int color) {
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float alpha = (float) (color >> 24 & 255) / 255f;
		float red = (float) (color >> 16 & 255) / 255f;
		float green = (float) (color >> 8 & 255) / 255f;
		float blue = (float) (color & 255) / 255f;

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

	@SubscribeEvent
	public static void onDrawBlockSelectionBox(final DrawHighlightEvent.HighlightBlock event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;

		ItemStack heldStack = player.getHeldItemMainhand();
		if (!player.isSneaking() && !heldStack.isEmpty() && heldStack.getItem() instanceof IAreaHarvestingItem) {
			IAreaHarvestingItem aoeHarvester = (IAreaHarvestingItem) heldStack.getItem();
			byte blockHarvestRange = aoeHarvester.getBlockHarvestRange(heldStack);
			if (blockHarvestRange > 0) {
				BlockPos pos = event.getTarget().getPos();
				BlockState blockState = player.worldClient.getBlockState(pos);
				if (blockState.isAir(player.worldClient, pos)) return;

				Vector3d pView = event.getInfo().getProjectedView();
				IVertexBuilder vertexBuilder = event.getBuffers().getBuffer(RenderType.getLines());

				List<BlockPos> neighbors = PlayerInteractionUtil.findBlockNeighbors(player.worldClient, event.getTarget(), blockState, pos, blockHarvestRange, aoeHarvester.getHarvestShape(heldStack));
				for (BlockPos neighborPos : neighbors) {
					if (player.worldClient.getWorldBorder().contains(neighborPos)) {
						AxisAlignedBB axisAlignedBB = new AxisAlignedBB(neighborPos).offset(-pView.x, -pView.y, -pView.z);
						WorldRenderer.drawBoundingBox(event.getMatrix(), vertexBuilder, axisAlignedBB, 0f, 0f, 0f, 0.3f);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
		ItemStack stack = event.getStack();
		if (!stack.isEmpty() && stack.getItem().getGroup() == BiomancyMod.ITEM_GROUP) {
			event.setBackground(0xED000000);
			int borderColorStart = 0xFFAAAAAA;
			int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
			event.setBorderStart(borderColorStart);
			event.setBorderEnd(borderColorEnd);
		}
	}

	@SubscribeEvent
	public static void onItemTooltip(final ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ATTUNED_BANE.get(), stack);
		if (level > 0 && AttunedDamageEnchantment.isAttuned(stack)) {
			List<ITextComponent> list = event.getToolTip();
			for (int i = 0; i < list.size(); i++) {
				ITextComponent iTextComponent = list.get(i);
				if (iTextComponent instanceof TranslationTextComponent && ((TranslationTextComponent) iTextComponent).getKey().equals(ModEnchantments.ATTUNED_BANE.get().getName())) {
					list.set(i, ModEnchantments.ATTUNED_BANE.get().getDisplayName(level, stack));
					break;
				}
			}
		}
	}

}
