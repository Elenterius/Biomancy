package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DevArmCannonModel extends AnimatedGeoModel<DevArmCannonItem> {

	@Override
	public ResourceLocation getModelResource(DevArmCannonItem item) {
		return BiomancyMod.createRL("geo/item/arm_cannon.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DevArmCannonItem item) {
		return BiomancyMod.createRL("textures/item/weapon/arm_cannon.png");
	}

	@Override
	public ResourceLocation getAnimationResource(DevArmCannonItem item) {
		return BiomancyMod.createRL("animations/item/arm_cannon.animation.json");
	}

}
