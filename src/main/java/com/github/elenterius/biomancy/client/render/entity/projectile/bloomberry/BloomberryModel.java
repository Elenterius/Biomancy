package com.github.elenterius.biomancy.client.render.entity.projectile.bloomberry;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.BloomberryProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BloomberryModel extends DefaultedEntityGeoModel<BloomberryProjectile> {

	public BloomberryModel() {
		super(BiomancyMod.createRL("projectile/bloomberry"));
	}

	@Override
	public RenderType getRenderType(BloomberryProjectile animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
