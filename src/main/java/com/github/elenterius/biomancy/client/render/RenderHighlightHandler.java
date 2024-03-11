package com.github.elenterius.biomancy.client.render;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.IdentityHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class RenderHighlightHandler {

	private RenderHighlightHandler() {}

	@SubscribeEvent
	public static void onBlockHighlight(RenderHighlightEvent.Block event) {
		if (event.getCamera().getEntity() instanceof Player player) {
			ItemStack itemStack = player.getMainHandItem();
			if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DirectionalSlabBlock slabBlock) {
				BlockPlaceContext placeContext = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, event.getTarget());
				BlockPos placePos = placeContext.getClickedPos();
				BlockState foundState = player.level().getBlockState(placePos);

				DirectionalSlabType type;
				if (foundState.getBlock() instanceof DirectionalSlabBlock) {
					type = DirectionalSlabType.getHalfFrom(foundState.getValue(DirectionalSlabBlock.TYPE).getFacing().getOpposite());
				}
				else {
					type = DirectionalSlabType.getHalfFrom(placePos, placeContext.getClickLocation(), placeContext.getClickedFace());
				}
				BlockState state = slabBlock.defaultBlockState().setValue(DirectionalSlabBlock.TYPE, type);

				double cameraX = event.getCamera().getPosition().x;
				double cameraY = event.getCamera().getPosition().y;
				double cameraZ = event.getCamera().getPosition().z;

				PoseStack poseStack = event.getPoseStack();
				poseStack.pushPose();
				poseStack.translate(-cameraX, -cameraY, -cameraZ);
				renderBlock(poseStack, event.getMultiBufferSource(), state, placePos);
				poseStack.popPose();
			}
		}
	}

	public static void renderBlock(PoseStack poseStack, MultiBufferSource buffer, BlockState state, BlockPos pos) {
		poseStack.pushPose();
		poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, AlphaBuffer.of(buffer, 0.7f), 0xF000F0, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}

	private static final class AlphaBuffer implements MultiBufferSource {
		private static final AlphaBuffer delegate = new AlphaBuffer();
		private MultiBufferSource internalBuffer;
		private float alpha;

		private AlphaBuffer() {}

		public static MultiBufferSource of(MultiBufferSource bufferSource, float alpha) {
			delegate.internalBuffer = bufferSource;
			delegate.alpha = alpha;
			return delegate;
		}

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			return internalBuffer.getBuffer(AlphaRenderType.of(renderType, alpha));
		}
	}

	/**
	 * Adapted from:
	 * https://github.com/klikli-dev/modonomicon/blob/c299f3617f5f784904cfb6f501a0272a42fb2732/src/main/java/com/klikli_dev/modonomicon/client/render/MultiblockPreviewRenderer.java#L403-L429
	 * <p>
	 * Copyright (C) 2022 klikli-dev
	 * <p>
	 * Permission is hereby granted, free of charge, to any person obtaining
	 * a copy of this software and associated documentation files (the
	 * "Software"), to deal in the Software without restriction, including
	 * without limitation the rights to use, copy, modify, merge, publish,
	 * distribute, sublicense, and/or sell copies of the Software, and to
	 * permit persons to whom the Software is furnished to do so, subject to
	 * the following conditions:
	 * <p>
	 * The above copyright notice and this permission notice shall be
	 * included in all copies or substantial portions of the Software.
	 */
	private static class AlphaRenderType extends RenderType {
		private static final Map<RenderType, AlphaRenderType> remappedTypes = new IdentityHashMap<>();
		private static float alpha;

		private AlphaRenderType(RenderType original) {
			super(String.format("%s_%s_alpha", original, BiomancyMod.MOD_ID), original.format(), original.mode(), original.bufferSize(), original.affectsCrumbling(), true, () -> {
				original.setupRenderState();

				RenderSystem.disableDepthTest();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.setShaderColor(1, 1, 1, alpha);
			}, () -> {
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.disableBlend();
				RenderSystem.enableDepthTest();

				original.clearRenderState();
			});
		}

		public static AlphaRenderType of(RenderType renderType, float alpha) {
			AlphaRenderType.alpha = alpha;
			return renderType instanceof AlphaRenderType alphaType ? alphaType : remappedTypes.computeIfAbsent(renderType, AlphaRenderType::new);
		}

	}

}
