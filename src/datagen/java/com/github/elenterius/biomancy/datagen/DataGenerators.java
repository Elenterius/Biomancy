package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.advancements.ModAdvancementProvider;
import com.github.elenterius.biomancy.datagen.lang.EnglishLangProvider;
import com.github.elenterius.biomancy.datagen.lang.PirateLangProvider;
import com.github.elenterius.biomancy.datagen.loot.ModGlobalLootModifierProvider;
import com.github.elenterius.biomancy.datagen.loot.ModLootTableProvider;
import com.github.elenterius.biomancy.datagen.models.ModBlockStateProvider;
import com.github.elenterius.biomancy.datagen.models.ModItemModelProvider;
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
		boolean includeServer = event.includeServer();
		boolean includeClient = event.includeClient();

		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		DatapackEntriesProvider datapackEntriesProvider = new DatapackEntriesProvider(packOutput, lookupProvider);
		lookupProvider = datapackEntriesProvider.getRegistryProvider();
		generator.addProvider(includeServer, datapackEntriesProvider);

		//tags
		ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(includeServer, blockTagsProvider);
		generator.addProvider(includeServer, new ModItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		generator.addProvider(includeServer, new ForgeEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(includeServer, new ModEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(includeServer, new ModBannerPatternTagsProvider(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(includeServer, new ModDamageTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(includeServer, new ModMobEffectTagsProvider(packOutput, lookupProvider, existingFileHelper));

		//recipes
		generator.addProvider(includeServer, new ModRecipeProvider(packOutput));

		//loot
		generator.addProvider(includeServer, new ModLootTableProvider(packOutput));
		generator.addProvider(includeServer, new ModGlobalLootModifierProvider(packOutput));

		//models & block states
		generator.addProvider(includeServer, new ModBlockStateProvider(packOutput, existingFileHelper));
		generator.addProvider(includeServer, new ModItemModelProvider(packOutput, existingFileHelper));

		//sounds
		generator.addProvider(includeClient, new ModSoundProvider(packOutput, existingFileHelper));

		//particles
		generator.addProvider(includeServer, new ModParticleSpriteProvider(packOutput, existingFileHelper));

		//translations
		EnglishLangProvider enLanguage = new EnglishLangProvider(packOutput);
		PirateLangProvider pirateLanguage = new PirateLangProvider(packOutput);

		//advancements
		generator.addProvider(includeServer, new ModAdvancementProvider(packOutput, lookupProvider, existingFileHelper, enLanguage));

		//guide book
		//generator.addProvider(includeServer, new GuideBookProvider(packOutput, enLanguage));

		generator.addProvider(includeServer, enLanguage);
		generator.addProvider(includeServer, pirateLanguage);
	}

}