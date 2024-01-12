package com.github.elenterius.biomancy.client.render.block.cradle;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;

public class PrimordialCradleRenderer extends CustomGeoBlockRenderer<PrimordialCradleBlockEntity> {

	private final Vector4f vertexPosition = new Vector4f(0, 0, 0, 1);
	private float lifeEnergyPct;
	private boolean isSpecialCube = false;

	public PrimordialCradleRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new PrimordialCradleModel());
	}

	@Override
	public void renderEarly(PrimordialCradleBlockEntity cradle, PoseStack stack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		AnimationProcessor<?> processor = getGeoModelProvider().getAnimationProcessor();
		IBone boneFillLevel = processor.getBone("_fill_level");
		IBone boneToppings = processor.getBone("_toppings");

		boneFillLevel.setHidden(true);
		boneToppings.setHidden(true);

		float biomassPct = cradle.getBiomassPct();
		if (biomassPct > 0) {
			boneFillLevel.setHidden(false);
			boneFillLevel.setPositionY(Mth.floor(biomassPct * 8f) + 2f); //sets the position in model space
			if (cradle.hasModifiers()) {
				boneToppings.setHidden(false);
			}
		}

		lifeEnergyPct = Math.min(cradle.getLifeEnergyPct(), 1f);
	}

	@Override
	public void renderCubesOfBone(GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("_eye_overlay")) {
			if (lifeEnergyPct > 0) {
				float steps = 8f; //UV face height in pixels
				lifeEnergyPct = (Mth.floor(lifeEnergyPct * (steps - 1f)) + 1f) / steps; //rescale to fixed step size, -+1 ensures we never get 0
				isSpecialCube = true;
				for (GeoCube cube : bone.childCubes) {
					if (!bone.cubesAreHidden()) {
						stack.pushPose();
						renderCube(cube, stack, rtb.getBuffer(RenderType.eyes(PrimordialCradleModel.TEXTURE)), 0xf000f0, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
						stack.popPose();
					}
				}
				isSpecialCube = false;
			}
		}
		else super.renderCubesOfBone(bone, stack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f matrix4f, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		GeoVertex[] vertices = quad.vertices;

		if (isSpecialCube && quad.direction == Direction.NORTH) {
			float textureV = Mth.lerp(lifeEnergyPct, vertices[2].textureV, vertices[0].textureV);
			float positionY = Mth.lerp(lifeEnergyPct, vertices[2].position.y(), vertices[0].position.y());

			GeoVertex topLeft = quad.vertices[0]; // Top left corner
			GeoVertex topRight = quad.vertices[1]; // Top right corner

			for (GeoVertex vertex : quad.vertices) {
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
		vertexPosition.set(geoVertex.position.x(), geoVertex.position.y(), geoVertex.position.z(), 1);
		vertexPosition.transform(matrix4f);
		buffer.vertex(
				vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), red, green, blue, alpha,
				geoVertex.textureU, geoVertex.textureV, packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
		);
	}

	private void createVertex(Matrix4f matrix4f, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, GeoVertex geoVertex, float v, float y) {
		vertexPosition.set(geoVertex.position.x(), y, geoVertex.position.z(), 1);
		vertexPosition.transform(matrix4f);
		buffer.vertex(
				vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), red, green, blue, alpha,
				geoVertex.textureU, v, packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
		);
	}

}
