package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.BileSpitterItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BileSpitterModel extends DefaultedItemGeoModel<BileSpitterItem> {

	public BileSpitterModel() {
		super(BiomancyMod.createRL("weapon/bile_spitter"));
	}

	@Override
	public RenderType getRenderType(BileSpitterItem animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
