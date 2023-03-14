package com.github.elenterius.biomancy.client.render.item.longclaws;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.LivingLongClawsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LongClawsModel extends AnimatedGeoModel<LivingLongClawsItem> {

	@Override
	public ResourceLocation getModelLocation(LivingLongClawsItem item) {
		return BiomancyMod.createRL("geo/item/long_claws.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(LivingLongClawsItem item) {
		return BiomancyMod.createRL("textures/item/weapon/long_claws.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(LivingLongClawsItem item) {
		return BiomancyMod.createRL("animations/item/long_claws.animation.json");
	}

}
