package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public final class ModMobEffectTags {

	public static final TagKey<MobEffect> NOT_REMOVABLE_WITH_CLEANSING_SERUM = tag("not_removable_with_cleansing_serum");

	public static final TagKey<MobEffect> CRADLE_LIFE_ENERGY_SOURCE = tag("cradle/life_energy_sources");
	public static final TagKey<MobEffect> CRADLE_DISEASE_SOURCE = tag("cradle/disease_sources");
	public static final TagKey<MobEffect> CRADLE_SUCCESS_SOURCE = tag("cradle/success_sources");
	public static final TagKey<MobEffect> CRADLE_HOSTILITY_SOURCE = tag("cradle/hostility_sources");
	public static final TagKey<MobEffect> CRADLE_ANOMALY_SOURCE = tag("cradle/anomaly_sources");

	private ModMobEffectTags() {}

	public static boolean isNotRemovableWithCleansingSerum(MobEffect mobEffect) {
		return getTag(NOT_REMOVABLE_WITH_CLEANSING_SERUM).contains(mobEffect);
	}

	public static boolean isCradleLifeEnergySource(MobEffect mobEffect) {
		return getTag(CRADLE_LIFE_ENERGY_SOURCE).contains(mobEffect);
	}

	public static boolean isCradleDiseaseSource(MobEffect mobEffect) {
		return getTag(CRADLE_DISEASE_SOURCE).contains(mobEffect);
	}

	public static boolean isCradleSuccessSource(MobEffect mobEffect) {
		return getTag(CRADLE_DISEASE_SOURCE).contains(mobEffect);
	}

	public static boolean isCradleHostilitySource(MobEffect mobEffect) {
		return getTag(CRADLE_HOSTILITY_SOURCE).contains(mobEffect);
	}

	public static boolean isCradleAnomalySource(MobEffect mobEffect) {
		return getTag(CRADLE_ANOMALY_SOURCE).contains(mobEffect);
	}

	public static ITag<MobEffect> getTag(TagKey<MobEffect> tagKey) {
		//noinspection DataFlowIssue
		return ForgeRegistries.MOB_EFFECTS.tags().getTag(tagKey);
	}

	private static TagKey<MobEffect> tag(String name) {
		return createTag(BiomancyMod.createRL(name));
	}

	private static TagKey<MobEffect> forgeTag(String name) {
		return createTag(new ResourceLocation("forge", name));
	}

	private static TagKey<MobEffect> createTag(ResourceLocation key) {
		return TagKey.create(Registries.MOB_EFFECT, key);
	}

}
