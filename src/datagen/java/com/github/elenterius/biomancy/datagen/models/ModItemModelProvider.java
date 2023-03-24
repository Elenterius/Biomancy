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
import net.minecraftforge.registries.ForgeRegistries;
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
		return spawnEggItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	public ItemModelBuilder componentItem(Item item) {
		return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), "component");
	}

	public ItemModelBuilder serumItem(Item item) {
		return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), "serum");
	}

	public ItemModelBuilder genericSerumItem(Item item) {
		ResourceLocation rl = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
		return getBuilder(rl.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(rl.getNamespace(), ITEM_FOLDER + "/serum/generic_serum"))
				.texture("layer1", new ResourceLocation(rl.getNamespace(), ITEM_FOLDER + "/serum/generic_serum_overlay"));
	}

	public ItemModelBuilder weaponItem(Item item) {
		return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), "weapon");
	}

	public ItemModelBuilder handheldWeaponItem(Item item) {
		return handheldItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), "weapon");
	}

	public ItemModelBuilder miscItem(Item item) {
		return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), "misc");
	}

	private ItemModelBuilder basicItem(ResourceLocation item, String subfolder) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + subfolder + "/" + item.getPath()));
	}

	public ItemModelBuilder overlayItem(Item item) {
		return overlayItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	private ItemModelBuilder overlayItem(ResourceLocation item) {
		return basicItem(item).texture("layer1", new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + item.getPath() + "_overlay"));
	}

	public ItemModelBuilder handheldItem(Item item) {
		return handheldItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	private ItemModelBuilder handheldItem(ResourceLocation item) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + item.getPath()));
	}

	private ItemModelBuilder handheldItem(ResourceLocation item, String subfolder) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(item.getNamespace(), ITEM_FOLDER + "/" + subfolder + "/" + item.getPath()));
	}

	public ItemModelBuilder flatBlockItem(BlockItem blockItem) {
		return flatBlockItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(blockItem)));
	}

	private ItemModelBuilder flatBlockItem(ResourceLocation blockItem) {
		return getBuilder(blockItem.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(blockItem.getNamespace(), BLOCK_FOLDER + "/" + blockItem.getPath()));
	}

	public ItemModelBuilder wallBlockItem(BlockItem blockItem) {
		return wallBlockItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(blockItem)));
	}

	private ItemModelBuilder wallBlockItem(ResourceLocation blockItem) {
		return getBuilder(blockItem.toString())
				.parent(new ModelFile.UncheckedModelFile(BLOCK_FOLDER + "/wall_inventory"))
				.texture("wall", new ResourceLocation(blockItem.getNamespace(), BLOCK_FOLDER + "/" + blockItem.getPath().replace("_wall", "")));
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
		basicItem(ModItems.PRIMORDIAL_LIVING_FLESH.get());
		basicItem(ModItems.PRIMORDIAL_LIVING_OCULUS.get());

		componentItem(ModItems.FLESH_BITS.get());
		componentItem(ModItems.BONE_FRAGMENTS.get());
		componentItem(ModItems.ELASTIC_FIBERS.get());
		componentItem(ModItems.MINERAL_FRAGMENT.get());
		componentItem(ModItems.TOUGH_FIBERS.get());
		componentItem(ModItems.ORGANIC_MATTER.get());
		componentItem(ModItems.EXOTIC_DUST.get());
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
		basicItem(ModItems.FULL_FLESH_DOOR.get());

		serumItem(ModItems.REJUVENATION_SERUM.get());
		serumItem(ModItems.AGEING_SERUM.get());
		genericSerumItem(ModItems.ENLARGEMENT_SERUM.get());
		genericSerumItem(ModItems.SHRINKING_SERUM.get());
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
		handheldWeaponItem(ModItems.BONE_CLEAVER.get());

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(this::spawnEggItem);

		wallBlockItem(ModItems.FLESH_WALL.get());
		wallBlockItem(ModItems.PACKED_FLESH_WALL.get());
	}

}
