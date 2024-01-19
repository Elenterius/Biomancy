package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypeTags {

	public static final TagKey<DamageType> FORGE_IS_ACID = forgeTag("is_acid");

	/*
	public static final TagKey<DamageType> IS_CORROSIVE = tag("is_corrosive");
	public static final TagKey<DamageType> IS_VIRAL = tag("is_viral");
	public static final TagKey<DamageType> IS_TOXIN = tag("is_toxin");
	public static final TagKey<DamageType> IS_HEAT = tag("is_heat");
	public static final TagKey<DamageType> IS_FROST = tag("is_frost");
	public static final TagKey<DamageType> IS_BLAST = tag("is_blast");
	public static final TagKey<DamageType> IS_GAS = tag("is_gas");
	public static final TagKey<DamageType> IS_IMPACT = tag("is_impact");
	public static final TagKey<DamageType> IS_SLASH = tag("is_slash");
	public static final TagKey<DamageType> IS_PIERCE = tag("is_pierce");
	*/

	private ModDamageTypeTags() {}

	private static TagKey<DamageType> tag(String name) {
		return TagKey.create(Registries.DAMAGE_TYPE, BiomancyMod.createRL(name));
	}

	private static TagKey<DamageType> forgeTag(String name) {
		return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("forge", name));
	}

}
