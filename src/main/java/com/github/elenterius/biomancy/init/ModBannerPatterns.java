package com.github.elenterius.biomancy.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public final class ModBannerPatterns {

	public static final TagKey<BannerPattern> MASCOT_PATTERN = create("pattern_item/mascot");
	public static final TagKey<BannerPattern> MASCOT_ACCENT_PATTERN = create("pattern_item/mascot_accent");
	public static final TagKey<BannerPattern> MASCOT_OUTLINE_PATTERN = create("pattern_item/mascot_outline");

	private ModBannerPatterns() {}

	public static void register() {
		//forces initialization of static fields
	}

	private static TagKey<BannerPattern> create(String name) {
		return TagKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(name));
	}

}
