package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshCow;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshCowModel<T extends FleshCow> extends DefaultedEntityGeoModel<T> {

	public FleshCowModel() {
		super(BiomancyMod.createRL("mob/flesh_cow"), true);
	}

}
