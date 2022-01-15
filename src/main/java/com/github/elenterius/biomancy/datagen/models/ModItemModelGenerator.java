package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.init.ModItems;
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

	private static final ModelTemplate EGG_MODEL_TEMPLATE = createTemplate("template_spawn_egg");

	private static ModelTemplate createTemplate(String id, TextureSlot... requiredSlots) {
		return new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/" + id)), Optional.empty(), requiredSlots);
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

	public void run() {
		generateFlat(ModItems.FLESH_BITS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BONE_SCRAPS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.ELASTIC_FIBERS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.MINERAL_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.GEM_FRAGMENTS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.SEDIMENT_FRAGMENTS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.PLANT_MATTER.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.TOUGH_FILAMENTS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIO_LUMENS.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.NUTRIENT_PASTE.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIOTIC_MATTER.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.EXOTIC_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BIO_MINERALS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.OXIDES.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LAPIDARY_DUST.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.LITHIC_POWDER.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.TOXINS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.BILE.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.BIOMETAL_INGOT.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.OCULUS.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.SHARP_TOOTH.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.SKIN_CHUNK.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NECROTIC_FLESH_LUMP.get(), ModItems.FLESH_BITS.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);
		generateFlat(ModItems.PROTEIN_BAR.get(), ModItems.NUTRIENT_BAR.get(), ModelTemplates.FLAT_ITEM);

		generateFlat(ModItems.BONE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(item -> generateFlat(item, EGG_MODEL_TEMPLATE));
	}

}
