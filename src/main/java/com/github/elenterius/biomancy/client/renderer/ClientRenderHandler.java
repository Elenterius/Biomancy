package com.github.elenterius.biomancy.client.renderer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.item.IAreaHarvestingItem;
import com.github.elenterius.biomancy.item.IHighlightRayTraceResultItem;
import com.github.elenterius.biomancy.item.InjectionDeviceItem;
import com.github.elenterius.biomancy.item.ItemStorageBagItem;
import com.github.elenterius.biomancy.item.weapon.shootable.ProjectileWeaponItem;
import com.github.elenterius.biomancy.item.weapon.shootable.SinewBowItem;
import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import com.github.elenterius.biomancy.util.RayTraceUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
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

		ItemStack heldStack = player.getMainHandItem();
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
				BlockPos blockPos = traceResult.getBlockPos().relative(traceResult.getDirection());
				Vector3d pos = Vector3d.atLowerCornerOf(blockPos);
				HIGHLIGHTED_BLOCK_POS = blockPos;

				Vector3d pView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
				AxisAlignedBB axisAlignedBB = AxisAlignedBB.unitCubeFromLowerCorner(pos).move(-pView.x, -pView.y, -pView.z);
				IRenderTypeBuffer.Impl iRenderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
				IVertexBuilder buffer = iRenderTypeBuffer.getBuffer(RenderType.lines());

				int color = iHighlighter.getColorForBlock(heldStack, blockPos);
				float red = (color >> 16 & 255) / 255f;
				float green = (color >> 8 & 255) / 255f;
				float blue = (color & 255) / 255f;
				float alpha = 0.5f;
				WorldRenderer.renderLineBox(event.getMatrixStack(), buffer, axisAlignedBB, red, green, blue, alpha);
				iRenderTypeBuffer.endBatch(RenderType.lines());
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

			ItemStack heldStack = player.getMainHandItem();
			Item item = heldStack.getItem();

			if (item instanceof IHighlightRayTraceResultItem && (HIGHLIGHTED_ENTITY != null || HIGHLIGHTED_BLOCK_POS != null)) {
				event.setCanceled(true);
			}

			MatrixStack matrix = event.getMatrixStack();
			final int scaledWidth = event.getWindow().getGuiScaledWidth();
			final int scaledHeight = event.getWindow().getGuiScaledHeight();

			if (item instanceof SinewBowItem) {
				HudRenderUtil.drawBowOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (SinewBowItem) item);
			}
			else if (item instanceof ProjectileWeaponItem) {
				HudRenderUtil.drawGunOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (ProjectileWeaponItem) item);
			}
			else if (item instanceof ItemStorageBagItem) {
				HudRenderUtil.drawItemStorageBagOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (ItemStorageBagItem) item);
			}
			else if (item instanceof InjectionDeviceItem) {
				HudRenderUtil.drawInjectionDeviceOverlay(matrix, scaledWidth, scaledHeight, mc, player, heldStack, (InjectionDeviceItem) item);
			}
		}
	}

	@SubscribeEvent
	public static void onDrawBlockSelectionBox(final DrawHighlightEvent.HighlightBlock event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;

		ItemStack heldStack = player.getMainHandItem();
		if (!player.isShiftKeyDown() && !heldStack.isEmpty() && heldStack.getItem() instanceof IAreaHarvestingItem) {
			IAreaHarvestingItem aoeHarvester = (IAreaHarvestingItem) heldStack.getItem();
			byte blockHarvestRange = aoeHarvester.getBlockHarvestRange(heldStack);
			if (blockHarvestRange > 0) {
				BlockPos pos = event.getTarget().getBlockPos();
				BlockState blockState = player.clientLevel.getBlockState(pos);
				if (blockState.isAir(player.clientLevel, pos) || !aoeHarvester.isAreaSelectionVisibleFor(heldStack, pos, blockState)) return;

				Vector3d pView = event.getInfo().getPosition();
				IVertexBuilder vertexBuilder = event.getBuffers().getBuffer(RenderType.lines());

				List<BlockPos> neighbors = PlayerInteractionUtil.findBlockNeighbors(player.clientLevel, event.getTarget(), blockState, pos, blockHarvestRange, aoeHarvester.getHarvestShape(heldStack));
				for (BlockPos neighborPos : neighbors) {
					if (player.clientLevel.getWorldBorder().isWithinBounds(neighborPos)) {
						AxisAlignedBB axisAlignedBB = new AxisAlignedBB(neighborPos).move(-pView.x, -pView.y, -pView.z);
						WorldRenderer.renderLineBox(event.getMatrix(), vertexBuilder, axisAlignedBB, 0f, 0f, 0f, 0.3f);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
		ItemStack stack = event.getStack();
		if (!stack.isEmpty() && stack.getItem().getItemCategory() == BiomancyMod.ITEM_GROUP) {
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
		int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ATTUNED_BANE.get(), stack);
		if (level > 0 && AttunedDamageEnchantment.isAttuned(stack)) {
			List<ITextComponent> list = event.getToolTip();
			for (int i = 0; i < list.size(); i++) {
				ITextComponent iTextComponent = list.get(i);
				if (iTextComponent instanceof TranslationTextComponent && ((TranslationTextComponent) iTextComponent).getKey().equals(ModEnchantments.ATTUNED_BANE.get().getDescriptionId())) {
					list.set(i, ModEnchantments.ATTUNED_BANE.get().getDisplayName(level, stack));
					break;
				}
			}
		}
	}

}
