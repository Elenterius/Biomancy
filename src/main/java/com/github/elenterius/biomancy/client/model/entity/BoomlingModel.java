package com.github.elenterius.biomancy.client.model.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.ownable.Boomling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BoomlingModel<T extends Boomling> extends AnimatedGeoModel<T> {

	public static final ResourceLocation OVERLAY_TEXTURE = BiomancyMod.createRL("textures/entity/boomling_overlay.png");

	@Override
	public ResourceLocation getModelLocation(Boomling boomling) {
		return BiomancyMod.createRL("geo/entity/boomling.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Boomling boomling) {
		return BiomancyMod.createRL("textures/entity/boomling.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Boomling boomling) {
		return BiomancyMod.createRL("animations/entity/boomling.animation.json");
	}

	@Override
	public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent event) {
		super.setLivingAnimations(entity, uniqueID, event);

		if (event != null) {
			//noinspection unchecked
			EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);
			IBone head = getAnimationProcessor().getBone("head");
			head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
			head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);

			float limbSwing = event.getLimbSwing();
			float limbSwingAmount = event.getLimbSwingAmount();

			IBone leftLeg0 = getAnimationProcessor().getBone("leftLeg0");
			IBone leftLeg1 = getAnimationProcessor().getBone("leftLeg1");
			IBone leftLeg2 = getAnimationProcessor().getBone("leftLeg2");
			IBone rightLeg0 = getAnimationProcessor().getBone("rightLeg0");
			IBone rightLeg1 = getAnimationProcessor().getBone("rightLeg1");
			IBone rightLeg2 = getAnimationProcessor().getBone("rightLeg2");

			float rad45deg = 45f * Mth.DEG_TO_RAD;
			leftLeg0.setRotationZ(-rad45deg);
			leftLeg1.setRotationZ(-rad45deg);
			leftLeg2.setRotationZ(-rad45deg);
			rightLeg0.setRotationZ(rad45deg);
			rightLeg1.setRotationZ(rad45deg);
			rightLeg2.setRotationZ(rad45deg);

			float rad25deg = 25f * Mth.DEG_TO_RAD;
			leftLeg0.setRotationX(-rad25deg);
			rightLeg0.setRotationX(-rad25deg);
			leftLeg1.setRotationX(0);
			rightLeg1.setRotationX(0);
			leftLeg2.setRotationX(rad25deg);
			rightLeg2.setRotationX(rad25deg);

			float swing = limbSwing * 0.6662f;
			float scale = 1.6f;
			float lx0 = -(Mth.cos(swing * 2f) * scale) * limbSwingAmount;
			float lx1 = -(Mth.cos(swing * 2f + Mth.PI) * scale) * limbSwingAmount;
			float rx0 = -(Mth.cos(swing * 2f + Mth.HALF_PI) * scale) * limbSwingAmount;
			float rx1 = -(Mth.cos(swing * 2f + Mth.PI * 1.5f) * scale) * limbSwingAmount;
			float lz0 = Math.abs(Mth.sin(swing) * scale) * limbSwingAmount;
			float lz1 = Math.abs(Mth.sin(swing + Mth.PI) * scale) * limbSwingAmount;
			float rz0 = Math.abs(Mth.sin(swing + Mth.HALF_PI) * scale) * limbSwingAmount;
			float rz1 = Math.abs(Mth.sin(swing + Mth.PI * 1.5f) * scale) * limbSwingAmount;

			addRotationX(leftLeg0, lx0);
			addRotationX(leftLeg1, -lx0);
			addRotationX(leftLeg2, lx1);
			addRotationX(rightLeg0, -rx0);
			addRotationX(rightLeg1, rx1);
			addRotationX(rightLeg2, -rx1);
			addRotationZ(leftLeg0, lz0);
			addRotationZ(leftLeg1, -lz0);
			addRotationZ(leftLeg2, lz1);
			addRotationZ(rightLeg0, -rz0);
			addRotationZ(rightLeg1, rz1);
			addRotationZ(rightLeg2, -rz1);
		}
	}

	private void addRotationX(IBone bone, float value) {
		bone.setRotationX(bone.getRotationX() + value);
	}

	private void addRotationZ(IBone bone, float value) {
		bone.setRotationZ(bone.getRotationZ() + value);
	}

}