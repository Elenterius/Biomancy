package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		createTag(ModEntityTags.NOT_CLONEABLE)
				.addTag(ModEntityTags.FORGE_BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional("strawgolem:strawgolem", "strawgolem:strawnggolem");

		createTag(ModEntityTags.FLESHKIN).add(
				ModEntityTypes.FLESH_BLOB.get(), ModEntityTypes.HUNGRY_FLESH_BLOB.get(), ModEntityTypes.LEGACY_FLESH_BLOB.get(),
				ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get()
		);
	}

	protected EnhancedTagAppender<EntityType<?>> createTag(TagKey<EntityType<?>> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ENTITY_TYPES);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
