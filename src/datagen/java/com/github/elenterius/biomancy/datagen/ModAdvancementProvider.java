package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.trigger.EvolutionPoolCreatedTrigger;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class ModAdvancementProvider implements IDataProvider {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	public final Logger LOGGER = BiomancyMod.LOGGER;
	public final Marker logMarker = MarkerManager.getMarker("ModAdvancementProvider");
	private final DataGenerator generator;

	public ModAdvancementProvider(DataGenerator generatorIn) {
		generator = generatorIn;
	}

	protected static Path getPath(Path pathIn, Advancement advancementIn) {
		return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
	}

	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider... items) {
		return InventoryChangeTrigger.Instance.hasItems(items);
	}

	protected static InventoryChangeTrigger.Instance hasTag(ITag<Item> tag) {
		return InventoryChangeTrigger.Instance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	public TranslationTextComponent createTitle(String name) {
		return new TranslationTextComponent("advancements.biomancy." + name + ".title");
	}

	public TranslationTextComponent createDescription(String name) {
		return new TranslationTextComponent("advancements.biomancy." + name + ".description");
	}

	@Override
	public void run(DirectoryCache cache) throws IOException {
		Path outputFolder = generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<Advancement> consumer = (advancement) -> {
			if (!set.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			}
			else {
				Path path = getPath(outputFolder, advancement);
				try {
					IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save advancement {}", path, ioexception);
				}

			}
		};

		LOGGER.info(logMarker, "registering workbench recipes...");
		registerAdvancements(consumer);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " Advancements";
	}

	protected void registerAdvancements(Consumer<Advancement> consumer) {
		Advancement root = Advancement.Builder.advancement().display(ModItems.OCULUS.get(), createTitle("root"), createDescription("root"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
				FrameType.TASK, true, false, false).addCriterion("has_raw_meats", hasTag(ModTags.Items.RAW_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/root");

		Advancement nodeA1 = Advancement.Builder.advancement().parent(root).display(Items.CAULDRON, createTitle("flesh"), createDescription("flesh"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_flesh_block", hasItems(ModItems.FLESH_BLOCK.get())).save(consumer, BiomancyMod.MOD_ID + "/flesh");

		Advancement nodeA2 = Advancement.Builder.advancement().parent(nodeA1).display(ModItems.STOMACH.get(), createTitle("stomach"), createDescription("stomach"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_stomachs", hasTag(ModTags.Items.STOMACHS)).save(consumer, BiomancyMod.MOD_ID + "/stomach");

		Advancement nodeA3 = Advancement.Builder.advancement().parent(nodeA2).display(ModItems.DIGESTER.get(), createTitle("digester"), createDescription("digester"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_digester", hasItems(ModItems.DIGESTER.get())).save(consumer, BiomancyMod.MOD_ID + "/digester");

		Advancement nodeE0 = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.CHEWER.get(), createTitle("chewer"), createDescription("chewer"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_chewer", hasItems(ModItems.CHEWER.get())).save(consumer, BiomancyMod.MOD_ID + "/chewer");

		Advancement nodeE1 = Advancement.Builder.advancement().parent(nodeE0).display(ModItems.BOLUS.get(), createTitle("crushed_biomass"), createDescription("crushed_biomass"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_crushed_biomass", hasItems(ModItems.BOLUS.get())).save(consumer, BiomancyMod.MOD_ID + "/crushed_biomass");

		Advancement nodeD0 = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.SOLIDIFIER.get(), createTitle("solidifier"), createDescription("solidifier"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_solidifier", hasItems(ModItems.SOLIDIFIER.get())).save(consumer, BiomancyMod.MOD_ID + "/solidifier");

		Advancement nodeD1 = Advancement.Builder.advancement().parent(nodeD0).display(ModItems.NUTRIENT_PASTE.get(), createTitle("nutrient_paste"), createDescription("nutrient_paste"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_nutrient_paste", hasItems(ModItems.NUTRIENT_PASTE.get())).save(consumer, BiomancyMod.MOD_ID + "/nutrient_paste");

		Advancement nodeA4c = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.DECOMPOSER.get(), createTitle("decomposer"), createDescription("decomposer"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_decomposer", hasItems(ModItems.DECOMPOSER.get())).save(consumer, BiomancyMod.MOD_ID + "/decomposer");

		Advancement nodeB1 = Advancement.Builder.advancement().parent(nodeA4c)
				.display(ModItems.MUTAGENIC_BILE.get(), createTitle("evolution_pool"), createDescription("evolution_pool"), null, FrameType.TASK, true, false, false)
				.addCriterion("create_evolution_pool", EvolutionPoolCreatedTrigger.Instance.create()).save(consumer, BiomancyMod.MOD_ID + "/evolution_pool");

		Advancement nodeB2 = Advancement.Builder.advancement().parent(nodeB1).display(ModItems.BIOMETAL.get(), createTitle("biometal"), createDescription("biometal"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_biometal", hasItems(ModItems.BIOMETAL.get())).save(consumer, BiomancyMod.MOD_ID + "/biometal");

		Advancement nodeC1 = Advancement.Builder.advancement().parent(nodeA4c).display(ModItems.REAGENT.get(), createTitle("reagent"), createDescription("reagent"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_reagent", hasItems(ModItems.REAGENT.get())).save(consumer, BiomancyMod.MOD_ID + "/reagent");
	}

}
