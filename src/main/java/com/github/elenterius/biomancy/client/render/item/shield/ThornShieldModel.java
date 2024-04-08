package com.github.elenterius.biomancy.client.render.item.shield;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.shield.ThornShieldItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ThornShieldModel extends DefaultedItemGeoModel<ThornShieldItem> {

	public ThornShieldModel() {
		super(BiomancyMod.createRL("shield/thorn_shield"));
	}

	@Override
	public RenderType getRenderType(ThornShieldItem animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
