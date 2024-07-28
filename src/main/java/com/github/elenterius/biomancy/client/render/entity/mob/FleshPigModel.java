package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshPig;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshPigModel<T extends FleshPig> extends DefaultedEntityGeoModel<T> {

	public FleshPigModel() {
		super(BiomancyMod.createRL("mob/flesh_pig"), true);
	}

}
