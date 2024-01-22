package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.entity.mob.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.mob.fleshblob.EaterFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PrimordialFleshBlobRenderer<T extends EaterFleshBlob & PrimordialFleshkin> extends AbstractFleshBlobRenderer<T> {

	public PrimordialFleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new PrimordialFleshBlobModel<>());
	}

}
