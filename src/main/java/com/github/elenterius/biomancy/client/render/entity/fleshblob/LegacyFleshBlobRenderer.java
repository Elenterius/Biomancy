package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LegacyFleshBlobRenderer<T extends EaterFleshBlob> extends AbstractFleshBlobRenderer<T> {

	public LegacyFleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new LegacyFleshBlobModel<>());
	}

}
