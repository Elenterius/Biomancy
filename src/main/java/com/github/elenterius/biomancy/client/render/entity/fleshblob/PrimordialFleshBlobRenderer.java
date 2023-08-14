package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PrimordialFleshBlobRenderer<T extends EaterFleshBlob & PrimordialFleshkin> extends AbstractFleshBlobRenderer<T> {

	public PrimordialFleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new PrimordialFleshBlobModel<>());
	}

}
