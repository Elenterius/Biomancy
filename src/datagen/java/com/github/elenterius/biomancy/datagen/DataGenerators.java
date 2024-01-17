package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.advancements.ModAdvancementProvider;
import com.github.elenterius.biomancy.datagen.lang.EnglishLangProvider;
import com.github.elenterius.biomancy.datagen.loot.ModGlobalLootModifierProvider;
import com.github.elenterius.biomancy.datagen.loot.ModLootTableProvider;
import com.github.elenterius.biomancy.datagen.models.ModBlockStateProvider;
import com.github.elenterius.biomancy.datagen.models.ModItemModelProvider;
import com.github.elenterius.biomancy.datagen.modonomicon.GuideBookProvider;
import com.github.elenterius.biomancy.datagen.particles.ModParticleSpriteProvider;
import com.github.elenterius.biomancy.datagen.recipes.ModRecipeProvider;
import com.github.elenterius.biomancy.datagen.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	private DataGenerators() {}

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		//tags
		ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);
		generator.addProvider(event.includeServer(), new ModItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		generator.addProvider(event.includeServer(), new ForgeEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new ModEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));

		generator.addProvider(event.includeServer(), new ModBannerPatternTagsProvider(packOutput, lookupProvider, existingFileHelper));

		//recipes
		generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));

		//loot
		generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));
		generator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput));

		//models & block states
		generator.addProvider(event.includeServer(), new ModBlockStateProvider(packOutput, existingFileHelper));
		generator.addProvider(event.includeServer(), new ModItemModelProvider(packOutput, existingFileHelper));

		//sounds
		generator.addProvider(event.includeClient(), new ModSoundProvider(packOutput, existingFileHelper));

		//particles
		generator.addProvider(event.includeServer(), new ModParticleSpriteProvider(packOutput, existingFileHelper));

		//translations
		EnglishLangProvider translationProvider = new EnglishLangProvider(packOutput);

		//advancements
		generator.addProvider(event.includeServer(), new ModAdvancementProvider(packOutput, lookupProvider, existingFileHelper, translationProvider));

		//guide book
		generator.addProvider(event.includeServer(), new GuideBookProvider(packOutput, translationProvider));

		generator.addProvider(event.includeServer(), translationProvider);
	}

}