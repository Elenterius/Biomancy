package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.entity.mob.fleshblob.EaterFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FleshBlobRenderer extends AbstractFleshBlobRenderer<EaterFleshBlob> {

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>());
	}

}
