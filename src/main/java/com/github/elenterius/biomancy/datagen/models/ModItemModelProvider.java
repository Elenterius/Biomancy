package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {

	protected static final String LAYER_0_TEXTURE = "layer0";

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private ItemModelBuilder spawnEggItem(ResourceLocation item) {
		return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
	}

	public ItemModelBuilder spawnEggItem(Item item) {
		return spawnEggItem(Objects.requireNonNull(item.getRegistryName()));
	}

	public ItemModelBuilder componentItem(Item item) {
		return basicItem(Objects.requireNonNull(item.getRegistryName()), "component");
	}

	public ItemModelBuilder serumItem(Item item) {
		return basicItem(Objects.requireNonNull(item.getRegistryName()), "serum");
	}

	private ItemModelBuilder basicItem(ResourceLocation item, String subfolder) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + subfolder + "/" + item.getPath()));
	}

	public ItemModelBuilder overlayItem(Item item) {
		return overlayItem(Objects.requireNonNull(item.getRegistryName()));
	}

	private ItemModelBuilder overlayItem(ResourceLocation item) {
		return basicItem(item).texture("layer1", new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + item.getPath() + "_overlay"));
	}

	public ItemModelBuilder handheldItem(Item item) {
		return handheldItem(Objects.requireNonNull(item.getRegistryName()));
	}

	private ItemModelBuilder handheldItem(ResourceLocation item) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + item.getPath()));
	}

	private ItemModelBuilder flatBlockItem(BlockItem item) {
		ResourceLocation id = Objects.requireNonNull(item.getBlock().getRegistryName());
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(id.getNamespace(), BLOCK_FOLDER + "/" + id.getPath()));
	}

	@Override
	protected void registerModels() {
		basicItem(ModItems.MOB_FANG.get());
		basicItem(ModItems.MOB_CLAW.get());
		basicItem(ModItems.MOB_SINEW.get());
		basicItem(ModItems.MOB_MARROW.get());
		basicItem(ModItems.WITHERED_MOB_MARROW.get());
		basicItem(ModItems.GENERIC_MOB_GLAND.get());
		basicItem(ModItems.TOXIN_GLAND.get());
		basicItem(ModItems.VOLATILE_GLAND.get());
		basicItem(ModItems.LIVING_FLESH.get());
		basicItem(ModItems.EXALTED_LIVING_FLESH.get());

		componentItem(ModItems.FLESH_BITS.get());
		componentItem(ModItems.BONE_FRAGMENTS.get());
		componentItem(ModItems.ELASTIC_FIBERS.get());
		componentItem(ModItems.MINERAL_FRAGMENT.get());
		componentItem(ModItems.TOUGH_FIBERS.get());
		componentItem(ModItems.ORGANIC_MATTER.get());
		componentItem(ModItems.EXOTIC_DUST.get());
		componentItem(ModItems.BIO_MINERALS.get());
		componentItem(ModItems.BIO_LUMENS.get());
		componentItem(ModItems.NUTRIENTS.get());
		componentItem(ModItems.GEM_FRAGMENTS.get());
		componentItem(ModItems.STONE_POWDER.get());

		componentItem(ModItems.REGENERATIVE_FLUID.get());
		componentItem(ModItems.WITHERING_OOZE.get());
		componentItem(ModItems.HORMONE_SECRETION.get());
		componentItem(ModItems.TOXIN_EXTRACT.get());
		componentItem(ModItems.VOLATILE_FLUID.get());
		componentItem(ModItems.BILE.get());

		basicItem(ModItems.FERTILIZER.get());
		basicItem(ModItems.CREATOR_MIX.get());
		basicItem(ModItems.NUTRIENT_PASTE.get());
		basicItem(ModItems.NUTRIENT_BAR.get());
		basicItem(ModItems.GLASS_VIAL.get());
		basicItem(ModItems.FLESH_DOOR.get());

		serumItem(ModItems.REJUVENATION_SERUM.get());
		serumItem(ModItems.GROWTH_SERUM.get());
		serumItem(ModItems.BREEDING_STIMULANT.get());
		serumItem(ModItems.ABSORPTION_BOOST.get());
		serumItem(ModItems.CLEANSING_SERUM.get());
		serumItem(ModItems.INSOMNIA_CURE.get());

		serumItem(ModItems.ORGANIC_COMPOUND.get());
		serumItem(ModItems.UNSTABLE_COMPOUND.get());
		serumItem(ModItems.GENETIC_COMPOUND.get());
		serumItem(ModItems.EXOTIC_COMPOUND.get());
		serumItem(ModItems.HEALING_ADDITIVE.get());
		serumItem(ModItems.CORROSIVE_ADDITIVE.get());

		overlayItem(ModItems.ESSENCE.get());

		flatBlockItem(ModItems.FLESH_LADDER.get());
		flatBlockItem(ModItems.MALIGNANT_FLESH_VEINS.get());

		handheldItem(ModItems.BIO_EXTRACTOR.get());

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(this::spawnEggItem);
	}

}
