package com.github.elenterius.biomancy.client.render.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.ownable.Boomling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

@Deprecated
public class BoomlingModel<T extends Boomling> extends DefaultedEntityGeoModel<T> {

	public static final ResourceLocation OVERLAY_TEXTURE = BiomancyMod.createRL("textures/entity/boomling_overlay.png");

	public BoomlingModel() {
		super(BiomancyMod.createRL("boomling"));
	}

	@Override
	public void setCustomAnimations(T entity, long instanceId, AnimationState<T> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		CoreGeoBone leftLeg0 = getAnimationProcessor().getBone("leftLeg0");
		CoreGeoBone leftLeg1 = getAnimationProcessor().getBone("leftLeg1");
		CoreGeoBone leftLeg2 = getAnimationProcessor().getBone("leftLeg2");
		CoreGeoBone rightLeg0 = getAnimationProcessor().getBone("rightLeg0");
		CoreGeoBone rightLeg1 = getAnimationProcessor().getBone("rightLeg1");
		CoreGeoBone rightLeg2 = getAnimationProcessor().getBone("rightLeg2");

		EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
		head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

		float rad25deg = 25f * Mth.DEG_TO_RAD;
		float rad45deg = 45f * Mth.DEG_TO_RAD;
		float scale = 1.6f;
		float limbSwing = animationState.getLimbSwing() * 0.6662f;
		float limbSwingAmount = animationState.getLimbSwingAmount();
		float lx0 = -(Mth.cos(limbSwing * 2f) * scale) * limbSwingAmount;
		float lx1 = -(Mth.cos(limbSwing * 2f + Mth.PI) * scale) * limbSwingAmount;
		float rx0 = -(Mth.cos(limbSwing * 2f + Mth.HALF_PI) * scale) * limbSwingAmount;
		float rx1 = -(Mth.cos(limbSwing * 2f + Mth.PI * 1.5f) * scale) * limbSwingAmount;
		float lz0 = Math.abs(Mth.sin(limbSwing) * scale) * limbSwingAmount;
		float lz1 = Math.abs(Mth.sin(limbSwing + Mth.PI) * scale) * limbSwingAmount;
		float rz0 = Math.abs(Mth.sin(limbSwing + Mth.HALF_PI) * scale) * limbSwingAmount;
		float rz1 = Math.abs(Mth.sin(limbSwing + Mth.PI * 1.5f) * scale) * limbSwingAmount;

		leftLeg0.setRotX(rad25deg + lx0);
		leftLeg1.setRotX(-lx0);
		leftLeg2.setRotX(-rad25deg - lx1);

		rightLeg0.setRotX(rad25deg + rx0);
		rightLeg1.setRotX(rx1);
		rightLeg2.setRotX(-rad25deg - rx1);

		leftLeg0.setRotZ(-rad45deg + lz0);
		leftLeg1.setRotZ(-rad45deg + -lz0);
		leftLeg2.setRotZ(-rad45deg + lz1);
		rightLeg0.setRotZ(rad45deg + -rz0);
		rightLeg1.setRotZ(rad45deg + rz1);
		rightLeg2.setRotZ(rad45deg + -rz1);
	}

}