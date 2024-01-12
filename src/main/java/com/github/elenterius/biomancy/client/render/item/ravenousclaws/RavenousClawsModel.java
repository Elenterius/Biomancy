package com.github.elenterius.biomancy.client.render.item.ravenousclaws;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import net.minecraft.resources.ResourceLocation;

public class RavenousClawsModel extends GeckolibModel<RavenousClawsItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/ravenous_claws.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/weapon/ravenous_claws.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/ravenous_claws.animation.json");

	@Override
	public ResourceLocation getModelResource(RavenousClawsItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(RavenousClawsItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(RavenousClawsItem item) {
		return ANIMATION;
	}

}
