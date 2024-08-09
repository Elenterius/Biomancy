package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshPig;
import net.minecraft.client.Minecraft;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshPigModel<T extends FleshPig> extends DefaultedEntityGeoModel<T> {

	public FleshPigModel() {
		super(BiomancyMod.createRL("mob/flesh_pig"), true);
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
