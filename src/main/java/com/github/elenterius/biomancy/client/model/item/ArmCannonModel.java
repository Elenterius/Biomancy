package com.github.elenterius.biomancy.client.model.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.ArmCannonItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ArmCannonModel extends AnimatedGeoModel<ArmCannonItem> {

	@Override
	public ResourceLocation getModelLocation(ArmCannonItem item) {
		return BiomancyMod.createRL("geo/item/arm_cannon.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(ArmCannonItem item) {
		return BiomancyMod.createRL("textures/item/weapon/arm_cannon.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(ArmCannonItem item) {
		return BiomancyMod.createRL("animations/item/arm_cannon.animation.json");
	}

}
