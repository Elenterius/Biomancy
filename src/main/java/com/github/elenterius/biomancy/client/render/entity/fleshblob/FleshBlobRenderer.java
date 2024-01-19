package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FleshBlobRenderer extends AbstractFleshBlobRenderer<EaterFleshBlob> {

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>());
	}

}
