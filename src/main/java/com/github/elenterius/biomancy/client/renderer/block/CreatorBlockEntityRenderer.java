package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.CreatorModel;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;

public class CreatorBlockEntityRenderer extends CustomGeoBlockRenderer<CreatorBlockEntity> {

	public CreatorBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new CreatorModel());
	}

	@Override
	public void renderEarly(CreatorBlockEntity creatorEntity, PoseStack stackIn, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		IBone bone = getGeoModelProvider().getAnimationProcessor().getBone("bone_fill");
		bone.setHidden(true);

		int fillLevel = creatorEntity.getFillLevel();
		if (fillLevel > 0) {
			bone.setHidden(false);
			bone.setPositionY(fillLevel + 1f); //sets the position in animation space
		}
	}

}
