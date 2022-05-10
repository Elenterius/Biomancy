package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.loot.ModGlobalLootModifierProvider;
import com.github.elenterius.biomancy.datagen.loot.ModLootTableProvider;
import com.github.elenterius.biomancy.datagen.models.ModBlockStateProvider;
import com.github.elenterius.biomancy.datagen.models.ModModelProvider;
import com.github.elenterius.biomancy.datagen.recipes.ModRecipeProvider;
import com.github.elenterius.biomancy.datagen.tags.ForgeEntityTypeTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModBlockTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModEntityTypeTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	private DataGenerators() {}

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		//tags
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(generator, existingFileHelper);
		generator.addProvider(blockTags);
		generator.addProvider(new ModItemTagsProvider(generator, blockTags, existingFileHelper));
		generator.addProvider(new ForgeEntityTypeTagsProvider(generator, existingFileHelper));
		generator.addProvider(new ModEntityTypeTagsProvider(generator, existingFileHelper));

		//recipes
		generator.addProvider(new ModRecipeProvider(generator));

		//loot
		generator.addProvider(new ModLootTableProvider(generator));
		generator.addProvider(new ModGlobalLootModifierProvider(generator));

		//advancements
		generator.addProvider(new ModAdvancementProvider(generator, existingFileHelper));

		//models & block states
		generator.addProvider(new ModModelProvider(generator));
		generator.addProvider(new ModBlockStateProvider(generator, existingFileHelper));

//		generator.addProvider(new PatchouliProvider(generator));
	}

}