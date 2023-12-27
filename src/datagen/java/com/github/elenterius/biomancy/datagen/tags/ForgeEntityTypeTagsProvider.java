package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ForgeEntityTypeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, "forge", existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ModEntityTags.FORGE_BOSSES)
				.add(EntityType.WITHER, EntityType.ENDER_DRAGON);

		createTag(ModEntityTags.FORGE_GOLEMS)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional("strawgolem:strawgolem", "strawgolem:strawnggolem");
	}

	protected EnhancedTagAppender<EntityType<?>> createTag(TagKey<EntityType<?>> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ENTITY_TYPES);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
