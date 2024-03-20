package com.github.elenterius.biomancy.client.render.block.cradle;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;

public class PrimordialCradleRenderer extends CustomGeoBlockRenderer<PrimordialCradleBlockEntity> {

	private final Vector4f vertexPosition = new Vector4f(0, 0, 0, 1);
	private float lifeEnergyPct;
	private boolean isSpecialCube = false;
	private MultiBufferSource mbs = null;

	public PrimordialCradleRenderer(BlockEntityRendererProvider.Context context) {
		super(new PrimordialCradleModel());
	}

	@Override
	public void preRender(PoseStack poseStack, PrimordialCradleBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		AnimationProcessor<?> processor = getGeoModel().getAnimationProcessor();
		CoreGeoBone boneFillLevel = processor.getBone("_fill_level");
		CoreGeoBone boneToppings = processor.getBone("_toppings");

		boneFillLevel.setHidden(true);
		boneToppings.setHidden(true);

		float biomassPct = animatable.getBiomassPct();
		if (biomassPct > 0) {
			boneFillLevel.setHidden(false);
			boneFillLevel.setPosY(Mth.floor(biomassPct * 8f) + 2f); //sets the position in model space
			if (animatable.hasModifiers()) {
				boneToppings.setHidden(false);
			}
		}

		lifeEnergyPct = Math.min(animatable.getLifeEnergyPct(), 1f);
		mbs = bufferSource;

		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderCubesOfBone(PoseStack stack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("_eye_overlay")) {
			if (lifeEnergyPct > 0) {
				isSpecialCube = true;
				if (!bone.isHidden()) {
					for (GeoCube cube : bone.getCubes()) {
						stack.pushPose();
						renderCube(stack, cube, mbs.getBuffer(RenderType.eyes(getTextureLocation(animatable))), 0xf000f0, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
						stack.popPose();
					}
				}
				isSpecialCube = false;
			}
		}
		else super.renderCubesOfBone(stack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f matrix4f, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		GeoVertex[] vertices = quad.vertices();

		if (isSpecialCube && quad.direction() == Direction.NORTH) {
			float steps = (vertices[0].position().y() - vertices[2].position().y()) * 16f; // 6 "pixels"
			float delta = (Mth.floor(lifeEnergyPct * (steps - 1f)) + 1f) / steps; //rescale to fixed step size, -+1 ensures we never get 0
			float textureV = Mth.lerp(delta, vertices[2].texV(), vertices[0].texV());
			float positionY = Mth.lerp(delta, vertices[2].position().y(), vertices[0].position().y());

			GeoVertex topLeft = vertices[0]; // Top left corner
			GeoVertex topRight = vertices[1]; // Top right corner

			for (GeoVertex vertex : vertices) {
				boolean isTopVertex = (vertex == topLeft || vertex == topRight);
				if (isTopVertex) {
					createVertex(matrix4f, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha, vertex, textureV, positionY);
				}
				else {
					createVertex(matrix4f, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha, vertex);
				}
			}
		}
		else {
			for (GeoVertex vertex : vertices) {
				createVertex(matrix4f, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha, vertex);
			}
		}
	}

	private void createVertex(Matrix4f matrix4f, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, GeoVertex geoVertex) {
		vertexPosition.set(geoVertex.position().x(), geoVertex.position().y(), geoVertex.position().z(), 1);
		matrix4f.transform(vertexPosition);
		buffer.vertex(
				vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), red, green, blue, alpha,
				geoVertex.texU(), geoVertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
		);
	}

	private void createVertex(Matrix4f matrix4f, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, GeoVertex geoVertex, float v, float y) {
		vertexPosition.set(geoVertex.position().x(), y, geoVertex.position().z(), 1);
		matrix4f.transform(vertexPosition);
		buffer.vertex(
				vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), red, green, blue, alpha,
				geoVertex.texU(), v, packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
		);
	}

}
