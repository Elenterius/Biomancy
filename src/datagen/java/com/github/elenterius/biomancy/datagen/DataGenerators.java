package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.advancements.ModAdvancementProvider;
import com.github.elenterius.biomancy.datagen.loot.ModGlobalLootModifierProvider;
import com.github.elenterius.biomancy.datagen.loot.ModLootTableProvider;
import com.github.elenterius.biomancy.datagen.models.ModBlockStateProvider;
import com.github.elenterius.biomancy.datagen.models.ModItemModelProvider;
import com.github.elenterius.biomancy.datagen.modonomicon.GuideBookProvider;
import com.github.elenterius.biomancy.datagen.recipes.ModRecipeProvider;
import com.github.elenterius.biomancy.datagen.tags.*;
import com.github.elenterius.biomancy.datagen.translations.EnglishTranslationProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	private DataGenerators() {}

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		//tags
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(generator, existingFileHelper);
		generator.addProvider(true, blockTags);
		generator.addProvider(true, new ModItemTagsProvider(generator, blockTags, existingFileHelper));
		generator.addProvider(true, new ForgeEntityTypeTagsProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModEntityTypeTagsProvider(generator, existingFileHelper));

		generator.addProvider(true, new ModBannerPatternTagsProvider(generator, existingFileHelper));

		//recipes
		generator.addProvider(true, new ModRecipeProvider(generator));

		//loot
		generator.addProvider(true, new ModLootTableProvider(generator));
		generator.addProvider(true, new ModGlobalLootModifierProvider(generator));

		//models & block states
		generator.addProvider(true, new ModBlockStateProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModItemModelProvider(generator, existingFileHelper));

		//sounds
		generator.addProvider(true, new ModSoundProvider(generator, existingFileHelper));

		//translations
		EnglishTranslationProvider translationProvider = new EnglishTranslationProvider(generator);

		//advancements
		generator.addProvider(true, new ModAdvancementProvider(generator, existingFileHelper, translationProvider));

		//guide book
		generator.addProvider(true, new GuideBookProvider(generator, translationProvider));

		generator.addProvider(true, translationProvider);
	}

}