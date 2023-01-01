package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBannerPatterns;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ModBannerPatternTagsProvider extends TagsProvider<BannerPattern> {

	public ModBannerPatternTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, Registry.BANNER_PATTERN, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags() {
		tag(ModBannerPatterns.TAG_MASCOT).add(
				ModBannerPatterns.MASCOT_BASE.get(),
				ModBannerPatterns.MASCOT_ACCENT.get(),
				ModBannerPatterns.MASCOT_OUTLINE.get()
		);
	}

}
