package com.github.elenterius.biomancy.client.render.item.longclaws;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.LivingLongClawsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LongClawsModel extends AnimatedGeoModel<LivingLongClawsItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/long_claws.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/weapon/long_claws.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/long_claws.animation.json");

	@Override
	public ResourceLocation getModelResource(LivingLongClawsItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(LivingLongClawsItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(LivingLongClawsItem item) {
		return ANIMATION;
	}

}
