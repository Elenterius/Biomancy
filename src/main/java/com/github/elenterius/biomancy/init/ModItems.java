package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.*;
import com.github.elenterius.biomancy.world.item.weapon.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final RegistryObject<Item> NECROTIC_FLESH_LUMP = ITEMS.register("necrotic_flesh_lump", () -> new Item(createProperties().food(ModFoods.NECROTIC_FLESH)));
	public static final RegistryObject<Item> SKIN_CHUNK = ITEMS.register("skin_chunk", () -> new Item(createProperties().food(ModFoods.POOR_FLESH)));
	public static final RegistryObject<Item> LARYNX = ITEMS.register("larynx", () -> new LarynxItem(createProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> MOB_FANG = ITEMS.register("mob_fang", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> MOB_CLAW = ITEMS.register("mob_claw", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> MOB_SINEW = ITEMS.register("mob_sinew", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> MOB_MARROW = ITEMS.register("mob_marrow", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> MOB_GLAND = ITEMS.register("mob_gland", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> VENOM_GLAND = ITEMS.register("venom_gland", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> VOLATILE_GLAND = ITEMS.register("volatile_gland", () -> new SimpleItem(createProperties()));
	//##Special
	public static final RegistryObject<Item> LIVING_FLESH = ITEMS.register("living_flesh", () -> new SimpleItem(createProperties().food(ModFoods.AVERAGE_FLESH)));
	public static final RegistryObject<Item> EXALTED_LIVING_FLESH = ITEMS.register("exalted_living_flesh", () -> new SimpleItem.WithFoilItem(createProperties().food(ModFoods.AVERAGE_FLESH)));

	//# Components
	//## Complex
	public static final RegistryObject<Item> FLESH_BITS = ITEMS.register("flesh_bits", () -> new SimpleItem(createProperties().food(ModFoods.POOR_FLESH)));
	public static final RegistryObject<Item> BONE_SCRAPS = ITEMS.register("bone_scraps", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> PLANT_MATTER = ITEMS.register("plant_matter", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> TOUGH_FIBERS = ITEMS.register("tough_fibers", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> ELASTIC_FIBERS = ITEMS.register("elastic_fibers", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> MINERAL_DUST = ITEMS.register("mineral_dust", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> GEM_DUST = ITEMS.register("gem_dust", () -> new SimpleItem(createProperties()));
	//## Basic
	public static final RegistryObject<Item> NUTRIENTS = ITEMS.register("nutrients", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> BIOTIC_MATTER = ITEMS.register("organic_matter", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> BIO_LUMENS = ITEMS.register("bio_lumens", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> EXOTIC_DUST = ITEMS.register("exotic_dust", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> BIO_MINERALS = ITEMS.register("bio_minerals", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> OXIDES = ITEMS.register("oxides", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> LITHIC_POWDER = ITEMS.register("lithic_powder", () -> new SimpleItem(createProperties()));
	//## Specific
	public static final RegistryObject<Item> REJUVENATING_MUCUS = ITEMS.register("rejuvenating_mucus", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> HORMONE_SECRETION = ITEMS.register("hormone_secretion", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> VENOM_EXTRACT = ITEMS.register("venom_extract", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> BILE_EXTRACT = ITEMS.register("bile_extract", () -> new SimpleItem(createProperties()));
	public static final RegistryObject<Item> VOLATILE_EXTRACT = ITEMS.register("volatile_extract", () -> new SimpleItem(createProperties()));

	//# Bio-Forge
	public static final RegistryObject<Item> BIOMETAL_INGOT = ITEMS.register("biometal_ingot", () -> new Item(createProperties()));
	public static final RegistryObject<Item> OCULUS = ITEMS.register("oculus", () -> new Item(createProperties().food(ModFoods.OCULUS)));

	//# Food
	public static final RegistryObject<Item> NUTRIENT_BAR = ITEMS.register("nutrient_bar", () -> new EffectCureItem(createProperties().food(ModFoods.NUTRIENT_BAR)));
	public static final RegistryObject<Item> PROTEIN_BAR = ITEMS.register("protein_bar", () -> new EffectCureItem(createProperties().food(ModFoods.PROTEIN_BAR)));

	//# Misc
	public static final RegistryObject<Item> GLASS_VIAL = ITEMS.register("glass_vial", () -> new Item(createProperties()));
	public static final RegistryObject<SerumItem> SERUM = ITEMS.register("serum", () -> new SerumItem(createProperties().stacksTo(8)));
	public static final RegistryObject<EssenceItem> ESSENCE = ITEMS.register("essence", () -> new EssenceItem(createProperties()));
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = ITEMS.register("bio_extractor", () -> new BioExtractorItem(createProperties().durability(200)));
	public static final RegistryObject<BioInjectorItem> BIO_INJECTOR = ITEMS.register("bio_injector", () -> new BioInjectorItem(createProperties().durability(200)));
	public static final RegistryObject<ControlStaffItem> CONTROL_STAFF = ITEMS.register("control_staff", () -> new ControlStaffItem(createProperties().stacksTo(1)));

	//# Weapons
	public static final RegistryObject<SwordItem> BONE_SWORD = ITEMS.register("bone_sword", () -> new SwordItem(ModTiers.BONE, 3, -2.4f, createProperties()));
	public static final RegistryObject<ToothGunItem> TOOTH_GUN = ITEMS.register("tooth_gun", () -> new ToothGunItem(createProperties().stacksTo(1).durability(ModTiers.LESSER_BIOMETAL.getUses()).rarity(Rarity.EPIC)));
	public static final RegistryObject<WithershotItem> WITHERSHOT = ITEMS.register("withershot", () -> new WithershotItem(createProperties().stacksTo(1).durability(ModTiers.BIOMETAL.getUses()).rarity(Rarity.EPIC)));
	public static final RegistryObject<LongClawItem> LONG_CLAW = ITEMS.register("long_range_claw", () -> new LongClawItem(ModTiers.BIOMETAL, 3, -2.4f, 60, createProperties().rarity(Rarity.EPIC)));

	//# Creature
	public static final RegistryObject<BoomlingItem> BOOMLING = ITEMS.register("boomling", () -> new BoomlingItem(createProperties().stacksTo(1)));

	//# Block Items
	//## Material
	public static final RegistryObject<BlockItem> FLESH_BLOCK = ITEMS.register("flesh_block", () -> new BlockItem(ModBlocks.FLESH_BLOCK.get(), createProperties()));
	public static final RegistryObject<BlockItem> FLESH_BLOCK_SLAB = ITEMS.register("flesh_block_slab", () -> new BlockItem(ModBlocks.FLESH_BLOCK_SLAB.get(), createProperties()));
	public static final RegistryObject<BlockItem> FLESH_BLOCK_STAIRS = ITEMS.register("flesh_block_stairs", () -> new BlockItem(ModBlocks.FLESH_BLOCK_STAIRS.get(), createProperties()));
	public static final RegistryObject<BlockItem> NECROTIC_FLESH_BLOCK = ITEMS.register("necrotic_flesh_block", () -> new BlockItem(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createProperties()));

	//## Machine
	public static final RegistryObject<CreatorBlockItem> CREATOR = ITEMS.register("creator", () -> new CreatorBlockItem(ModBlocks.CREATOR.get(), createProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> BIO_FORGE = ITEMS.register("bio_forge", () -> new BlockItem(ModBlocks.BIO_FORGE.get(), createProperties().rarity(Rarity.RARE)));
	public static final RegistryObject<BlockItem> DECOMPOSER = ITEMS.register("decomposer", () -> new BlockItem(ModBlocks.DECOMPOSER.get(), createProperties().rarity(Rarity.RARE)));
	public static final RegistryObject<BlockItem> GLAND = ITEMS.register("gland", () -> new BlockItem(ModBlocks.GLAND.get(), createProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> BIO_LAB = ITEMS.register("bio_lab", () -> new BlockItem(ModBlocks.BIO_LAB.get(), createProperties().rarity(Rarity.RARE)));

	//## Storage & Automation
	public static final RegistryObject<BlockItem> TONGUE = ITEMS.register("tongue", () -> new BlockItem(ModBlocks.TONGUE.get(), createProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> GULGE = ITEMS.register("gulge", () -> new BlockItem(ModBlocks.GULGE.get(), createProperties().rarity(Rarity.RARE)));
	public static final RegistryObject<BlockItem> SAC = ITEMS.register("sac", () -> new BlockItem(ModBlocks.SAC.get(), createProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> FLESH_CHEST = ITEMS.register("flesh_chest", () -> new BlockItem(ModBlocks.FLESH_CHEST.get(), createProperties().rarity(Rarity.UNCOMMON)));

	//## Misc
	public static final RegistryObject<BlockItem> VOICE_BOX = ITEMS.register("voice_box", () -> new BlockItem(ModBlocks.VOICE_BOX.get(), createProperties()));

	//# Spawn Eggs
	public static final RegistryObject<ForgeSpawnEggItem> FLESH_BLOB_SPAWN_EGG = ITEMS.register("flesh_blob_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6, createProperties()));
	public static final RegistryObject<ForgeSpawnEggItem> BOOMLING_SPAWN_EGG = ITEMS.register("boomling_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.BOOMLING, 0x3e3e3e, 0xcfcfcf, createProperties()));
	public static final RegistryObject<ForgeSpawnEggItem> FLESHKIN_SPAWN_EGG = ITEMS.register("fleshkin_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.FLESHKIN, 0xe9967a, 0xf6d2c6, createProperties()));

//	public static final RegistryObject<Item> BONE_GEAR = ITEMS.register("bone_gear", () -> new Item(createItemProperties()));
//	public static final RegistryObject<Item> SILICATE_PASTE = ITEMS.register("silicate_paste", () -> new Item(createItemProperties()));
//	public static final RegistryObject<Item> BOLUS = ITEMS.register("bolus", () -> new Item(createItemProperties())); //crushed biomass
//	public static final RegistryObject<Item> DIGESTATE = ITEMS.register("digestate", () -> new Item(createItemProperties()));
//	public static final RegistryObject<Item> MILK_GEL = ITEMS.register("milk_gel", () -> new MilkGelItem(createItemProperties().food(ModFoods.MILK_GEL)));
//	public static final RegistryObject<Item> MUTAGENIC_BILE = ITEMS.register("mutagenic_bile", () -> new Item(createItemProperties()));
//	public static final RegistryObject<Item> REJUVENATING_MUCUS = ITEMS.register("rejuvenating_mucus", () -> new Item(createItemProperties()));
//	public static final RegistryObject<DecayingItem> ERODING_BILE = ITEMS.register("eroding_bile", () -> new DecayingItem(6 * 60, 0.5f, createItemProperties()));

	//# Armor
//	public static final RegistryObject<OculiGogglesArmorItem> OCULI_OF_UNVEILING = ITEMS.register("unveiling_oculi", () -> new OculiGogglesArmorItem(ArmorMaterial.IRON, createItemProperties().rarity(Rarity.EPIC)));

	//# Weapons
//	public static final RegistryObject<BoomlingHiveGunItem> BOOMLING_HIVE_GUN = ITEMS.register("boomling_hive_gun", () -> new BoomlingHiveGunItem(createItemProperties().stacksTo(1).durability(ModItemTier.BIOMETAL.getUses()).rarity(Rarity.EPIC)));
//	public static final RegistryObject<BeeHiveGunItem> BEE_HIVE_GUN = ITEMS.register("bee_hive_gun", () -> new BeeHiveGunItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.BIOMETAL.getMaxUses()).rarity(Rarity.EPIC)));

	//# Tools
//	public static final RegistryObject<ItemStorageBagItem> SINGLE_ITEM_BAG_ITEM = ITEMS.register("single_item_bag", () -> new ItemStorageBagItem(createItemProperties().stacksTo(1).rarity(Rarity.UNCOMMON)));
//	public static final RegistryObject<EntityStorageBagItem> SMALL_ENTITY_BAG_ITEM = ITEMS.register("small_entity_bag", () -> new EntityStorageBagItem(4f, (byte) 1, createItemProperties().stacksTo(1).rarity(Rarity.UNCOMMON)));
//	public static final RegistryObject<EntityStorageBagItem> LARGE_ENTITY_BAG_ITEM = ITEMS.register("large_entity_bag", () -> new EntityStorageBagItem(10f, (byte) 8, createItemProperties().stacksTo(1).rarity(Rarity.UNCOMMON)));
//	public static final RegistryObject<AccessKeyItem> OCULUS_KEY = ITEMS.register("oculus_key", () -> new AccessKeyItem(createItemProperties()));
	// Adaptive Tools
//	public static final RegistryObject<AdaptivePickaxeItem> FLESHBORN_PICKAXE = ITEMS.register("fleshborn_pickaxe", () -> new AdaptivePickaxeItem(ModItemTier.LESSER_BIOMETAL, 1, -2.8f, createItemProperties().rarity(Rarity.UNCOMMON)));
//	public static final RegistryObject<AdaptiveShovelItem> FLESHBORN_SHOVEL = ITEMS.register("fleshborn_shovel", () -> new AdaptiveShovelItem(ModItemTier.LESSER_BIOMETAL, 1.5f, -3f, createItemProperties().rarity(Rarity.UNCOMMON)));
//	public static final RegistryObject<AdaptiveAxeItem> FLESHBORN_AXE = ITEMS.register("fleshborn_axe", () -> new AdaptiveAxeItem(ModItemTier.LESSER_BIOMETAL, 6f, -3f, createItemProperties().rarity(Rarity.UNCOMMON)));

	//# Fluids
//	public static final RegistryObject<Item> NUTRIENT_SLURRY_BUCKET = ITEMS.register("nutrient_slurry_bucket", () -> new BucketItem(ModFluids.NUTRIENT_SLURRY, createItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

	//# Spawn Eggs
//	public static final RegistryObject<ModSpawnEggItem> OCULUS_OBSERVER_SPAWN_EGG = ITEMS.register("oculus_observer_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.OCULUS_OBSERVER, 0xe9967a, 0xeff0f1, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> FAILED_SHEEP_SPAWN_EGG = ITEMS.register("failed_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> THICK_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("thick_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.THICK_WOOL_SHEEP, 0xe7e7e7, 0xf1ddcf, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> SILKY_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("silky_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.SILKY_WOOL_SHEEP, 0xe7e7e7, 0xfae6fa, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> FAILED_COW_SPAWN_EGG = ITEMS.register("failed_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_COW, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> NUTRIENT_COW_SPAWN_EGG = ITEMS.register("nutrient_slurry_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.NUTRIENT_SLURRY_COW, 0x443626, 0xccd65b, createItemProperties()));

//	public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEMS.register("brood_mother_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> CHROMA_SHEEP_SPAWN_EGG = ITEMS.register("chroma_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.CHROMA_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));

	/* **** Block Items ********************************************* */

	//bio-construct blocks
//	public static final RegistryObject<BlockItem> FLESHBORN_DOOR = ITEMS.register("fleshborn_door", () -> new BlockItem(ModBlocks.FLESHBORN_DOOR.get(), createItemProperties()));
//	public static final RegistryObject<BlockItem> FLESHBORN_TRAPDOOR = ITEMS.register("fleshborn_trapdoor", () -> new BlockItem(ModBlocks.FLESHBORN_TRAPDOOR.get(), createItemProperties()));
//	public static final RegistryObject<BlockItem> FLESHBORN_PRESSURE_PLATE = ITEMS.register("fleshborn_pressure_plate", () -> new BlockItem(ModBlocks.FLESHBORN_PRESSURE_PLATE.get(), createItemProperties()));
//	public static final RegistryObject<BlockItem> SCENT_DIFFUSER = ITEMS.register("scent_diffuser", () -> new BlockItem(ModBlocks.SCENT_DIFFUSER.get(), createItemProperties()));

	private ModItems() {}

	private static Item.Properties createProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB);
	}

}
