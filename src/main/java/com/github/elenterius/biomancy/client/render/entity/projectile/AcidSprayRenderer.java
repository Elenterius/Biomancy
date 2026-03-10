package com.github.elenterius.biomancy.client.render.entity.projectile;

import com.github.elenterius.biomancy.entity.projectile.AcidSprayProjectile;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class AcidSprayRenderer extends EntityRenderer<AcidSprayProjectile> {

	private static final Vector3f V_1 = new Vector3f();
	private static final Vector3f V_2 = new Vector3f();
	private static final Vector3f AXIS_1 = new Vector3f();
	private static final Vector3f AXIS_2 = new Vector3f();

	private static final Vector3f[] QUAD = new Vector3f[]{
			new Vector3f(-1f, -1f, 0f),
			new Vector3f(-1f, 1f, 0f),
			new Vector3f(1f, 1f, 0f),
			new Vector3f(1f, -1f, 0f)
	};

	private static final float ALMOST_ZERO = 1e-6f;

	private static final float Y_OFFSET = 0.125f;
	private static final float TRAIL_WIDTH = 0.25f;
	private static final float MAX_SEGMENT_LENGTH_SQUARED = 5f;

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/drip_hang.png");

	public AcidSprayRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(AcidSprayProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		int rgb = ModMobEffects.CORROSIVE.get().getColor();
		float r = (rgb >> 16 & 255) / 255f;
		float g = (rgb >> 8 & 255) / 255f;
		float b = (rgb & 255) / 255f;
		float alpha = 1f;
		int lightMap = packedLight;

		poseStack.pushPose();
		poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		poseStack.scale(0.5f, 0.5f, 0.5f);
		poseStack.translate(0f, -(0.5f + Y_OFFSET), 0f);

		RenderSystem.setShaderTexture(0, getTextureLocation(entity));
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		BufferBuilder consumer = Tesselator.getInstance().getBuilder();

		Matrix4f m = poseStack.last().pose();

		consumer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		consumer.vertex(m, QUAD[0].x(), QUAD[0].y(), QUAD[0].z()).uv(1f, 1f).color(r, g, b, alpha).uv2(lightMap).endVertex();
		consumer.vertex(m, QUAD[1].x(), QUAD[1].y(), QUAD[1].z()).uv(1f, 0f).color(r, g, b, alpha).uv2(lightMap).endVertex();
		consumer.vertex(m, QUAD[2].x(), QUAD[2].y(), QUAD[2].z()).uv(0f, 0f).color(r, g, b, alpha).uv2(lightMap).endVertex();
		consumer.vertex(m, QUAD[3].x(), QUAD[3].y(), QUAD[3].z()).uv(0f, 1f).color(r, g, b, alpha).uv2(lightMap).endVertex();
		BufferUploader.drawWithShader(consumer.end());
		poseStack.popPose();

		renderTrail(entity, poseStack, buffer, partialTick, r, g, b);

		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	private void renderTrail(AcidSprayProjectile entity, PoseStack poseStack, MultiBufferSource buffer, float partialTick, float r, float g, float b) {
		Vec3 motion = entity.getDeltaMovement();
		if (motion.lengthSqr() < 1e-6d) return;

		Vec3[] trail = entity.getTrailPositions();
		if (trail.length == 0) return;

		Vec3 currentPos = entity.getPosition(partialTick);
		Vec3 direction = motion.normalize();

		double offsetDistance = TRAIL_WIDTH * 1.75f;
		Vec3 startPos = currentPos.subtract(direction.scale(offsetDistance));

		Vec3 cameraPos = entityRenderDispatcher.camera.getPosition();

		poseStack.pushPose();
		poseStack.translate(-cameraPos.x, -cameraPos.y + Y_OFFSET, -cameraPos.z);

		Vec3[] points = new Vec3[trail.length + 1];
		points[0] = startPos;
		System.arraycopy(trail, 0, points, 1, trail.length);

		float widthOffset = (float) Math.sin(entity.getId() * 13.37d) * 0.005f;
		renderSegments(points, TRAIL_WIDTH + widthOffset, poseStack, buffer.getBuffer(ModRenderTypes.trail()), r, g, b, false);
		renderSegments(points, TRAIL_WIDTH + widthOffset, poseStack, buffer.getBuffer(ModRenderTypes.trail()), r, g, b, true);

		poseStack.popPose();
	}

	private void renderSegments(Vec3[] points, float baseWidth, PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, boolean useAxis2) {
		Camera camera = entityRenderDispatcher.camera;
		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();

		int segments = points.length - 1;

		for (int i = 0; i < segments; i++) {
			Vec3 from = points[i];
			Vec3 to = points[i + 1];

			Vector3f dir = to.subtract(from).toVector3f();
			float lengthSqr = dir.lengthSquared();
			if (lengthSqr < ALMOST_ZERO || lengthSqr > MAX_SEGMENT_LENGTH_SQUARED) continue;

			dir.normalize();

			float t = i / (float) segments;
			float width = Mth.lerp(t, baseWidth, baseWidth * 0.25f);
			float alpha = Mth.lerp(t, 0.8f, 0.1f);

			dir.cross(camera.getUpVector(), AXIS_1).normalize();
			if (AXIS_1.lengthSquared() < ALMOST_ZERO) {
				dir.cross(camera.getLeftVector(), AXIS_1).normalize();
			}

			if (useAxis2) {
				dir.cross(AXIS_1, AXIS_2).normalize();
				AXIS_2.mul(width);
				V_1.set(from.x, from.y, from.z).add(AXIS_2);
				V_2.set(to.x, to.y, to.z).sub(AXIS_2);
			}
			else {
				AXIS_1.mul(width);
				V_1.set(from.x, from.y, from.z).add(AXIS_1);
				V_2.set(to.x, to.y, to.z).sub(AXIS_1);
			}

			consumer.vertex(matrix, V_1.x, V_1.y, V_1.z).color(r, g, b, alpha).normal(normal, 0, 1, 0).endVertex();
			consumer.vertex(matrix, V_2.x, V_2.y, V_2.z).color(r, g, b, alpha).normal(normal, 0, 1, 0).endVertex();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(AcidSprayProjectile entity) {
		return TEXTURE;
	}

	//	protected int getLightMap(AcidSprayProjectile entity) {
	//		BlockPos pos = entity.blockPosition();
	//		Level level = entity.level();
	//		return level.hasChunkAt(pos) ? LevelRenderer.getLightColor(level, pos) : 0;
	//	}

	//	@Override
	//	public boolean shouldRender(AcidSprayProjectile livingEntity, Frustum camera, double camX, double camY, double camZ) {
	//		return super.shouldRender(livingEntity, camera, camX, camY, camZ);
	//	}

}