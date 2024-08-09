package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshSheep;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FleshSheepModel<T extends FleshSheep> extends DefaultedEntityGeoModel<T> {

	public FleshSheepModel() {
		super(BiomancyMod.createRL("mob/flesh_sheep"), true);
	}

	@Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {

		if (FleshSheep.Animations.getEatAnimationTick(animatable) > 0) {
			return; //during the grazing animation don't override the head rotation
		}

		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head == null) return;

		EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
		head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
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
