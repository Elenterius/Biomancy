package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ForgeEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, "forge", existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
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
