package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModEntityTags {

	public static final TagKey<EntityType<?>> C_CAPTURING_NOT_SUPPORTED = conventionalTag("capturing_not_supported");
	public static final TagKey<EntityType<?>> C_BOSSES = conventionalTag("bosses");
	public static final TagKey<EntityType<?>> C_GOLEMS = conventionalTag("golems");

	public static final TagKey<EntityType<?>> NOT_CLONEABLE = tag("not_cloneable");
	public static final TagKey<EntityType<?>> CAPTURING_BY_CHRYSALIS_NOT_ALLOWED = tag("capturing_by_chrysalis_not_allowed");
	public static final TagKey<EntityType<?>> RESIZING_NOT_ALLOWED = tag("resizing_not_allowed");

	public static final TagKey<EntityType<?>> FLESHKIN = tag("fleshkin");
	public static final TagKey<EntityType<?>> FLESHKIN_IGNORES = tag("fleshkin_ignores");

	private ModEntityTags() {}

	private static TagKey<EntityType<?>> tag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, BiomancyMod.rl(name));
	}

	private static TagKey<EntityType<?>> forgeTag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", name));
	}

	/// conventional tags introduced in neo-forge/fabric for minecraft 1.21+
	private static TagKey<EntityType<?>> conventionalTag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("c", name));
	}

}
