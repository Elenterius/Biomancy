package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.BioLabModel;
import com.github.elenterius.biomancy.world.block.entity.BioLabBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BioLabBlockRenderer extends GeoBlockRenderer<BioLabBlockEntity> {

	public BioLabBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new BioLabModel());
	}

	@Override
	public RenderType getRenderType(BioLabBlockEntity blockEntity, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(blockEntity));
	}

}
