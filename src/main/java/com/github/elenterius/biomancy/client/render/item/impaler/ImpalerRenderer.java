package com.github.elenterius.biomancy.client.render.item.impaler;

import com.github.elenterius.biomancy.item.weapon.gun.ImpalerItem;
import com.github.elenterius.biomancy.mixin.accessor.PlayerRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class ImpalerRenderer extends GeoItemRenderer<ImpalerItem> {

	public ImpalerRenderer() {
		super(new ImpalerModel());
	}

	private static void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LocalPlayer player, boolean isRightArm) {
		//align arm with geckolib bone
		float direction = isRightArm ? 1f : -1f;
		poseStack.mulPose(Axis.YP.rotationDegrees(90f * direction));
		poseStack.translate(1f / 16f * direction, 0f, 0f);

		//if (!ForgeHooksClient.renderSpecificFirstPersonArm(poseStack, buffer, packedLight, player, isRightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT)) {
		renderHandWithEmptyPose(poseStack, buffer, packedLight, player, isRightArm);
		//}
	}

	private static void renderHandWithEmptyPose(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LocalPlayer player, boolean isRightArm) {
		PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
		PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();
		ModelPart armModel = isRightArm ? playerModel.rightArm : playerModel.leftArm;
		ModelPart armSleeveModel = isRightArm ? playerModel.rightSleeve : playerModel.leftSleeve;

		((PlayerRendererAccessor) playerRenderer).biomancy$SetModelProperties(player);
		playerModel.rightArmPose = HumanoidModel.ArmPose.EMPTY;
		playerModel.leftArmPose = HumanoidModel.ArmPose.EMPTY;

		armModel.setRotation(0f, 0f, 0f);
		armModel.setPos(0f, 0f, 0f);
		armSleeveModel.setRotation(0f, 0f, 0f);
		armSleeveModel.setPos(0f, 0f, 0f);

		armModel.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(player.getSkinTextureLocation())), packedLight, OverlayTexture.NO_OVERLAY); //use a render type with no culling because the left had is flipped with a negative scale
		armSleeveModel.render(poseStack, buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation())), packedLight, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, ImpalerItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("arm_placeholder")) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (renderPerspective.firstPerson() && !player.isInvisible()) {
				poseStack.pushPose();

				RenderUtils.prepMatrixForBone(poseStack, bone);
				RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);
				renderPlayerArm(poseStack, bufferSource, packedLight, player, renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
				poseStack.popPose();

				bufferSource.getBuffer(renderType); //restore buffer of impaler model
			}
		}
		else {
			super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

}
