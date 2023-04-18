package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.loot.ModLootTableProvider;
import com.github.elenterius.biomancy.datagen.recipes.ModRecipeProvider;
import com.github.elenterius.biomancy.datagen.tags.ForgeEntityTypeTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModBlockTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModEntityTypeTagsProvider;
import com.github.elenterius.biomancy.datagen.tags.ModItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	private DataGenerators() {}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(new ModLootTableProvider(generator));

		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(generator, existingFileHelper);
		generator.addProvider(blockTags);
		generator.addProvider(new ModItemTagsProvider(generator, blockTags, existingFileHelper));
		generator.addProvider(new ForgeEntityTypeTagsProvider(generator, existingFileHelper));
		generator.addProvider(new ModEntityTypeTagsProvider(generator, existingFileHelper));

		generator.addProvider(new ModRecipeProvider(generator));
		generator.addProvider(new ModAdvancementProvider(generator));

		generator.addProvider(new PatchouliProvider(generator));
	}

}