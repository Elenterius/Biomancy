package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Locale;

public final class ModBannerPatterns {

	public static final BannerPattern MASCOT_PATTERN = createBannerPattern("mascot");
	public static final BannerPattern MASCOT_ACCENT_PATTERN = createBannerPattern("mascot_accent");
	public static final BannerPattern MASCOT_OUTLINE_PATTERN = createBannerPattern("mascot_outline");

	private ModBannerPatterns() {}

	public static void register() {
		//forces initialization of static fields
	}

	private static BannerPattern createBannerPattern(String name) {
		String pattern = BiomancyMod.MOD_ID + "_" + name;
		return BannerPattern.create(pattern.toUpperCase(Locale.ENGLISH), pattern, pattern, true);
	}

}
