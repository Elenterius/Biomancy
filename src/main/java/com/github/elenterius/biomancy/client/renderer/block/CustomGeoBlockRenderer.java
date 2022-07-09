package com.github.elenterius.biomancy.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public abstract class CustomGeoBlockRenderer<T extends BlockEntity & IAnimatable> extends GeoBlockRenderer<T> {

	protected CustomGeoBlockRenderer(BlockEntityRendererProvider.Context context, AnimatedGeoModel<T> modelProvider) {
		super(context, modelProvider);
	}

	@Override
	public void render(T tile, float partialTicks, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
		stack.pushPose();

		stack.translate(0, -0.01f, 0); //undo translation by geckolib
		//supposedly this is related to lighting issues, but I couldn't find any
		//we will keep this modification until some issue reports surface and re-evaluate this change

		super.render(tile, partialTicks, stack, bufferSource, packedLight);
		stack.popPose();
	}
}
