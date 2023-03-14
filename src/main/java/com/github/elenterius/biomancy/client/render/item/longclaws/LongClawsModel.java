package com.github.elenterius.biomancy.client.render.item.longclaws;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.LivingLongClawsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LongClawsModel extends AnimatedGeoModel<LivingLongClawsItem> {

	@Override
	public ResourceLocation getModelResource(LivingLongClawsItem item) {
		return BiomancyMod.createRL("geo/item/long_claws.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingLongClawsItem item) {
		return BiomancyMod.createRL("textures/item/weapon/long_claws.png");
	}

	@Override
	public ResourceLocation getAnimationResource(LivingLongClawsItem item) {
		return BiomancyMod.createRL("animations/item/long_claws.animation.json");
	}

}
