package com.github.elenterius.biomancy.client.render.item.caustic_gunblade;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.gun.CausticGunbladeItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CausticGunbladeModel extends DefaultedItemGeoModel<CausticGunbladeItem> {

	public CausticGunbladeModel() {
		super(BiomancyMod.createRL("weapon/caustic_gunblade"));
	}

	@Override
	public RenderType getRenderType(CausticGunbladeItem animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
