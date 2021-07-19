package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
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
		return InventoryChangeTrigger.Instance.forItems(items);
	}

	protected static InventoryChangeTrigger.Instance hasTag(ITag<Item> tag) {
		return InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(tag).build());
	}

	public TranslationTextComponent createTitle(String name) {
		return new TranslationTextComponent("advancements.biomancy." + name + ".title");
	}

	public TranslationTextComponent createDescription(String name) {
		return new TranslationTextComponent("advancements.biomancy."+ name + ".description");
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		Path outputFolder = generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<Advancement> consumer = (advancement) -> {
			if (!set.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			}
			else {
				Path path = getPath(outputFolder, advancement);
				try {
					IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
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
		Advancement root = Advancement.Builder.builder().withDisplay(ModItems.OCULUS.get(), createTitle("root"), createDescription("root"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
				FrameType.TASK, true, false, false).withCriterion("has_raw_meats", hasTag(ModTags.Items.RAW_MEATS)).register(consumer, BiomancyMod.MOD_ID + "/root");

		Advancement nodeA1 = Advancement.Builder.builder().withParent(root).withDisplay(Items.CAULDRON, createTitle("flesh"), createDescription("flesh"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_flesh_block", hasItems(ModItems.FLESH_BLOCK.get())).register(consumer, BiomancyMod.MOD_ID + "/flesh");

		Advancement nodeA2 = Advancement.Builder.builder().withParent(nodeA1).withDisplay(ModItems.STOMACH.get(), createTitle("stomach"), createDescription("stomach"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_stomachs", hasTag(ModTags.Items.STOMACHS)).register(consumer, BiomancyMod.MOD_ID + "/stomach");

		Advancement nodeA3 = Advancement.Builder.builder().withParent(nodeA2).withDisplay(ModItems.DIGESTER.get(), createTitle("digester"), createDescription("digester"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_digester", hasItems(ModItems.DIGESTER.get())).register(consumer, BiomancyMod.MOD_ID + "/digester");

		Advancement nodeA4a = Advancement.Builder.builder().withParent(nodeA3).withDisplay(ModItems.CHEWER.get(), createTitle("chewer"), createDescription("chewer"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_chewer", hasItems(ModItems.CHEWER.get())).register(consumer, BiomancyMod.MOD_ID + "/chewer");

		Advancement nodeA4b = Advancement.Builder.builder().withParent(nodeA3).withDisplay(ModItems.SOLIDIFIER.get(), createTitle("solidifier"), createDescription("solidifier"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_solidifier", hasItems(ModItems.SOLIDIFIER.get())).register(consumer, BiomancyMod.MOD_ID + "/solidifier");

		Advancement nodeA4c = Advancement.Builder.builder().withParent(nodeA3).withDisplay(ModItems.DECOMPOSER.get(), createTitle("decomposer"), createDescription("decomposer"), null,
				FrameType.TASK, true, false, false)
				.withCriterion("has_decomposer", hasItems(ModItems.DECOMPOSER.get())).register(consumer, BiomancyMod.MOD_ID + "/decomposer");
	}

}
