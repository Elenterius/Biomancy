package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.gun.DevArmCannonItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevArmCannonModel extends DefaultedItemGeoModel<DevArmCannonItem> {

	public DevArmCannonModel() {
		super(BiomancyMod.createRL("weapon/arm_cannon"));
	}

	@Override
	public RenderType getRenderType(DevArmCannonItem animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
