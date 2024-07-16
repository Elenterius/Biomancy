package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.fleshblob.AdulteratedHangryEaterFleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.EaterFleshBlob;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;

public class FleshBlobModel<T extends EaterFleshBlob> extends GeoModel<T> {

	protected static final ResourceLocation BASE_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_neutral.png");
	protected static final ResourceLocation HUNGRY_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_hostile.png");
	protected static final ResourceLocation CLOWN_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_clown.png");
	protected static final ResourceLocation TROLL_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_troll.png");
	protected static final ResourceLocation WATCHER_TEXTURE = BiomancyMod.createRL("textures/entity/mob/flesh_blob/flesh_blob_watcher.png");

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/mob/flesh_blob.geo.json");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/entity/mob/flesh_blob.animation.json");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(T fleshBlob) {
		if (BiomancyMod.EVENT_CALENDAR.isCarnivalStart()) return CLOWN_TEXTURE;

		if (fleshBlob.hasCustomName()) {
			String name = fleshBlob.getName().getString().toLowerCase(Locale.ENGLISH);
			if (name.equals("trololo") || name.equals("u mad bro?")) return TROLL_TEXTURE;
			if (name.contains("krusty")) return CLOWN_TEXTURE;
			if (name.contains("beholder") || name.contains("observer")) return WATCHER_TEXTURE;
		}

		return fleshBlob instanceof AdulteratedHangryEaterFleshBlob ? HUNGRY_TEXTURE : BASE_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(T fleshBlob) {
		return ANIMATION;
	}

	@Override
	public RenderType getRenderType(T fleshBlob, ResourceLocation texture) {
		if (BiomancyMod.EVENT_CALENDAR.isAprilFools()) return ModRenderTypes.getCutoutPartyTime(TROLL_TEXTURE);

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