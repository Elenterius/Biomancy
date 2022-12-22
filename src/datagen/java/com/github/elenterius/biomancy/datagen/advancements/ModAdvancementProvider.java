package com.github.elenterius.biomancy.datagen.advancements;

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

public class ModAdvancementProvider extends AdvancementProvider {

	private final Marker LOG_MARKER = MarkerManager.getMarker("AdvancementProvider");

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
		LOGGER.info(LOG_MARKER, "registering advancements...");
		registerBiomancyAdvancements(consumer, fileHelper);
	}

	private AdvancementBuilder createAdvancement(String id) {
		return AdvancementBuilder.create(BiomancyMod.MOD_ID, id);
	}

	private void registerBiomancyAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
		Advancement root = createAdvancement("root").icon(ModItems.FLESH_BITS.get()).background("textures/block/flesh.png")
				.showToast().announceToChat()
				.addHasCriterion(ModTags.Items.RAW_MEATS)
				.save(consumer, fileHelper);

		Advancement greedyButcher = createAdvancement("greedy_butcher").parent(root).icon(ModItems.BONE_CLEAVER.get())
				.showToast()
				.addHasCriterion(ModItems.BONE_CLEAVER.get())
				.save(consumer, fileHelper);

		createAdvancement("organ_trader").parent(greedyButcher).icon(ModItems.VOLATILE_GLAND.get())
				.frameType(FrameType.CHALLENGE).showToast().announceToChat().hidden()
				.addCriterion("has_traded_organs", hasTradedItems(ModItems.VOLATILE_GLAND.get(), ModItems.GENERIC_MOB_GLAND.get(), ModItems.TOXIN_GLAND.get()))
				.save(consumer, fileHelper);

		createAdvancement("poacher").parent(greedyButcher).icon(Items.LEATHER)
				.frameType(FrameType.CHALLENGE).showToast().announceToChat().hidden()
				.addCriterion("has_killed_ocelot", hasKilledEntity(EntityType.OCELOT))
				.addCriterion("has_killed_panda", hasKilledEntity(EntityType.PANDA))
				.addCriterion("has_killed_polar_bear", hasKilledEntity(EntityType.POLAR_BEAR))
				.addCriterion("has_killed_axolotl", hasKilledEntity(EntityType.AXOLOTL))
				.save(consumer, fileHelper);

		createAdvancement("predator_killer").parent(greedyButcher).icon(ModItems.MOB_FANG.get())
				.frameType(FrameType.CHALLENGE).showToast().announceToChat().hidden()
				.addCriterion("has_killed_fangs_mob", hasKilledEntityTag(ModTags.EntityTypes.SHARP_FANG))
				.addCriterion("has_killed_claws_mob", hasKilledEntityTag(ModTags.EntityTypes.SHARP_CLAW))
				.addHasCriterion(ModItems.MOB_FANG.get())
				.addHasCriterion(ModItems.MOB_CLAW.get())
				.save(consumer, fileHelper);

		createAdvancement("cat_killer").parent(greedyButcher).icon(Items.STRING)
				.frameType(FrameType.CHALLENGE).showToast().announceToChat().hidden()
				.addCriterion("has_killed_cat", hasKilledEntity(EntityType.CAT))
				.save(consumer, fileHelper);

		Advancement cradle = createAdvancement("flesh").parent(root).icon(ModItems.PRIMORDIAL_CRADLE.get())
				.frameType(FrameType.CHALLENGE).showToast()
				.addHasCriterion(Items.ENDER_EYE)
				.save(consumer, fileHelper);

		Advancement livingFlesh = createAdvancement("living_flesh").parent(cradle).icon(ModItems.PRIMORDIAL_CRADLE.get())
				.showToast()
				.addCriterion("placed_creator", hasPlacedBlock(ModBlocks.PRIMORDIAL_CRADLE.get()))
				.save(consumer, fileHelper);

		createAdvancement("healing_activator_sacrifice").parent(livingFlesh).icon(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING))
				.showToast()
				.addCriterion("has_sacrificed_healing_activator", hasSacrificedItem(Items.POTION))
				.save(consumer, fileHelper);

		createAdvancement("raw_meat_sacrifice").parent(livingFlesh).icon(Items.BEEF)
				.showToast()
				.addCriterion("has_sacrificed_raw_meat", hasSacrificedTag(ModTags.Items.RAW_MEATS))
				.save(consumer, fileHelper);

		createAdvancement("cooked_meat_sacrifice").parent(livingFlesh).icon(Items.COOKED_BEEF)
				.showToast().hidden()
				.addCriterion("has_sacrificed_cooked_meat", hasSacrificedTag(ModTags.Items.COOKED_MEATS))
				.save(consumer, fileHelper);

		createAdvancement("decomposer").parent(livingFlesh).icon(ModItems.DECOMPOSER.get())
				.showToast()
				.addHasCriterion(ModItems.DECOMPOSER.get())
				.save(consumer, fileHelper);

		Advancement bioForge = createAdvancement("bio_forge").parent(livingFlesh).icon(ModItems.BIO_FORGE.get())
				.frameType(FrameType.GOAL).showToast().announceToChat()
				.addHasCriterion(ModItems.BIO_FORGE.get())
				.save(consumer, fileHelper);

		createAdvancement("digester").parent(bioForge).icon(ModItems.DIGESTER.get())
				.frameType(FrameType.CHALLENGE).showToast()
				.addHasCriterion(ModItems.DIGESTER.get())
				.save(consumer, fileHelper);

		Advancement bioLab = createAdvancement("bio_lab").parent(bioForge).icon(ModItems.BIO_LAB.get())
				.frameType(FrameType.CHALLENGE).showToast()
				.addHasCriterion(ModItems.BIO_LAB.get())
				.save(consumer, fileHelper);

		createAdvancement("bio_injector").parent(bioLab).icon(ModItems.INJECTOR.get())
				.showToast()
				.addHasCriterion(ModItems.INJECTOR.get())
				.save(consumer, fileHelper);

		Advancement organicCompounds = createAdvancement("organic_compounds").parent(bioLab).icon(ModItems.ORGANIC_COMPOUND.get())
				.showToast()
				.addHasCriterion(ModItems.ORGANIC_COMPOUND.get())
				.save(consumer, fileHelper);

		createAdvancement("exotic_compounds").parent(organicCompounds).icon(ModItems.EXOTIC_COMPOUND.get())
				.showToast()
				.addHasCriterion(ModItems.EXOTIC_COMPOUND.get())
				.save(consumer, fileHelper);

		createAdvancement("genetic_compounds").parent(organicCompounds).icon(ModItems.GENETIC_COMPOUND.get())
				.showToast()
				.addHasCriterion(ModItems.GENETIC_COMPOUND.get())
				.save(consumer, fileHelper);
	}

}
