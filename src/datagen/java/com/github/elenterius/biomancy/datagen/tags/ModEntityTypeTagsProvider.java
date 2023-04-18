package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public final Logger LOGGER = BiomancyMod.LOGGER;
	public final Marker logMarker = MarkerManager.getMarker("ModEntityTypeTagsProvider");

	public ModEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		LOGGER.info(logMarker, "registering entity type tags...");

		//noinspection SpellCheckingInspection
		tag(ModTags.EntityTypes.NOT_CLONEABLE)
				.addTag(ModTags.EntityTypes.BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional(new ResourceLocation("strawgolem", "strawgolem"))
				.addOptional(new ResourceLocation("strawgolem", "strawnggolem"));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
