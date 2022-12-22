package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.trigger.SacrificedItemTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Consumer;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;
import static com.github.elenterius.biomancy.datagen.ModEnglishLanguageProvider.AdvancementTranslations;

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

	protected static KilledTrigger.TriggerInstance hasKilledEntity(EntityType<?> entityType) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType).build());
	}

	protected static KilledTrigger.TriggerInstance hasKilledEntityTag(TagKey<EntityType<?>> tag) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(tag).build());
	}

	protected static SacrificedItemTrigger.TriggerInstance hasSacrificedItem(ItemLike item) {
		return SacrificedItemTrigger.TriggerInstance.sacrificedItem(item);
	}

	protected static SacrificedItemTrigger.TriggerInstance hasSacrificedTag(TagKey<Item> tag) {
		return SacrificedItemTrigger.TriggerInstance.sacrificedItem(tag);
	}

	protected static TradeTrigger.TriggerInstance hasTradedItems(ItemLike... items) {
		return new TradeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().of(items).build());
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " Advancements";
	}

	@Override
	protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
		LOGGER.info(logMarker, "registering advancements...");

		Advancement root = Advancement.Builder.advancement().display(ModItems.FLESH_BITS.get(), AdvancementTranslations.ROOT.getTitle(), AdvancementTranslations.ROOT.getDescription(), BiomancyMod.createRL("textures/block/flesh.png"),
				FrameType.TASK, true, true, false).addCriterion("has_raw_meats", hasTag(ModTags.Items.RAW_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/root");

		Advancement greedyButcher = Advancement.Builder.advancement().parent(root).display(ModItems.BONE_CLEAVER.get(), AdvancementTranslations.GREEDY_BUTCHER.getTitle(), AdvancementTranslations.GREEDY_BUTCHER.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_bone_cleaver", hasItems(ModItems.BONE_CLEAVER.get())).save(consumer, BiomancyMod.MOD_ID + "/greedy_butcher");

		Advancement.Builder.advancement().parent(greedyButcher).display(ModItems.VOLATILE_GLAND.get(), AdvancementTranslations.ORGAN_TRADER.getTitle(), AdvancementTranslations.ORGAN_TRADER.getDescription(), null,
						FrameType.CHALLENGE, true, true, true)
				.addCriterion("has_traded_organs", hasTradedItems(ModItems.VOLATILE_GLAND.get(), ModItems.GENERIC_MOB_GLAND.get(), ModItems.TOXIN_GLAND.get()))
				.save(consumer, BiomancyMod.MOD_ID + "/organ_trader");

		Advancement.Builder.advancement().parent(greedyButcher).display(Items.LEATHER, AdvancementTranslations.POACHER.getTitle(), AdvancementTranslations.POACHER.getDescription(), null,
						FrameType.CHALLENGE, true, true, true)
				.addCriterion("has_killed_ocelot", hasKilledEntity(EntityType.OCELOT))
				.addCriterion("has_killed_panda", hasKilledEntity(EntityType.PANDA))
				.addCriterion("has_killed_polar_bear", hasKilledEntity(EntityType.POLAR_BEAR))
				.addCriterion("has_killed_axolotl", hasKilledEntity(EntityType.AXOLOTL))
				.save(consumer, BiomancyMod.MOD_ID + "/poacher");

		Advancement.Builder.advancement().parent(greedyButcher).display(ModItems.MOB_FANG.get(), AdvancementTranslations.PREDATOR_KILLER.getTitle(), AdvancementTranslations.PREDATOR_KILLER.getDescription(), null,
						FrameType.CHALLENGE, true, true, true)
				.addCriterion("has_killed_fangs_mob", hasKilledEntityTag(ModTags.EntityTypes.SHARP_FANG))
				.addCriterion("has_killed_claws_mob", hasKilledEntityTag(ModTags.EntityTypes.SHARP_CLAW))
				.addCriterion("has_fangs", hasItems(ModItems.MOB_FANG.get()))
				.addCriterion("has_claws", hasItems(ModItems.MOB_CLAW.get()))
				.save(consumer, BiomancyMod.MOD_ID + "/predator_killer");

		Advancement.Builder.advancement().parent(greedyButcher).display(Items.STRING, AdvancementTranslations.CAT_KILLER.getTitle(), AdvancementTranslations.CAT_KILLER.getDescription(), null,
						FrameType.CHALLENGE, true, true, true)
				.addCriterion("has_killed_cat", hasKilledEntity(EntityType.CAT))
				.save(consumer, BiomancyMod.MOD_ID + "/cat_killer");

		Advancement nodeCradle = Advancement.Builder.advancement().parent(root).display(ModItems.PRIMORDIAL_CRADLE.get(), AdvancementTranslations.FLESH.getTitle(), AdvancementTranslations.FLESH.getDescription(), null,
						FrameType.CHALLENGE, true, false, false)
				.addCriterion("has_ender_eye", hasItems(Items.ENDER_EYE)).save(consumer, BiomancyMod.MOD_ID + "/flesh");

		Advancement nodeLivingFlesh = Advancement.Builder.advancement().parent(nodeCradle).display(ModItems.LIVING_FLESH.get(), AdvancementTranslations.LIVING_FLESH.getTitle(), AdvancementTranslations.LIVING_FLESH.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("placed_creator", hasPlacedBlock(ModBlocks.PRIMORDIAL_CRADLE.get())).save(consumer, BiomancyMod.MOD_ID + "/living_flesh");

		Advancement.Builder.advancement().parent(nodeLivingFlesh).display(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING),
						AdvancementTranslations.HEALING_ACTIVATOR_SACRIFICE.getTitle(), AdvancementTranslations.HEALING_ACTIVATOR_SACRIFICE.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_sacrificed_healing_activator", hasSacrificedItem(Items.POTION)).save(consumer, BiomancyMod.MOD_ID + "/healing_activator_sacrifice");

		Advancement.Builder.advancement().parent(nodeLivingFlesh).display(Items.BEEF, AdvancementTranslations.RAW_MEAT_SACRIFICE.getTitle(), AdvancementTranslations.RAW_MEAT_SACRIFICE.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_sacrificed_raw_meat", hasSacrificedTag(ModTags.Items.RAW_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/raw_meat_sacrifice");

		Advancement.Builder.advancement().parent(nodeLivingFlesh).display(Items.COOKED_BEEF, AdvancementTranslations.COOKED_MEAT_SACRIFICE.getTitle(), AdvancementTranslations.COOKED_MEAT_SACRIFICE.getDescription(), null,
						FrameType.TASK, true, false, true)
				.addCriterion("has_sacrificed_cooked_meat", hasSacrificedTag(ModTags.Items.COOKED_MEATS)).save(consumer, BiomancyMod.MOD_ID + "/cooked_meat_sacrifice");

		Advancement.Builder.advancement().parent(nodeLivingFlesh).display(ModItems.DECOMPOSER.get(), AdvancementTranslations.DECOMPOSER.getTitle(), AdvancementTranslations.DECOMPOSER.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_decomposer", hasItems(ModItems.DECOMPOSER.get())).save(consumer, BiomancyMod.MOD_ID + "/decomposer");

		Advancement bioForge = Advancement.Builder.advancement().parent(nodeLivingFlesh).display(ModItems.BIO_FORGE.get(), AdvancementTranslations.BIO_FORGE.getTitle(), AdvancementTranslations.BIO_FORGE.getDescription(), null,
						FrameType.GOAL, true, true, false)
				.addCriterion("has_bio_forge", hasItems(ModItems.BIO_FORGE.get())).save(consumer, BiomancyMod.MOD_ID + "/bio_forge");

		Advancement.Builder.advancement().parent(bioForge).display(ModItems.DIGESTER.get(), AdvancementTranslations.DIGESTER.getTitle(), AdvancementTranslations.DIGESTER.getDescription(), null,
						FrameType.CHALLENGE, true, false, false)
				.addCriterion("has_digester", hasItems(ModItems.DIGESTER.get())).save(consumer, BiomancyMod.MOD_ID + "/digester");

		Advancement bioLab = Advancement.Builder.advancement().parent(bioForge).display(ModItems.BIO_LAB.get(), AdvancementTranslations.BIO_LAB.getTitle(), AdvancementTranslations.BIO_LAB.getDescription(), null,
						FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_bio_lab", hasItems(ModItems.BIO_LAB.get())).save(consumer, BiomancyMod.MOD_ID + "/bio_lab");

		Advancement.Builder.advancement().parent(bioLab).display(ModItems.INJECTOR.get(), AdvancementTranslations.BIO_INJECTOR.getTitle(), AdvancementTranslations.BIO_INJECTOR.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_bio_injector", hasItems(ModItems.INJECTOR.get())).save(consumer, BiomancyMod.MOD_ID + "/bio_injector");

		Advancement organicCompounds = Advancement.Builder.advancement().parent(bioLab).display(ModItems.ORGANIC_COMPOUND.get(), AdvancementTranslations.ORGANIC_COMPOUNDS.getTitle(), AdvancementTranslations.ORGANIC_COMPOUNDS.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_organic_compound", hasItems(ModItems.ORGANIC_COMPOUND.get())).save(consumer, BiomancyMod.MOD_ID + "/organic_compounds");

		Advancement.Builder.advancement().parent(organicCompounds).display(ModItems.EXOTIC_COMPOUND.get(), AdvancementTranslations.EXOTIC_COMPOUNDS.getTitle(), AdvancementTranslations.EXOTIC_COMPOUNDS.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_exotic_compound", hasItems(ModItems.EXOTIC_COMPOUND.get())).save(consumer, BiomancyMod.MOD_ID + "/exotic_compounds");

		Advancement.Builder.advancement().parent(organicCompounds).display(ModItems.GENETIC_COMPOUND.get(), AdvancementTranslations.GENETIC_COMPOUNDS.getTitle(), AdvancementTranslations.GENETIC_COMPOUNDS.getDescription(), null,
						FrameType.TASK, true, false, false)
				.addCriterion("has_genetic_compound", hasItems(ModItems.GENETIC_COMPOUND.get())).save(consumer, BiomancyMod.MOD_ID + "/genetic_compounds");
	}

}
