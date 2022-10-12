package com.github.elenterius.biomancy.client.model.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.BileSpitterItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BileSpitterModel extends AnimatedGeoModel<BileSpitterItem> {

	@Override
	public ResourceLocation getModelLocation(BileSpitterItem item) {
		return BiomancyMod.createRL("geo/item/bile_spitter.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BileSpitterItem item) {
		return BiomancyMod.createRL("textures/item/weapon/bile_spitter.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(BileSpitterItem item) {
		return BiomancyMod.createRL("animations/item/bile_spitter.animation.json");
	}

}
