package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModMobEffectTagsProvider extends IntrinsicHolderTagsProvider<MobEffect> {

	public ModMobEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.MOB_EFFECT, lookupProvider, mobEffect -> ForgeRegistries.MOB_EFFECTS.getDelegateOrThrow(mobEffect).key(), BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(ModMobEffectTags.NOT_REMOVABLE_WITH_CLEANSING_SERUM).add(
				ModMobEffects.ESSENCE_ANEMIA.get()
		);

		tag(ModMobEffectTags.CRADLE_LIFE_ENERGY_SOURCE).add(
				MobEffects.HEAL,
				MobEffects.REGENERATION,
				MobEffects.HEALTH_BOOST,
				MobEffects.ABSORPTION
		);

		tag(ModMobEffectTags.CRADLE_DISEASE_SOURCE).add(
				MobEffects.WEAKNESS,
				MobEffects.WITHER,
				MobEffects.POISON,
				ModMobEffects.BLEED.get()
		);

		tag(ModMobEffectTags.CRADLE_SUCCESS_SOURCE).add(
				MobEffects.LUCK,
				MobEffects.SATURATION,
				ModMobEffects.LIBIDO.get()
		);

		tag(ModMobEffectTags.CRADLE_HOSTILITY_SOURCE).add(
				MobEffects.HUNGER,
				MobEffects.CONFUSION,
				MobEffects.BLINDNESS,
				MobEffects.HARM,
				MobEffects.WITHER,
				MobEffects.POISON,
				ModMobEffects.BLEED.get()
		);

		tag(ModMobEffectTags.CRADLE_ANOMALY_SOURCE).add(
				MobEffects.BAD_OMEN,
				MobEffects.DARKNESS,
				ModMobEffects.CORROSIVE.get()
		);
	}

}
