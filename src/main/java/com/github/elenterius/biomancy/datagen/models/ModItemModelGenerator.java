package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.mixin.TextureSlotAccessor;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record ModItemModelGenerator(BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {

	private static final TextureSlot LAYER_1 = TextureSlotAccessor.callCreate("layer1");

	private static final ModelTemplate EGG_MODEL_TEMPLATE = createVanillaTemplate("template_spawn_egg");
	private static final ModelTemplate GUN_OVERLAY_TEMPLATE = createTemplate("handheld_gun", TextureSlot.LAYER0, LAYER_1);
	private static final ModelTemplate GUN_TEMPLATE = createTemplate("handheld_gun", TextureSlot.LAYER0);
	private static final ModelTemplate OVERLAY_TEMPLATE = createVanillaTemplate("generated", TextureSlot.LAYER0, LAYER_1);

	private static ModelTemplate createVanillaTemplate(String id, TextureSlot... requiredSlots) {
		return new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/" + id)), Optional.empty(), requiredSlots);
	}

	private static ModelTemplate createTemplate(String id, TextureSlot... pRequiredSlots) {
		return new ModelTemplate(Optional.of(new ResourceLocation("biomancy", "item/" + id)), Optional.empty(), pRequiredSlots);
	}

	private void generateFlat(Item item, ModelTemplate modelTemplate) {
		modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), modelOutput);
	}

	private void generateFlat(Item item, String suffix, ModelTemplate modelTemplate) {
		modelTemplate.create(ModelLocationUtils.getModelLocation(item, suffix), TextureMapping.layer0(TextureMapping.getItemTexture(item, suffix)), modelOutput);
	}

	private void generateFlat(Item item, Item displayItem, ModelTemplate modelTemplate) {
		modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(displayItem), modelOutput);
	}

	private void generateFlatWithOverlay(Item item, ModelTemplate modelTemplate) {
		ResourceLocation itemTexture = TextureMapping.getItemTexture(item);
		ResourceLocation overlayTexture = new ResourceLocation(itemTexture.getNamespace(), itemTexture.getPath() + "_overlay");
		modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(itemTexture).put(LAYER_1, overlayTexture), modelOutput);
	}

	private void generateFlatWithOverlayInFolder(Item item, String folder, ModelTemplate modelTemplate) {
		ResourceLocation itemTexture = getItemTexture(item, folder);
		ResourceLocation overlayTexture = new ResourceLocation(itemTexture.getNamespace(), itemTexture.getPath() + "_overlay");
		modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(itemTexture).put(LAYER_1, overlayTexture), modelOutput);
	}

	private ResourceLocation getItemTexture(Item pItem, String folder) {
		ResourceLocation resourcelocation = Registry.ITEM.getKey(pItem);
		return new ResourceLocation(resourcelocation.getNamespace(), String.format("item/%s/%s", folder, resourcelocation.getPath()));
	}

	private ResourceLocation getItemTexture(Item pItem, String folder, String suffix) {
		ResourceLocation resourcelocation = Registry.ITEM.getKey(pItem);
		return new ResourceLocation(resourcelocation.getNamespace(), String.format("item/%s/%s%s", folder, resourcelocation.getPath(), suffix));
	}

	public TextureMapping layer0(Item item, String folder) {
		return new TextureMapping().put(TextureSlot.LAYER0, getItemTexture(item, folder));
	}

	private void generateSerum(Item item) {
		generateFlatItemInFolder(item, "serum", ModelTemplates.FLAT_ITEM);
	}

	private void generateComponent(Item item) {
		generateFlatItemInFolder(item, "component", ModelTemplates.FLAT_ITEM);
	}

	private void generateWeapon(Item item, ModelTemplate modelTemplate) {
		generateFlatItemInFolder(item, "weapon", modelTemplate);
	}

	private void generateWeaponWithOverlay(Item item, ModelTemplate modelTemplate) {
		generateFlatWithOverlayInFolder(item, "weapon", modelTemplate);
	}

	private void generateFlatItemInFolder(Item item, String folder, ModelTemplate modelTemplate) {
		modelTemplate.create(ModelLocationUtils.getModelLocation(item), layer0(item, folder), modelOutput);
	}

	public void run() {
		generateComponent(ModItems.FLESH_BITS.get());
		generateComponent(ModItems.BONE_SCRAPS.get());
		generateComponent(ModItems.ELASTIC_FIBERS.get());
		generateComponent(ModItems.MINERAL_DUST.get());
		generateComponent(ModItems.TOUGH_FIBERS.get());
		generateComponent(ModItems.ORGANIC_MATTER.get());
		generateComponent(ModItems.EXOTIC_DUST.get());
		generateComponent(ModItems.BIO_MINERALS.get());
		generateComponent(ModItems.BIO_LUMENS.get());
		generateComponent(ModItems.NUTRIENTS.get());
		generateComponent(ModItems.GEM_DUST.get());
		generateComponent(ModItems.LITHIC_POWDER.get());

		generateComponent(ModItems.REJUVENATING_MUCUS.get());
		generateComponent(ModItems.HORMONE_SECRETION.get());
		generateComponent(ModItems.VENOM_EXTRACT.get());
		generateComponent(ModItems.VOLATILE_EXTRACT.get());
		generateComponent(ModItems.BILE_EXTRACT.get());

		generateFlat(ModItems.SKIN_CHUNK.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NECROTIC_FLESH_LUMP.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LARYNX.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MOB_FANG.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MOB_CLAW.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MOB_SINEW.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MOB_MARROW.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.WITHERED_MOB_MARROW.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MOB_GLAND.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.VENOM_GLAND.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.VOLATILE_GLAND.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.LIVING_FLESH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.EXALTED_LIVING_FLESH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.OCULUS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIOMETAL_INGOT.get(), ModelTemplates.FLAT_ITEM);
		generateFlatWithOverlay(ModItems.ESSENCE.get(), OVERLAY_TEMPLATE);
		generateFlat(ModItems.BIO_EXTRACTOR.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
		generateFlatWithOverlay(ModItems.BIO_INJECTOR.get(), GUN_OVERLAY_TEMPLATE);

		generateFlat(ModItems.NUTRIENT_PASTE.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);
//		generateFlat(ModItems.PROTEIN_BAR.get(), ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.GLASS_VIAL.get(), ModelTemplates.FLAT_ITEM);
		generateFlatWithOverlay(ModItems.GENERIC_SERUM.get(), OVERLAY_TEMPLATE);
		generateSerum(ModItems.REJUVENATION_SERUM.get());
		generateSerum(ModItems.GROWTH_SERUM.get());
		generateSerum(ModItems.BREEDING_STIMULANT.get());
		generateSerum(ModItems.ABSORPTION_BOOST.get());
		generateSerum(ModItems.CLEANSING_SERUM.get());
		generateSerum(ModItems.INSOMNIA_CURE.get());
		generateSerum(ModItems.ADRENALINE_SERUM.get());
		generateSerum(ModItems.DECAY_AGENT.get());

		generateSerum(ModItems.ICHOR_SERUM.get());

		generateSerum(ModItems.ORGANIC_COMPOUND.get());
		generateSerum(ModItems.CORROSIVE_COMPOUND.get());
		generateSerum(ModItems.VITALIZING_COMPOUND.get());
		generateSerum(ModItems.HORMONE_COMPOUND.get());
		generateSerum(ModItems.EXOTIC_COMPOUND.get());

		generateWeapon(ModItems.BONE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
		generateWeaponWithOverlay(ModItems.BOOMLING.get(), OVERLAY_TEMPLATE);
		generateWeapon(ModItems.CONTROL_STAFF.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
		generateWeapon(ModItems.TOOTH_GUN.get(), GUN_TEMPLATE);
		generateWeapon(ModItems.WITHERSHOT.get(), GUN_TEMPLATE);

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(item -> generateFlat(item, EGG_MODEL_TEMPLATE));
	}

}
