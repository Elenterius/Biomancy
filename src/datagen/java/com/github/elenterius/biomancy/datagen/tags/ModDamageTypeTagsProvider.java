package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModDamageTypes;
import com.github.elenterius.biomancy.init.tags.ModDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {

	public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).add(
				ModDamageTypes.PRIMORDIAL_SPIKES,
				ModDamageTypes.CHEST_BITE,
				ModDamageTypes.BLEED,
				ModDamageTypes.FALL_ON_SPIKE,
				ModDamageTypes.IMPALED_BY_SPIKE
		);

		tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(
				ModDamageTypes.BLEED
		);

		tag(DamageTypeTags.BYPASSES_RESISTANCE).add(
				ModDamageTypes.BLEED
		);

		tag(DamageTypeTags.BYPASSES_EFFECTS).add(
				ModDamageTypes.BLEED
		);

		//damage that ignores Invincibility-Frames
		tag(DamageTypeTags.BYPASSES_COOLDOWN).add(
				ModDamageTypes.BLEED,
				ModDamageTypes.CORROSIVE_ACID
		);

		tag(DamageTypeTags.NO_IMPACT).add(
				ModDamageTypes.BLEED,
				ModDamageTypes.CORROSIVE_ACID
		);

		tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).add(
				ModDamageTypes.BLEED,
				ModDamageTypes.CORROSIVE_ACID
		);

		tag(DamageTypeTags.IS_FALL).add(
				ModDamageTypes.FALL_ON_SPIKE
		);

		tag(DamageTypeTags.IS_PROJECTILE).add(
				ModDamageTypes.TOOTH_PROJECTILE
		);

		tag(ModDamageTypeTags.FORGE_IS_ACID).add(
				ModDamageTypes.CORROSIVE_ACID
		);
	}

}
