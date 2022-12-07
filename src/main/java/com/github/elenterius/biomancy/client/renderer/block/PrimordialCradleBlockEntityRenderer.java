package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.PrimordialCradleModel;
import com.github.elenterius.biomancy.world.block.entity.PrimordialCradleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;

public class PrimordialCradleBlockEntityRenderer extends CustomGeoBlockRenderer<PrimordialCradleBlockEntity> {

	private float lifeEnergyPct;

	public PrimordialCradleBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new PrimordialCradleModel());
	}

	@Override
	public void renderEarly(PrimordialCradleBlockEntity cradle, PoseStack stackIn, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
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

	private boolean isSpecialCube = false;

	@Override
	public void renderCubesOfBone(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("_eye_overlay")) {
			if (lifeEnergyPct > 0) {
				float steps = 8f; //UV face height in pixels
				lifeEnergyPct = (Mth.floor(lifeEnergyPct * (steps - 1f)) + 1f) / steps; //rescale to fixed step size, -+1 ensures we never get 0
				isSpecialCube = true;
				for (GeoCube cube : bone.childCubes) {
					stack.pushPose();
					if (!bone.cubesAreHidden()) {
						renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					}
					stack.popPose();
				}
				isSpecialCube = false;
			}
		} else super.renderCubesOfBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f matrix4f, Vector3f normal, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (isSpecialCube && quad.direction == Direction.NORTH) {
			float textureV = Mth.lerp(lifeEnergyPct, quad.vertices[2].textureV, quad.vertices[0].textureV);
			float positionY = Mth.lerp(lifeEnergyPct, quad.vertices[2].position.y(), quad.vertices[0].position.y());

			GeoVertex topLeft = quad.vertices[0]; // Top left corner
			GeoVertex topRight = quad.vertices[1]; // Top right corner

			for (GeoVertex vertex : quad.vertices) {
				boolean isTopVertex = (vertex == topLeft || vertex == topRight);

				Vector4f vector4f = new Vector4f(vertex.position.x(), isTopVertex ? positionY : vertex.position.y(), vertex.position.z(), 1f);
				vector4f.transform(matrix4f);
				bufferIn.vertex(
						vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha,
						vertex.textureU, isTopVertex ? textureV : vertex.textureV,
						packedOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z()
				);
			}
		} else {super.createVerticesOfQuad(quad, matrix4f, normal, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);}
	}

}
