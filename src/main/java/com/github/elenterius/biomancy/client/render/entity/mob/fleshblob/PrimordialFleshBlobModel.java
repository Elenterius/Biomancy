package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.mob.fleshblob.EaterFleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.PrimordialHangryEaterFleshBlob;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;

public class PrimordialFleshBlobModel<T extends EaterFleshBlob & PrimordialFleshkin> extends GeoModel<T> {

	protected static final ResourceLocation BASE_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/primordial_flesh_blob_neutral.png");
	protected static final ResourceLocation HUNGRY_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/primordial_flesh_blob_hostile.png");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return FleshBlobModel.MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(T fleshBlob) {
		return fleshBlob instanceof PrimordialHangryEaterFleshBlob ? HUNGRY_TEXTURE : BASE_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(T fleshBlob) {
		return FleshBlobModel.ANIMATION;
	}

	@Override
	public RenderType getRenderType(T fleshBlob, ResourceLocation texture) {
		if (fleshBlob.hasCustomName()) {
			Component customName = fleshBlob.getCustomName();
			if (customName != null) {
				String name = customName.getContents().toString().toLowerCase(Locale.ENGLISH);
				if (name.contains("party_blob")) {
					return ModRenderTypes.getCutoutPartyTime(texture);
				}
			}
		}
		return super.getRenderType(fleshBlob, texture);
	}

}