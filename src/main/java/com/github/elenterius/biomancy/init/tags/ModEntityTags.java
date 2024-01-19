package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;

public final class ModEntityTags {

	public static final TagKey<EntityType<?>> FORGE_BOSSES = Tags.EntityTypes.BOSSES;
	public static final TagKey<EntityType<?>> FORGE_GOLEMS = forgeTag("golems");
	public static final TagKey<EntityType<?>> NOT_CLONEABLE = tag("not_cloneable");

	public static final TagKey<EntityType<?>> FLESHKIN = tag("fleshkin");

	//Special Mob Loot
	public static final TagKey<EntityType<?>> SHARP_FANG = tag("sharp_fang");
	public static final TagKey<EntityType<?>> SHARP_CLAW = tag("sharp_claw");
	public static final TagKey<EntityType<?>> SINEW = tag("sinew");
	public static final TagKey<EntityType<?>> TOXIN_GLAND = tag("toxin_gland");
	public static final TagKey<EntityType<?>> VOLATILE_GLAND = tag("volatile_gland");
	public static final TagKey<EntityType<?>> BILE_GLAND = tag("bile_gland");
	public static final TagKey<EntityType<?>> BONE_MARROW = tag("bone_marrow");
	public static final TagKey<EntityType<?>> WITHERED_BONE_MARROW = tag("withered_bone_marrow");

	private ModEntityTags() {}

	private static TagKey<EntityType<?>> tag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, BiomancyMod.createRL(name));
	}

	private static TagKey<EntityType<?>> forgeTag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", name));
	}

}
