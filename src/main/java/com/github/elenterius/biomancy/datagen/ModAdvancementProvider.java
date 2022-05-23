package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
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

		Advancement root = Advancement.Builder.advancement().display(ModItems.OCULUS.get(), createTitle("root"), createDescription("root"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
				FrameType.TASK, true, false, false).addCriterion("has_raw_meats", hasTag(ModTags.Items.RAW_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/root");

		Advancement nodeA1 = Advancement.Builder.advancement().parent(root).display(ModItems.CREATOR.get(), createTitle("flesh"), createDescription("flesh"), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_flesh_block", hasItems(ModItems.FLESH_BLOCK.get())).save(consumer, BiomancyMod.MOD_ID + "/flesh");

//		Advancement nodeA2 = Advancement.Builder.advancement().parent(nodeA1).display(ModItems.STOMACH.get(), createTitle("stomach"), createDescription("stomach"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_stomachs", hasTag(ModTags.Items.STOMACHS)).save(consumer, BiomancyMod.MOD_ID + "/stomach");
//
//		Advancement nodeA3 = Advancement.Builder.advancement().parent(nodeA2).display(ModItems.DIGESTER.get(), createTitle("digester"), createDescription("digester"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_digester", hasItems(ModItems.DIGESTER.get())).save(consumer, BiomancyMod.MOD_ID + "/digester");
//
//		Advancement nodeE0 = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.CHEWER.get(), createTitle("chewer"), createDescription("chewer"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_chewer", hasItems(ModItems.CHEWER.get())).save(consumer, BiomancyMod.MOD_ID + "/chewer");
//
//		Advancement nodeE1 = Advancement.Builder.advancement().parent(nodeE0).display(ModItems.BOLUS.get(), createTitle("crushed_biomass"), createDescription("crushed_biomass"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_crushed_biomass", hasItems(ModItems.BOLUS.get())).save(consumer, BiomancyMod.MOD_ID + "/crushed_biomass");
//
//		Advancement nodeD0 = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.SOLIDIFIER.get(), createTitle("solidifier"), createDescription("solidifier"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_solidifier", hasItems(ModItems.SOLIDIFIER.get())).save(consumer, BiomancyMod.MOD_ID + "/solidifier");
//
//		Advancement nodeD1 = Advancement.Builder.advancement().parent(nodeD0).display(ModItems.NUTRIENT_PASTE.get(), createTitle("nutrient_paste"), createDescription("nutrient_paste"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_nutrient_paste", hasItems(ModItems.NUTRIENT_PASTE.get())).save(consumer, BiomancyMod.MOD_ID + "/nutrient_paste");
//
//		Advancement nodeA4c = Advancement.Builder.advancement().parent(nodeA3).display(ModItems.DECOMPOSER.get(), createTitle("decomposer"), createDescription("decomposer"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_decomposer", hasItems(ModItems.DECOMPOSER.get())).save(consumer, BiomancyMod.MOD_ID + "/decomposer");
//
//		Advancement nodeB1 = Advancement.Builder.advancement().parent(nodeA4c)
//				.display(ModItems.MUTAGENIC_BILE.get(), createTitle("evolution_pool"), createDescription("evolution_pool"), null, FrameType.TASK, true, false, false)
//				.addCriterion("create_evolution_pool", EvolutionPoolCreatedTrigger.Instance.create()).save(consumer, BiomancyMod.MOD_ID + "/evolution_pool");
//
//		Advancement nodeB2 = Advancement.Builder.advancement().parent(nodeB1).display(ModItems.BIOMETAL.get(), createTitle("biometal"), createDescription("biometal"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_biometal", hasItems(ModItems.BIOMETAL.get())).save(consumer, BiomancyMod.MOD_ID + "/biometal");
//
//		Advancement nodeC1 = Advancement.Builder.advancement().parent(nodeA4c).display(ModItems.REAGENT.get(), createTitle("reagent"), createDescription("reagent"), null,
//						FrameType.TASK, true, false, false)
//				.addCriterion("has_reagent", hasItems(ModItems.REAGENT.get())).save(consumer, BiomancyMod.MOD_ID + "/reagent");
	}

}
