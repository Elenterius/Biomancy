package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBannerPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBannerPatternTagsProvider extends IntrinsicHolderTagsProvider<BannerPattern> {

	public ModBannerPatternTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BANNER_PATTERN, lookupProvider, bannerPattern -> BannerPattern.byHash(bannerPattern.getHashname()).unwrapKey().get(), BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModBannerPatterns.TAG_MASCOT).add(
				ModBannerPatterns.MASCOT_BASE.get(),
				ModBannerPatterns.MASCOT_ACCENT.get(),
				ModBannerPatterns.MASCOT_OUTLINE.get()
		);
	}

}
