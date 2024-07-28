package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshChicken;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
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

}
