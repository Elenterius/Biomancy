package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModBannerPatterns {

	public static final DeferredRegister<BannerPattern> BANNERS = DeferredRegister.create(Registries.BANNER_PATTERN, BiomancyMod.MOD_ID);
	public static final RegistryObject<BannerPattern> MASCOT_BASE = register("mascot_base");
	public static final RegistryObject<BannerPattern> MASCOT_ACCENT = register("mascot_accent");
	public static final RegistryObject<BannerPattern> MASCOT_OUTLINE = register("mascot_outline");
	public static final TagKey<BannerPattern> TAG_MASCOT = createTagKey("mascot");

	private ModBannerPatterns() {}

	private static RegistryObject<BannerPattern> register(String name) {
		return BANNERS.register(name, () -> new BannerPattern(BiomancyMod.createRLString(name)));
	}

	private static TagKey<BannerPattern> createTagKey(String name) {
		return TagKey.create(Registries.BANNER_PATTERN, BiomancyMod.createRL("pattern_item/" + name));
	}

	private static TagKey<BannerPattern> createTagKey(RegistryObject<BannerPattern> registryObject) {
		ResourceLocation registryName = registryObject.getId();
		String modId = registryName.getNamespace();
		String name = registryName.getPath();
		return TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(modId, "pattern_item/" + name));
	}

}
