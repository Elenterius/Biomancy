package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.fleshblob.MalignantFleshBlob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MalignantFleshBlobRenderer extends AbstractFleshBlobRenderer<MalignantFleshBlob> {

	public MalignantFleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new MalignantFleshBlobModel<>());
	}

}
