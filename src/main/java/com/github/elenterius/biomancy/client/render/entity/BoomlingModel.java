package com.github.elenterius.biomancy.client.render.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.ownable.Boomling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

@Deprecated
public class BoomlingModel<T extends Boomling> extends AnimatedGeoModel<T> {

	public static final ResourceLocation OVERLAY_TEXTURE = BiomancyMod.createRL("textures/entity/boomling_overlay.png");

	@Override
	public ResourceLocation getModelResource(Boomling boomling) {
		return BiomancyMod.createRL("geo/entity/boomling.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Boomling boomling) {
		return BiomancyMod.createRL("textures/entity/boomling.png");
	}

	@Override
	public ResourceLocation getAnimationResource(Boomling boomling) {
		return BiomancyMod.createRL("animations/entity/boomling.animation.json");
	}

	@Override
	public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent event) {
		super.setLivingAnimations(entity, uniqueID, event);

		if (event != null) {
			IBone head = getAnimationProcessor().getBone("head");
			IBone leftLeg0 = getAnimationProcessor().getBone("leftLeg0");
			IBone leftLeg1 = getAnimationProcessor().getBone("leftLeg1");
			IBone leftLeg2 = getAnimationProcessor().getBone("leftLeg2");
			IBone rightLeg0 = getAnimationProcessor().getBone("rightLeg0");
			IBone rightLeg1 = getAnimationProcessor().getBone("rightLeg1");
			IBone rightLeg2 = getAnimationProcessor().getBone("rightLeg2");

			//noinspection unchecked
			EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);
			head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
			head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);

			float rad25deg = 25f * Mth.DEG_TO_RAD;
			float rad45deg = 45f * Mth.DEG_TO_RAD;
			float scale = 1.6f;
			float limbSwing = event.getLimbSwing() * 0.6662f;
			float limbSwingAmount = event.getLimbSwingAmount();
			float lx0 = -(Mth.cos(limbSwing * 2f) * scale) * limbSwingAmount;
			float lx1 = -(Mth.cos(limbSwing * 2f + Mth.PI) * scale) * limbSwingAmount;
			float rx0 = -(Mth.cos(limbSwing * 2f + Mth.HALF_PI) * scale) * limbSwingAmount;
			float rx1 = -(Mth.cos(limbSwing * 2f + Mth.PI * 1.5f) * scale) * limbSwingAmount;
			float lz0 = Math.abs(Mth.sin(limbSwing) * scale) * limbSwingAmount;
			float lz1 = Math.abs(Mth.sin(limbSwing + Mth.PI) * scale) * limbSwingAmount;
			float rz0 = Math.abs(Mth.sin(limbSwing + Mth.HALF_PI) * scale) * limbSwingAmount;
			float rz1 = Math.abs(Mth.sin(limbSwing + Mth.PI * 1.5f) * scale) * limbSwingAmount;

			leftLeg0.setRotationX(rad25deg + lx0);
			leftLeg1.setRotationX(-lx0);
			leftLeg2.setRotationX(-rad25deg - lx1);

			rightLeg0.setRotationX(rad25deg + rx0);
			rightLeg1.setRotationX(rx1);
			rightLeg2.setRotationX(-rad25deg - rx1);

			leftLeg0.setRotationZ(-rad45deg + lz0);
			leftLeg1.setRotationZ(-rad45deg + -lz0);
			leftLeg2.setRotationZ(-rad45deg + lz1);
			rightLeg0.setRotationZ(rad45deg + -rz0);
			rightLeg1.setRotationZ(rad45deg + rz1);
			rightLeg2.setRotationZ(rad45deg + -rz1);
		}
	}

}