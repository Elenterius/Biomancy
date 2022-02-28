package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.mixin.TextureSlotAccessor;
import com.google.gson.JsonElement;
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
	private static final ModelTemplate GUN_TEMPLATE = createTemplate("handheld_gun", TextureSlot.LAYER0, LAYER_1);
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

	public void run() {
		generateFlat(ModItems.FLESH_BITS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BONE_SCRAPS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.ELASTIC_FIBERS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MINERAL_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.PLANT_MATTER.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.TOUGH_FIBERS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIOTIC_MATTER.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.EXOTIC_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIO_MINERALS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.OXIDES.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIO_LUMENS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NUTRIENTS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LAPIDARY_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LITHIC_POWDER.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.PHEROMONES.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.TOXINS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BILE.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.BIOMETAL_INGOT.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.OCULUS.get(), ModelTemplates.FLAT_ITEM);

		generateFlatWithOverlay(ModItems.ESSENCE.get(), OVERLAY_TEMPLATE);
		generateFlat(ModItems.BIO_EXTRACTOR.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
		generateFlatWithOverlay(ModItems.BIO_INJECTOR.get(), GUN_TEMPLATE);
		generateFlat(ModItems.GLASS_VIAL.get(), ModelTemplates.FLAT_ITEM);
		generateFlatWithOverlay(ModItems.SERUM.get(), OVERLAY_TEMPLATE);

		generateFlat(ModItems.SHARP_TOOTH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.SKIN_CHUNK.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NECROTIC_FLESH_LUMP.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.STOMACH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.ANCIENT_STOMACH.get(), ModItems.STOMACH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LARYNX.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.PROTEIN_BAR.get(), ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.BONE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
		generateFlatWithOverlay(ModItems.BOOMLING.get(), OVERLAY_TEMPLATE);
		generateFlat(ModItems.CONTROL_STAFF.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(item -> generateFlat(item, EGG_MODEL_TEMPLATE));
	}

}
