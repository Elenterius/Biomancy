package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Consumer;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModAdvancementProvider extends AdvancementProvider {

	private final Marker logMarker = MarkerManager.getMarker("AdvancementProvider");

	public ModAdvancementProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		super(generator, fileHelper);
	}

	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	protected static InventoryChangeTrigger.TriggerInstance hasTag(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	protected static PlacedBlockTrigger.TriggerInstance hasPlacedBlock(Block block) {
		return PlacedBlockTrigger.TriggerInstance.placedBlock(block);
	}

	protected TranslatableComponent createTitle(String name) {
		return new TranslatableComponent("advancements.biomancy." + name + ".title");
	}

	protected TranslatableComponent createDescription(String name) {
		return new TranslatableComponent("advancements.biomancy." + name + ".description");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " Advancements";
	}

	@Override
	protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
		LOGGER.info(logMarker, "registering advancements...");

		Advancement root = Advancement.Builder.advancement().display(ModItems.FLESH_BITS.get(), createTitle("root"), createDescription("root"), BiomancyMod.createRL("textures/block/flesh.png"),
				FrameType.TASK, true, true, false).addCriterion("has_raw_meats", hasTag(ModTags.Items.RAW_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/root");

		Advancement nodeCreator = Advancement.Builder.advancement().parent(root).display(ModItems.CREATOR.get(), createTitle("flesh"), createDescription("flesh"), null,
						FrameType.CHALLENGE, true, false, true)
				.addCriterion("has_ender_eye", hasItems(Items.ENDER_EYE)).save(consumer, BiomancyMod.MOD_ID + "/flesh");

		Advancement nodeLivingFlesh = Advancement.Builder.advancement().parent(nodeCreator).display(ModItems.LIVING_FLESH.get(), createTitle("living_flesh"), createDescription("living_flesh"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("placed_creator", hasPlacedBlock(ModBlocks.CREATOR.get())).save(consumer, BiomancyMod.MOD_ID + "/living_flesh");

		Advancement nodeDecomposer = Advancement.Builder.advancement().parent(nodeLivingFlesh).display(ModItems.DECOMPOSER.get(), createTitle("decomposer"), createDescription("decomposer"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("placed_decomposer", hasPlacedBlock(ModBlocks.DECOMPOSER.get())).save(consumer, BiomancyMod.MOD_ID + "/decomposer");

		Advancement nodeBioForge = Advancement.Builder.advancement().parent(nodeLivingFlesh).display(ModItems.BIO_FORGE.get(), createTitle("bio_forge"), createDescription("bio_forge"), null,
						FrameType.GOAL, true, true, false)
				.addCriterion("placed_bio_forge", hasPlacedBlock(ModBlocks.BIO_FORGE.get())).save(consumer, BiomancyMod.MOD_ID + "/bio_forge");

		Advancement nodeDigester = Advancement.Builder.advancement().parent(nodeBioForge).display(ModItems.DIGESTER.get(), createTitle("digester"), createDescription("digester"), null,
						FrameType.CHALLENGE, true, false, true)
				.addCriterion("has_digester", hasItems(ModItems.DIGESTER.get())).save(consumer, BiomancyMod.MOD_ID + "/digester");

		Advancement nodeBioLab = Advancement.Builder.advancement().parent(nodeBioForge).display(ModItems.BIO_LAB.get(), createTitle("bio_lab"), createDescription("bio_lab"), null,
						FrameType.CHALLENGE, true, true, true)
				.addCriterion("has_bio_lab", hasItems(ModItems.BIO_LAB.get())).save(consumer, BiomancyMod.MOD_ID + "/bio_lab");

		Advancement nodeCompounds = Advancement.Builder.advancement().parent(nodeBioLab).display(ModItems.ORGANIC_COMPOUND.get(), createTitle("compounds"), createDescription("compounds"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("placed_bio_lab", hasPlacedBlock(ModBlocks.BIO_LAB.get())).save(consumer, BiomancyMod.MOD_ID + "/compounds");

		//		Advancement nodeD1 = Advancement.Builder.advancement().parent(nodeD0).display(ModItems.NUTRIENT_PASTE.get(), createTitle("nutrient_paste"), createDescription("nutrient_paste"), null,
		//						FrameType.TASK, true, false, false)
		//				.addCriterion("has_nutrient_paste", hasItems(ModItems.NUTRIENT_PASTE.get())).save(consumer, BiomancyMod.MOD_ID + "/nutrient_paste");
	}

}
