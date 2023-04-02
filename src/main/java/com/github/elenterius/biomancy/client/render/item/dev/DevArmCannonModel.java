package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DevArmCannonModel extends AnimatedGeoModel<DevArmCannonItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/arm_cannon.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/weapon/arm_cannon.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/arm_cannon.animation.json");

	@Override
	public ResourceLocation getModelResource(DevArmCannonItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(DevArmCannonItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(DevArmCannonItem item) {
		return ANIMATION;
	}

}
