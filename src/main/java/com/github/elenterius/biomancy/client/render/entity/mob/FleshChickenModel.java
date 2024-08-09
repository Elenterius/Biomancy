package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshChicken;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshChickenModel<T extends FleshChicken> extends DefaultedEntityGeoModel<T> {

	public FleshChickenModel() {
		super(BiomancyMod.createRL("mob/flesh_chicken"), true);
	}

	@Override
	public void setCustomAnimations(T chicken, long instanceId, AnimationState<T> animationState) {
		super.setCustomAnimations(chicken, instanceId, animationState);

		float wingRotation = getWingRotation(chicken, animationState.getPartialTick());

		CoreGeoBone wing0 = getAnimationProcessor().getBone("wing0");
		if (wing0 != null) wing0.setRotZ(wingRotation);

		CoreGeoBone wing1 = getAnimationProcessor().getBone("wing1");
		if (wing1 != null) wing1.setRotZ(-wingRotation);
	}

	protected float getWingRotation(T chicken, float partialTicks) {
		float flapProgress = Mth.lerp(partialTicks, chicken.oFlap, chicken.flap);
		float flapSpeed = Mth.lerp(partialTicks, chicken.oFlapSpeed, chicken.flapSpeed);
		return (Mth.sin(flapProgress) + 1f) * flapSpeed;
	}

	@Override
	public void applyMolangQueries(T animatable, double animTime) {
		super.applyMolangQueries(animatable, animTime);

		MolangParser parser = MolangParser.INSTANCE;

		parser.setMemoizedValue("custom_query.limb_swing", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwing = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwing = animatable.walkAnimation.position(Minecraft.getInstance().getPartialTick());
				if (animatable.isBaby()) limbSwing *= 3f;
			}

			return limbSwing;
		});

		parser.setMemoizedValue("custom_query.limb_swing_amount", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwingAmount = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwingAmount = animatable.walkAnimation.speed(Minecraft.getInstance().getPartialTick());
				if (limbSwingAmount > 1f) limbSwingAmount = 1f;
			}

			return limbSwingAmount;
		});
	}

}
