package com.github.elenterius.biomancy.client.model.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.LongClawsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LongClawsModel extends AnimatedGeoModel<LongClawsItem> {

	@Override
	public ResourceLocation getModelLocation(LongClawsItem item) {
		return BiomancyMod.createRL("geo/item/long_claws.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(LongClawsItem item) {
		return BiomancyMod.createRL("textures/item/weapon/long_claws.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(LongClawsItem item) {
		return BiomancyMod.createRL("animations/item/long_claws.animation.json");
	}

}
