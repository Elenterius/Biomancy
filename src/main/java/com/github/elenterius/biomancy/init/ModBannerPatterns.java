package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Locale;

public final class ModBannerPatterns {

	public static final BannerPattern EYEBALL_PATTERN = createBannerPattern("eyeball");

	private ModBannerPatterns() {}

	public static void register() {
		//forces initialization of static fields
	}

	private static BannerPattern createBannerPattern(String name) {
		String pattern = BiomancyMod.MOD_ID + "_" + name;
		return BannerPattern.create(pattern.toUpperCase(Locale.ROOT), pattern, pattern, true);
	}

}
