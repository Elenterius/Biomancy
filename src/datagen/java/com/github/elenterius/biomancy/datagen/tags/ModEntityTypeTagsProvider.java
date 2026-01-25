package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.mcreator.sonsofsins.SonsOfSinsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static TagKey<EntityType<?>> forgeTag(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", path));
	}

	private static TagKey<EntityType<?>> conventionalTag(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("c", path));
	}

	protected EnhancedTagAppender<EntityType<?>> createTag(TagKey<EntityType<?>> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ENTITY_TYPES);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		addBiomancyTags();
		addMinecraftTags();
		addForgeTags();
		addConventionalTags();
	}

	private void addBiomancyTags() {
		createTag(ModEntityTags.NOT_CLONEABLE)
				.addTag(ModEntityTags.C_BOSSES)
				.addTag(ModEntityTags.C_GOLEMS);

		createTag(ModEntityTags.CAPTURING_BY_CHRYSALIS_NOT_ALLOWED)
				.addTag(ModEntityTags.C_BOSSES)
				.add(EntityType.WARDEN, EntityType.ELDER_GUARDIAN, EntityType.GIANT)
				.addOptional(ACEntityRegistry.GUM_WORM, ACEntityRegistry.TREMORZILLA, ACEntityRegistry.HULLBREAKER, ACEntityRegistry.FORSAKEN)
				.addOptional(AMEntityRegistry.VOID_WORM);

		createTag(ModEntityTags.RESIZING_NOT_ALLOWED);

		createTag(ModEntityTags.FLESHKIN).add(
				ModEntityTypes.FLESH_BLOB.get(), ModEntityTypes.HUNGRY_FLESH_BLOB.get(), ModEntityTypes.LEGACY_FLESH_BLOB.get(),
				ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get(),
				ModEntityTypes.FLESH_COW.get(), ModEntityTypes.FLESH_SHEEP.get(), ModEntityTypes.FLESH_PIG.get(), ModEntityTypes.FLESH_CHICKEN.get()
		);

		createTag(ModEntityTags.FLESHKIN_IGNORES)
				.addTag(
						ModEntityTags.FLESHKIN
				)
				.addOptionalTag(SonsOfSinsMod.MODID + ":is_a_flesh_creature");

		tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(
				ModEntityTypes.FLESH_CHICKEN.get()
		);

		tag(EntityTypeTags.DISMOUNTS_UNDERWATER).add(
				ModEntityTypes.FLESH_CHICKEN.get()
		);

		tag(ACTagRegistry.RESISTS_ACID).add(
				ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get(),
				ModEntityTypes.FLESH_CHICKEN.get()
		);
	}

	private void addMinecraftTags() {

	}

	private void addForgeTags() {

	}

	private void addConventionalTags() {
		tag(ModEntityTags.C_CAPTURING_NOT_SUPPORTED)
				.addOptionalTag(forgeTag("capturing_not_supported"));

		createTag(ModEntityTags.C_BOSSES)
				.addTag(Tags.EntityTypes.BOSSES)
				.add(EntityType.WITHER, EntityType.ENDER_DRAGON); //TODO: remove this line when porting to minecraft 1.21+

		createTag(ModEntityTags.C_GOLEMS)
				.addTag(forgeTag("golems"))
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional("strawgolem:strawgolem", "strawgolem:strawnggolem");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
