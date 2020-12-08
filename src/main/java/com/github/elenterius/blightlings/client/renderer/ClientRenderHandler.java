package com.github.elenterius.blightlings.client.renderer;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.item.IHighlightRayTraceResultItem;
import com.github.elenterius.blightlings.util.RayTraceUtil;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ClientRenderHandler
{
    public static Entity HIGHLIGHTED_ENTITY = null;
    public static BlockPos HIGHLIGHTED_BLOCK_POS = null;

    @SubscribeEvent
    public static void handleRenderWorldLast(RenderWorldLastEvent event) {
        HIGHLIGHTED_ENTITY = null;
        HIGHLIGHTED_BLOCK_POS = null;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) return;

        ItemStack heldStack = player.getHeldItemMainhand();
        if (!heldStack.isEmpty() && heldStack.getItem() instanceof IHighlightRayTraceResultItem) {
            RayTraceResult rayTraceResult = RayTraceUtil.clientRayTrace(player, event.getPartialTicks(), ((IHighlightRayTraceResultItem) heldStack.getItem()).getMaxRayTraceDistance());
            if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
                BlockRayTraceResult traceResult = (BlockRayTraceResult) rayTraceResult;
                BlockPos blockPos = traceResult.getPos().offset(traceResult.getFace());
                Vector3d pos = Vector3d.copy(blockPos);
                HIGHLIGHTED_BLOCK_POS = blockPos;

                Vector3d pView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
                AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromVector(pos).offset(-pView.x, -pView.y, -pView.z);
                IRenderTypeBuffer.Impl iRenderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                IVertexBuilder buffer = iRenderTypeBuffer.getBuffer(RenderType.getLines());
                float red = 0.8078f, green = 0f, blue = 0.0941f, alpha = 0.5f;
                WorldRenderer.drawBoundingBox(event.getMatrixStack(), buffer, axisAlignedBB, red, green, blue, alpha);
                iRenderTypeBuffer.finish(RenderType.getLines());
//                iRenderTypeBuffer.finish();
            }
            else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
                HIGHLIGHTED_ENTITY = ((EntityRayTraceResult) rayTraceResult).getEntity();
            }
        }
    }

    @SubscribeEvent
    static void onPreGameOverlayRender(final RenderGameOverlayEvent.Pre event) {
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
}
