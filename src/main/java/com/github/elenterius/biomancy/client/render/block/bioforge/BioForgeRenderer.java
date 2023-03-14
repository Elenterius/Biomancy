package com.github.elenterius.biomancy.client.render.block.bioforge;

import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.github.elenterius.biomancy.world.block.bioforge.BioForgeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BioForgeRenderer extends CustomGeoBlockRenderer<BioForgeBlockEntity> {

	public BioForgeRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new BioForgeModel());
	}

	@Override
	public RenderType getRenderType(BioForgeBlockEntity blockEntity, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(blockEntity));
	}

}
