package com.github.elenterius.biomancy.client.render.entity;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import java.util.Collections;

public class CustomGeoProjectileRenderer<T extends BaseProjectile & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T> {

	static {
		AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity ? (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity(entity) : null);
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected MultiBufferSource rtb = null;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public CustomGeoProjectileRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T projectile, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(projectile));
		dispatchedMat = poseStack.last().pose().copy();

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);

		poseStack.pushPose();

		poseStack.translate(0, projectile.getBbHeight() / 2f / 16f, 0);

		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, projectile.yRotO, projectile.getYRot()) - 90f));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, projectile.xRotO, projectile.getXRot())));

		AnimationEvent<T> predicate = new AnimationEvent<>(projectile, 0, 0, partialTick, false, Collections.singletonList(new EntityModelData()));
		modelProvider.setCustomAnimations(projectile, getInstanceId(projectile), predicate);

		if (!projectile.isInvisibleTo(Minecraft.getInstance().player)) {
			ResourceLocation textureLocation = getTextureLocation(projectile);
			RenderSystem.setShaderTexture(0, textureLocation);

			Color renderColor = getRenderColor(projectile, partialTick, poseStack, bufferSource, null, packedLight);
			RenderType renderType = getRenderType(projectile, partialTick, poseStack, bufferSource, null, packedLight, textureLocation);

			render(model, projectile, partialTick, renderType, poseStack, bufferSource, null, packedLight, getPackedOverlay(projectile, 0), renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
		}

		poseStack.popPose();

		super.render(projectile, yaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderEarly(T projectile, PoseStack poseStack, float partialTick, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		renderEarlyMat = poseStack.last().pose().copy();
		animatable = projectile;
		IGeoRenderer.super.renderEarly(projectile, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, renderEarlyMat));
			localMatrix.translate(new Vector3f(getRenderOffset(animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(animatable.position()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public int getPackedOverlay(T projectile, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return modelProvider;
	}

	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return currentModelRenderCycle;
	}

	@Override
	public void setCurrentModelRenderCycle(IRenderCycle renderCycle) {
		currentModelRenderCycle = renderCycle;
	}

	@Override
	public float getWidthScale(T projectile) {
		return widthScale;
	}

	@Override
	public float getHeightScale(T projectile) {
		return heightScale;
	}

	@Override
	public ResourceLocation getTextureLocation(T projectile) {
		return modelProvider.getTextureResource(projectile);
	}

	@Override
	public int getInstanceId(T projectile) {
		return projectile.getUUID().hashCode();
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return rtb;
	}

	@Override
	public void setCurrentRTB(MultiBufferSource buffer) {
		rtb = buffer;
	}
}
