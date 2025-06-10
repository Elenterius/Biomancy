package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPaintingTagsProvider extends IntrinsicHolderTagsProvider<PaintingVariant> {

	public ModPaintingTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.PAINTING_VARIANT, lookupProvider, variant -> ForgeRegistries.PAINTING_VARIANTS.getDelegateOrThrow(variant).key(), BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(PaintingVariantTags.PLACEABLE)
				.add(ModPaintings.JERRY_PROVIDES.get());
	}

}
