package com.github.elenterius.biomancy.client.renderer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.IAreaHarvestingItem;
import com.github.elenterius.biomancy.item.IHighlightRayTraceResultItem;
import com.github.elenterius.biomancy.item.weapon.shootable.ProjectileWeaponItem;
import com.github.elenterius.biomancy.item.weapon.shootable.SinewBowItem;
import com.github.elenterius.biomancy.util.GeometricShape;
import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import com.github.elenterius.biomancy.util.RayTraceUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null || player.isSpectator()) return;

			ItemStack heldStack = player.getHeldItemMainhand();
			Item item = heldStack.getItem();

			if (item instanceof IHighlightRayTraceResultItem && (HIGHLIGHTED_ENTITY != null || HIGHLIGHTED_BLOCK_POS != null)) {
				event.setCanceled(true);
			}

			if (item instanceof SinewBowItem) {
				SinewBowItem bowItem = (SinewBowItem) item;
				int timeLeft = player.getItemInUseCount();
				if (timeLeft == 0) timeLeft = heldStack.getUseDuration();
				int charge = heldStack.getUseDuration() - timeLeft;
				float pullProgress = Math.min((float) charge / bowItem.drawTime, 1f);
				float velocity = bowItem.getArrowVelocity(heldStack, charge) * bowItem.baseVelocity;
				float x = event.getWindow().getScaledWidth() * 0.5f;
				float y = event.getWindow().getScaledHeight() * 0.5f;
				AbstractGui.drawString(event.getMatrixStack(), Minecraft.getInstance().fontRenderer, String.format("V: %.1f", velocity), (int) x + 18, (int) y + 6, 0xFFFEFEFE);
//				drawArc(event.getMatrixStack(), x, y, 13f, 0f, pullProgress * (float) Math.PI * 2f, 0xDDF0F0F0);
				drawRectangularProgressBar(event.getMatrixStack(), x, y, 25f, pullProgress, 0xFFFEFEFE);
			}
			else if (item instanceof ProjectileWeaponItem) {
				FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

				ProjectileWeaponItem gunItem = (ProjectileWeaponItem) item;
				int ammo = gunItem.getAmmo(heldStack);
				int maxAmmo = gunItem.getMaxAmmo();
				String endPart = "/" + maxAmmo;
				String frontPart = "" + ammo;

				int scaledWidth = event.getWindow().getScaledWidth();
				int scaledHeight = event.getWindow().getScaledHeight();
				int x = scaledWidth - fontRenderer.getStringWidth(endPart) - 4;
				int y = scaledHeight - fontRenderer.FONT_HEIGHT - 4;

				AbstractGui.drawString(event.getMatrixStack(), fontRenderer, endPart, x, y, 0xFF9E9E9E);
				MatrixStack matrix = event.getMatrixStack();
				matrix.push();
				matrix.translate(scaledWidth, scaledHeight, 0);
				matrix.scale(1.5f, 1.5f, 1f);
				matrix.translate(-scaledWidth - fontRenderer.getStringWidth(frontPart) * 1.5f + fontRenderer.getStringWidth(endPart) - 4, -scaledHeight, 0);
				AbstractGui.drawString(event.getMatrixStack(), fontRenderer, frontPart, x, y, 0xFFFEFEFE);
				matrix.pop();
			}
		}
	}

	private static void drawRectangularProgressBar(MatrixStack matrixStack, float cx, float cy, float lengthA, float progress, int color) {
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float alpha = (float) (color >> 24 & 255) / 255f;
		float red = (float) (color >> 16 & 255) / 255f;
		float green = (float) (color >> 8 & 255) / 255f;
		float blue = (float) (color & 255) / 255f;

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
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
//			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(matrix, x, y + d, 0f).color(red, green, blue, alpha).endVertex();
		}

		// bottom line
		d = currentLength - 3f * halfLength;
		d = Math.min(d, lengthA);
		if (d > 0) {
			float x = cx + halfLength;
			float y = cy + halfLength;
//			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(matrix, x - d, y, 0f).color(red, green, blue, alpha).endVertex();
		}

		// left line
		d = currentLength - 5f * halfLength;
		d = Math.min(d, lengthA);
		if (d > 0) {
			float x = cx - halfLength;
			float y = cy + halfLength;
//			bufferbuilder.pos(matrix, x, y, 0f).color(red, green, blue, alpha).endVertex();
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
		RenderSystem.disableBlend();
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
		RenderSystem.defaultBlendFunc();
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
		RenderSystem.disableBlend();
	}

	@SubscribeEvent
	public static void onDrawBlockSelectionBox(final DrawHighlightEvent.HighlightBlock event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;

		ItemStack heldStack = player.getHeldItemMainhand();
		if (!player.isSneaking() && !heldStack.isEmpty() && heldStack.getItem() instanceof IAreaHarvestingItem) {
			byte blockHarvestRange = ((IAreaHarvestingItem) heldStack.getItem()).getBlockHarvestRange(heldStack);
			if (blockHarvestRange > 0) {
				BlockPos pos = event.getTarget().getPos();
				BlockState blockState = player.worldClient.getBlockState(pos);
				if (blockState.isAir(player.worldClient, pos)) return;

				Vector3d pView = event.getInfo().getProjectedView();
				IVertexBuilder vertexBuilder = event.getBuffers().getBuffer(RenderType.getLines());

				List<BlockPos> neighbors = PlayerInteractionUtil.findBlockNeighbors(player.worldClient, event.getTarget(), blockState, pos, blockHarvestRange, heldStack.getItem() == ModItems.LONG_RANGE_CLAW.get() ? GeometricShape.CUBE : GeometricShape.PLANE);
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
				if (iTextComponent instanceof TranslationTextComponent && ((TranslationTextComponent) iTextComponent).getKey().equals("enchantment.biomancy.attuned_bane")) {
					list.set(i, ModEnchantments.ATTUNED_BANE.get().getDisplayName(level, stack));
					break;
				}
			}
		}
	}

}
