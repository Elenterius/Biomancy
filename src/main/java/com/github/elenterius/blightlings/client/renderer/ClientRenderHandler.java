package com.github.elenterius.blightlings.client.renderer;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.blightlings.init.ModEnchantments;
import com.github.elenterius.blightlings.item.IHighlightRayTraceResultItem;
import com.github.elenterius.blightlings.util.RayTraceUtil;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientRenderHandler {
	private ClientRenderHandler() {}

	public static Entity HIGHLIGHTED_ENTITY = null;
	public static int COLOR_ENEMY = 0xffffff;
	public static int COLOR_FRIENDLY = 0xffffff;
	public static BlockPos HIGHLIGHTED_BLOCK_POS = null;

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
			} else {
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
			} else if (canHighlightEntities && rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
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
			if (!heldStack.isEmpty() && heldStack.getItem() instanceof IHighlightRayTraceResultItem && (HIGHLIGHTED_ENTITY != null || HIGHLIGHTED_BLOCK_POS != null)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
		ItemStack stack = event.getStack();
		if (!stack.isEmpty() && stack.getItem().getGroup() == BlightlingsMod.ITEM_GROUP) {
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
				if (iTextComponent instanceof TranslationTextComponent && ((TranslationTextComponent) iTextComponent).getKey().equals("enchantment.blightlings.attuned_bane")) {
					list.set(i, ModEnchantments.ATTUNED_BANE.get().getDisplayName(level, stack));
					break;
				}
			}
		}
	}

}
