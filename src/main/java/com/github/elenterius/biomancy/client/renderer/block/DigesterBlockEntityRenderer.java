package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.DigesterModel;
import com.github.elenterius.biomancy.world.block.entity.DigesterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class DigesterBlockEntityRenderer extends CustomGeoBlockRenderer<DigesterBlockEntity> {

	public DigesterBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new DigesterModel());
	}

	@Override
	public RenderType getRenderType(DigesterBlockEntity blockEntity, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(blockEntity));
	}

}
