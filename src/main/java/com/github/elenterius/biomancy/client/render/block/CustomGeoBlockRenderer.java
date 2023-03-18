package com.github.elenterius.biomancy.client.render.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public abstract class CustomGeoBlockRenderer<T extends BlockEntity & IAnimatable> extends GeoBlockRenderer<T> {

	protected CustomGeoBlockRenderer(BlockEntityRendererProvider.Context context, AnimatedGeoModel<T> modelProvider) {
		super(context, modelProvider);
	}

	@Override
	public int getViewDistance() {
		return 96;
	}

	/**
	 * Max distance at which animations are still played.
	 *
	 * @return value smaller or equal to the view distance
	 */
	public int getAnimationDistance() {
		return 48;
	}

	public boolean shouldAnimate(T blockEntity) {
		Vec3 cameraPos = Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.getPosition();
		return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPos, getAnimationDistance());
	}

	@Override
	public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(blockEntity));

		if (shouldAnimate(blockEntity)) {
			modelProvider.setCustomAnimations(blockEntity, getInstanceId(blockEntity));
		}

		dispatchedMat = poseStack.last().pose().copy();
		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);

		poseStack.pushPose();

		//poseStack.translate(0, 0.01f, 0); //by geckolib
		//supposedly this is related to lighting issues, but I couldn't find any
		//we will keep this translation disabled until some issue reports surface and re-evaluate this change

		poseStack.translate(0.5, 0, 0.5);
		rotateBlock(getFacing(blockEntity), poseStack);

		RenderSystem.setShaderTexture(0, getTextureLocation(blockEntity));
		Color renderColor = getRenderColor(blockEntity, partialTick, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(blockEntity, partialTick, poseStack, bufferSource, null, packedLight, getTextureLocation(blockEntity));
		render(model, blockEntity, partialTick, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY, renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);

		poseStack.popPose();
	}

	/**
	 * fixes GeckoLib bug: GeoBone#getWorldPosition returns the false position (position is not translated by the pivot point)
	 */
	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();

		RenderUtils.translateMatrixToBone(poseStack, bone);
		RenderUtils.translateToPivotPoint(poseStack, bone);
		RenderUtils.rotateMatrixAroundBone(poseStack, bone);
		RenderUtils.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, dispatchedMat);
			BlockPos pos = animatable.getBlockPos();
			Matrix4f worldState = localMatrix.copy();

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, renderEarlyMat));
			bone.setLocalSpaceXform(localMatrix);

			worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
			bone.setWorldSpaceXform(worldState);
		}

		RenderUtils.translateAwayFromPivotPoint(poseStack, bone); //has to be called after tracking the matrices

		renderCubesOfBone(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}

	/**
	 * we intentionally hide the parent class method
	 */
	protected Direction getFacing(T tile) {
		BlockState state = tile.getBlockState();
		if (state.hasProperty(HorizontalDirectionalBlock.FACING)) return state.getValue(HorizontalDirectionalBlock.FACING);
		else if (state.hasProperty(DirectionalBlock.FACING)) return state.getValue(DirectionalBlock.FACING);
		return Direction.NORTH;
	}

}
