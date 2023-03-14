package com.github.elenterius.biomancy.client.render.block.decomposer;

import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class DecomposerRenderer extends CustomGeoBlockRenderer<DecomposerBlockEntity> {

	public DecomposerRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new DecomposerModel());
	}

	@Override
	public RenderType getRenderType(DecomposerBlockEntity blockEntity, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(blockEntity));
	}

}
