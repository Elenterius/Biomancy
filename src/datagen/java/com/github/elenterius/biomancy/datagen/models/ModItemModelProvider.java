package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.weapon.DespoilingSwordItem;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {

	protected static final String LAYER_0_TEXTURE = "layer0";
	protected static final String LAYER_1_TEXTURE = "layer1";

	public ModItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
		super(packOutput, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static ResourceLocation registryKey(Item item) {
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
	}

	@Override
	protected void registerModels() {
		lootItem(ModItems.MOB_FANG);
		lootItem(ModItems.MOB_CLAW);
		lootItem(ModItems.MOB_SINEW);
		lootItem(ModItems.MOB_MARROW);
		lootItem(ModItems.WITHERED_MOB_MARROW);
		lootItem(ModItems.GENERIC_MOB_GLAND);
		lootItem(ModItems.TOXIN_GLAND);
		lootItem(ModItems.VOLATILE_GLAND);
		lootItem(ModItems.LIVING_FLESH);

		componentItem(ModItems.FLESH_BITS);
		componentItem(ModItems.BONE_FRAGMENTS);
		componentItem(ModItems.ELASTIC_FIBERS);
		componentItem(ModItems.MINERAL_FRAGMENT);
		componentItem(ModItems.TOUGH_FIBERS);
		componentItem(ModItems.ORGANIC_MATTER);
		componentItem(ModItems.EXOTIC_DUST);
		componentItem(ModItems.BIO_LUMENS);
		componentItem(ModItems.NUTRIENTS);
		componentItem(ModItems.GEM_FRAGMENTS);
		componentItem(ModItems.STONE_POWDER);

		componentItem(ModItems.REGENERATIVE_FLUID);
		componentItem(ModItems.WITHERING_OOZE);
		componentItem(ModItems.HORMONE_SECRETION);
		componentItem(ModItems.TOXIN_EXTRACT);
		componentItem(ModItems.VOLATILE_FLUID);
		componentItem(ModItems.BILE);

		serumItem(ModItems.REJUVENATION_SERUM);
		serumItem(ModItems.AGEING_SERUM);
		serumItem(ModItems.ENLARGEMENT_SERUM);
		serumItem(ModItems.SHRINKING_SERUM);
		serumItem(ModItems.BREEDING_STIMULANT);
		serumItem(ModItems.ABSORPTION_BOOST);
		serumItem(ModItems.CLEANSING_SERUM);
		serumItem(ModItems.INSOMNIA_CURE);
		serumItem(ModItems.FRENZY_SERUM);

		serumItem(ModItems.ORGANIC_COMPOUND);
		serumItem(ModItems.UNSTABLE_COMPOUND);
		serumItem(ModItems.GENETIC_COMPOUND);
		serumItem(ModItems.EXOTIC_COMPOUND);
		serumItem(ModItems.HEALING_ADDITIVE);
		serumItem(ModItems.CORROSIVE_ADDITIVE);

		emissiveItem(ModItems.PRIMORDIAL_CORE);
		fleshPlunderer(ModItems.DESPOIL_SICKLE);
		basicItem(ModItems.CREATOR_MIX);
		basicItem(ModItems.NUTRIENT_PASTE);
		basicItem(ModItems.NUTRIENT_BAR);
		basicItem(ModItems.BLOOMBERRY);
		serumItem(ModItems.VIAL);
		basicItem(ModItems.GIFT_SAC);
		basicItem(ModItems.FERTILIZER);
		overlayItem(ModItems.ESSENCE);

		armorItem(ModItems.ACOLYTE_ARMOR_HELMET);
		armorItem(ModItems.ACOLYTE_ARMOR_CHESTPLATE);
		armorItem(ModItems.ACOLYTE_ARMOR_LEGGINGS);
		armorItem(ModItems.ACOLYTE_ARMOR_BOOTS);

		basicItem(ModItems.FLESH_DOOR);
		basicItem(ModItems.FULL_FLESH_DOOR);
		wallBlockItem(ModItems.FLESH_WALL);
		wallBlockItem(ModItems.PACKED_FLESH_WALL);
		wallBlockItem(ModItems.FIBROUS_FLESH_WALL);
		wallBlockItem(ModItems.MALIGNANT_FLESH_WALL);
		wallBlockItem(ModItems.PRIMAL_FLESH_WALL);
		wallBlockItem(ModItems.SMOOTH_PRIMAL_FLESH_WALL);
		wallBlockItem(ModItems.POROUS_PRIMAL_FLESH_WALL);
		flatBlockItem(ModItems.FLESH_LADDER);
		flatBlockItem(ModItems.MALIGNANT_FLESH_VEINS);

		dynamicBucket(ModItems.ACID_BUCKET.get());

		//generate models for all eggs
		ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(SpawnEggItem.class::isInstance).forEach(this::spawnEggItem);
	}

	public <T extends Item> void emissiveItem(RegistryObject<T> registryObject) {
		emissiveItem(registryObject.getId());
	}

	public void emissiveItem(ResourceLocation registryKey) {
		getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.customLoader(ItemLayerModelBuilder::begin).emissive(0xf, 0xf, 1).end()
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/" + registryKey.getPath()))
				.texture(LAYER_1_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/" + registryKey.getPath() + "_emissive"));
	}

	public <T extends DespoilingSwordItem> void fleshPlunderer(RegistryObject<T> registryObject) {
		ResourceLocation registryKey = registryObject.getId();
		getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.customLoader(ItemLayerModelBuilder::begin).emissive(0xf, 0xf, 1).end()
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/weapon/" + registryKey.getPath()))
				.texture(LAYER_1_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/weapon/" + registryKey.getPath() + "_emissive"));
	}

	public <T extends Item> ItemModelBuilder basicItem(RegistryObject<T> registryObject) {
		return basicItem(registryObject.getId());
	}

	public ItemModelBuilder spawnEggItem(Item item) {
		return spawnEggItem(registryKey(item));
	}

	public ItemModelBuilder spawnEggItem(ResourceLocation registryKey) {
		return getBuilder(registryKey.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
	}

	public <T extends Item> ItemModelBuilder lootItem(RegistryObject<T> registryObject) {
		return basicItem(registryObject.getId(), "loot");
	}

	public ItemModelBuilder lootItem(Item item) {
		return basicItem(registryKey(item), "loot");
	}

	public <T extends Item> ItemModelBuilder componentItem(RegistryObject<T> registryObject) {
		return basicItem(registryObject.getId(), "component");
	}

	public ItemModelBuilder componentItem(Item item) {
		return basicItem(registryKey(item), "component");
	}

	public <T extends Item> ItemModelBuilder serumItem(RegistryObject<T> registryObject) {
		return basicItem(registryObject.getId(), "serum");
	}

	public ItemModelBuilder serumItem(Item item) {
		return basicItem(registryKey(item), "serum");
	}

	public <T extends Item> ItemModelBuilder armorItem(RegistryObject<T> registryObject) {
		return basicItem(registryObject.getId(), "armor");
	}

	public ItemModelBuilder genericSerumItem(Item item) {
		ResourceLocation rl = registryKey(item);
		return getBuilder(rl.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(rl.getNamespace(), ITEM_FOLDER + "/serum/generic_serum"))
				.texture(LAYER_1_TEXTURE, new ResourceLocation(rl.getNamespace(), ITEM_FOLDER + "/serum/generic_serum_overlay"));
	}

	public ItemModelBuilder weaponItem(Item item) {
		return basicItem(registryKey(item), "weapon");
	}

	public ItemModelBuilder handheldWeaponItem(Item item) {
		return handheldItem(registryKey(item), "weapon");
	}

	public <T extends Item> ItemModelBuilder handheldWeaponItem(RegistryObject<T> registryObject) {
		return handheldItem(registryObject.getId(), "weapon");
	}

	public ItemModelBuilder miscItem(Item item) {
		return basicItem(registryKey(item), "misc");
	}

	public ItemModelBuilder basicItem(ResourceLocation registryKey, String subfolder) {
		return getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/" + subfolder + "/" + registryKey.getPath()));
	}

	public ItemModelBuilder overlayItem(Item item) {
		return overlayItem(registryKey(item));
	}

	public <T extends Item> ItemModelBuilder overlayItem(RegistryObject<T> registryObject) {
		return overlayItem(registryObject.getId());
	}

	public ItemModelBuilder overlayItem(ResourceLocation registryKey) {
		String texturePath = ITEM_FOLDER + "/" + registryKey.getPath() + "_overlay";
		return basicItem(registryKey).texture(LAYER_1_TEXTURE, new ResourceLocation(registryKey.getNamespace(), texturePath));
	}

	public ItemModelBuilder handheldItem(Item item) {
		return handheldItem(registryKey(item));
	}

	public <T extends Item> ItemModelBuilder handheldItem(RegistryObject<T> registryObject) {
		return handheldItem(registryObject.getId());
	}

	public ItemModelBuilder handheldItem(ResourceLocation registryKey) {
		return getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/" + registryKey.getPath()));
	}

	public ItemModelBuilder handheldItem(ResourceLocation registryKey, String subfolder) {
		return getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), ITEM_FOLDER + "/" + subfolder + "/" + registryKey.getPath()));
	}

	public <T extends BlockItem> ItemModelBuilder flatBlockItem(RegistryObject<T> registryObject) {
		return flatBlockItem(registryObject.getId());
	}

	public ItemModelBuilder flatBlockItem(BlockItem blockItem) {
		return flatBlockItem(registryKey(blockItem));
	}

	public ItemModelBuilder flatBlockItem(ResourceLocation registryKey) {
		return getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture(LAYER_0_TEXTURE, new ResourceLocation(registryKey.getNamespace(), BLOCK_FOLDER + "/" + registryKey.getPath()));
	}

	public <T extends BlockItem> ItemModelBuilder wallBlockItem(RegistryObject<T> registryObject) {
		return wallBlockItem(registryObject.getId());
	}

	public ItemModelBuilder wallBlockItem(BlockItem blockItem) {
		return wallBlockItem(registryKey(blockItem));
	}

	public ItemModelBuilder wallBlockItem(ResourceLocation registryKey) {
		return getBuilder(registryKey.toString())
				.parent(new ModelFile.UncheckedModelFile(BLOCK_FOLDER + "/wall_inventory"))
				.texture("wall", new ResourceLocation(registryKey.getNamespace(), BLOCK_FOLDER + "/" + registryKey.getPath().replace("_wall", "")));
	}

	public ItemModelBuilder dynamicBucket(BucketItem item) {
		ResourceLocation itemKey = registryKey(item);
		ResourceLocation fluidKey = Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(item.getFluid()));
		ResourceLocation loaderKey = new ResourceLocation("forge", "fluid_container");
		ResourceLocation bucketModelKey = new ResourceLocation("forge", "item/bucket");

		return getBuilder(itemKey.toString())
				.parent(getExistingFile(bucketModelKey))
				.customLoader((builder, existingFileHelper) -> new CustomLoaderBuilder<ItemModelBuilder>(loaderKey, builder, existingFileHelper) {
					@Override
					public JsonObject toJson(JsonObject json) {
						JsonObject json1 = super.toJson(json);
						json1.addProperty("fluid", fluidKey.toString());
						return json1;
					}
				}).end();
	}
}
