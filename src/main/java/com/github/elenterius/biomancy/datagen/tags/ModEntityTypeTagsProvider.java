package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ModEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		//noinspection SpellCheckingInspection
		tag(ModTags.EntityTypes.NOT_CLONEABLE)
				.addTag(ModTags.EntityTypes.BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional(new ResourceLocation("strawgolem", "strawgolem"))
				.addOptional(new ResourceLocation("strawgolem", "strawnggolem"));

		tag(ModTags.EntityTypes.SHARP_TEETH)
				.add(EntityType.CAT, EntityType.OCELOT, EntityType.WOLF, EntityType.FOX, EntityType.DOLPHIN, EntityType.POLAR_BEAR, EntityType.PANDA,
						EntityType.BAT, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.ENDER_DRAGON);

	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
