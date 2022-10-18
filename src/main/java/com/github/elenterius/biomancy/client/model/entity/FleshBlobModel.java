package com.github.elenterius.biomancy.client.model.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.time.ZonedDateTime;
import java.util.Locale;

public class FleshBlobModel<T extends FleshBlob> extends AnimatedGeoModel<T> {

	protected static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
			BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_neutral.png"),
			BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_hostile.png"),
	};
	protected static final ResourceLocation LEGACY_TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_legacy.png");
	protected static final ResourceLocation CLOWN_TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_clown.png");
	protected static final ResourceLocation WATCHER_TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_watcher.png");
	private final boolean isClownDay;

	public FleshBlobModel() {
		ZonedDateTime now = ZonedDateTime.now();
		isClownDay = now.getMonthValue() == 11 && now.getDayOfMonth() == 11;
	}

	@Override
	public ResourceLocation getModelLocation(FleshBlob fleshBlob) {
		return BiomancyMod.createRL("geo/entity/flesh_blob.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(FleshBlob fleshBlob) {
		if (isClownDay) {
			return CLOWN_TEXTURE;
		}

		if (fleshBlob.hasCustomName()) {
			Component customName = fleshBlob.getCustomName();
			if (customName != null) {
				String name = customName.getContents().toLowerCase(Locale.ROOT);
				if (name.contains("happy")) {
					return LEGACY_TEXTURE;
				}
				if (name.contains("clown")) {
					return CLOWN_TEXTURE;
				}
				if (name.contains("beholder")) {
					return WATCHER_TEXTURE;
				}
			}
		}

		return TEXTURES[fleshBlob.getBlobType().textureIndex];
	}

	@Override
	public ResourceLocation getAnimationFileLocation(FleshBlob fleshBlob) {
		return BiomancyMod.createRL("animations/entity/fleshkin_blob.animation.json");
	}

}