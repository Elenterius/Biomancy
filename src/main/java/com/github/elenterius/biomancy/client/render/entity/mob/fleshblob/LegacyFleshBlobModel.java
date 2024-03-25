package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.fleshblob.EaterFleshBlob;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;

import static com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.FleshBlobModel.TROLL_TEXTURE;

public class LegacyFleshBlobModel<T extends EaterFleshBlob> extends GeoModel<T> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_legacy.png");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return FleshBlobModel.MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(T fleshBlob) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(T fleshBlob) {
		return FleshBlobModel.ANIMATION;
	}

	@Override
	public RenderType getRenderType(T fleshBlob, ResourceLocation texture) {
		if (BiomancyMod.WE_DO_A_LITTLE_FOOLING) return ModRenderTypes.getCutoutPartyTime(TROLL_TEXTURE);
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