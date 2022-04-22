package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.*;
import com.github.elenterius.biomancy.world.item.weapon.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final RegistryObject<Item> LARYNX = ITEMS.register("larynx", () -> new LarynxItem(createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<Item> MOB_FANG = ITEMS.register("mob_fang", ModItems::createSimpleItem);
	public static final RegistryObject<Item> MOB_CLAW = ITEMS.register("mob_claw", ModItems::createSimpleItem);
	public static final RegistryObject<Item> MOB_SINEW = ITEMS.register("mob_sinew", ModItems::createSimpleItem);
	public static final RegistryObject<Item> MOB_MARROW = ITEMS.register("mob_marrow", ModItems::createSimpleItem);
	public static final RegistryObject<Item> WITHERED_MOB_MARROW = ITEMS.register("withered_mob_marrow", ModItems::createSimpleItem);
	public static final RegistryObject<Item> MOB_GLAND = ITEMS.register("mob_gland", ModItems::createSimpleItem);
	public static final RegistryObject<Item> VENOM_GLAND = ITEMS.register("venom_gland", ModItems::createSimpleItem);
	public static final RegistryObject<Item> VOLATILE_GLAND = ITEMS.register("volatile_gland", ModItems::createSimpleItem);
	//## Special
	public static final RegistryObject<Item> LIVING_FLESH = ITEMS.register("living_flesh", ModItems::createSimpleItem);
	public static final RegistryObject<Item> EXALTED_LIVING_FLESH = ITEMS.register("exalted_living_flesh", () -> new SimpleItem.WithFoilItem(createBaseProperties()));

	//# Components
	//## Complex
	public static final RegistryObject<Item> FLESH_BITS = ITEMS.register("flesh_bits", ModItems::createSimpleItem);
	public static final RegistryObject<Item> BONE_SCRAPS = ITEMS.register("bone_scraps", ModItems::createSimpleItem);
	public static final RegistryObject<Item> TOUGH_FIBERS = ITEMS.register("tough_fibers", ModItems::createSimpleItem);
	public static final RegistryObject<Item> ELASTIC_FIBERS = ITEMS.register("elastic_fibers", ModItems::createSimpleItem);
	public static final RegistryObject<Item> MINERAL_DUST = ITEMS.register("mineral_dust", ModItems::createSimpleItem);
	public static final RegistryObject<Item> GEM_DUST = ITEMS.register("gem_dust", ModItems::createSimpleItem);
	//## Basic
	public static final RegistryObject<Item> NUTRIENTS = ITEMS.register("nutrients", ModItems::createSimpleItem);
	public static final RegistryObject<Item> ORGANIC_MATTER = ITEMS.register("organic_matter", ModItems::createSimpleItem);
	public static final RegistryObject<Item> BIO_LUMENS = ITEMS.register("bio_lumens", ModItems::createSimpleItem);
	public static final RegistryObject<Item> EXOTIC_DUST = ITEMS.register("exotic_dust", ModItems::createSimpleItem);
	public static final RegistryObject<Item> BIO_MINERALS = ITEMS.register("bio_minerals", ModItems::createSimpleItem);
	public static final RegistryObject<Item> LITHIC_POWDER = ITEMS.register("lithic_powder", ModItems::createSimpleItem);
	//## Specific
	public static final RegistryObject<Item> REJUVENATING_MUCUS = ITEMS.register("rejuvenating_mucus", ModItems::createSimpleItem);
	public static final RegistryObject<Item> HORMONE_SECRETION = ITEMS.register("hormone_secretion", ModItems::createSimpleItem);
	public static final RegistryObject<Item> VENOM_EXTRACT = ITEMS.register("venom_extract", ModItems::createSimpleItem);
	public static final RegistryObject<Item> BILE_EXTRACT = ITEMS.register("bile_extract", ModItems::createSimpleItem);
	public static final RegistryObject<Item> VOLATILE_EXTRACT = ITEMS.register("volatile_extract", ModItems::createSimpleItem);

	//# Serum
	public static final RegistryObject<Item> GLASS_VIAL = ITEMS.register("glass_vial", ModItems::createSimpleItem);
	public static final RegistryObject<Item> ORGANIC_COMPOUND = ITEMS.register("organic_compound", ModItems::createSimpleItem);
	public static final RegistryObject<Item> UNSTABLE_COMPOUND = ITEMS.register("unstable_compound", ModItems::createSimpleItem);
	public static final RegistryObject<Item> GENETIC_COMPOUND = ITEMS.register("genetic_compound", ModItems::createSimpleItem);

	public static final RegistryObject<SerumItem> GENERIC_SERUM = ITEMS.register("generic_serum", () -> new SerumItem.Generic(createBaseProperties().stacksTo(8)));
	public static final RegistryObject<SerumItem> REJUVENATION_SERUM = ITEMS.register("rejuvenation_serum", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.REJUVENATION_SERUM));
	public static final RegistryObject<SerumItem> GROWTH_SERUM = ITEMS.register("growth_serum", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.GROWTH_SERUM));
	public static final RegistryObject<SerumItem> BREEDING_STIMULANT = ITEMS.register("breeding_stimulant", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.BREEDING_STIMULANT));
	public static final RegistryObject<SerumItem> ABSORPTION_BOOST = ITEMS.register("absorption_boost", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.ABSORPTION_BOOST));
	public static final RegistryObject<SerumItem> CLEANSING_SERUM = ITEMS.register("cleansing_serum", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.CLEANSING_SERUM));
	public static final RegistryObject<SerumItem> INSOMNIA_CURE = ITEMS.register("insomnia_cure", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.INSOMNIA_CURE));
	public static final RegistryObject<SerumItem> ADRENALINE_SERUM = ITEMS.register("adrenaline_serum", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.ADRENALINE_SERUM));
	public static final RegistryObject<SerumItem> DECAY_AGENT = ITEMS.register("decay_agent", () -> new SerumItem(createBaseProperties().stacksTo(8), ModSerums.DECAY_AGENT));

	public static final RegistryObject<Item> ICHOR_SERUM = ITEMS.register("ichor_serum", () -> new SimpleItem.WithFoilItem(createBaseProperties()));

	//# Misc
	public static final RegistryObject<Item> OCULUS = ITEMS.register("oculus", () -> new SimpleItem(createBaseProperties().food(ModFoods.OCULUS)));
	public static final RegistryObject<EssenceItem> ESSENCE = ITEMS.register("essence", () -> new EssenceItem(createBaseProperties()));
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = ITEMS.register("bio_extractor", () -> new BioExtractorItem(createBaseProperties().durability(200)));
	public static final RegistryObject<BioInjectorItem> BIO_INJECTOR = ITEMS.register("bio_injector", () -> new BioInjectorItem(createBaseProperties().durability(200)));
	public static final RegistryObject<ControlStaffItem> CONTROL_STAFF = ITEMS.register("control_staff", () -> new ControlStaffItem(createBaseProperties().stacksTo(1)));

	//# Weapons
	public static final RegistryObject<SwordItem> BONE_SWORD = ITEMS.register("bone_sword", () -> new SwordItem(ModTiers.BONE, 3, -2.4f, createBaseProperties()));
	public static final RegistryObject<ToothGunItem> TOOTH_GUN = ITEMS.register("tooth_gun", () -> new ToothGunItem(createBaseProperties().stacksTo(1).durability(ModTiers.LESSER_BIOFLESH.getUses()).rarity(ModRarities.EXOTIC)));
	public static final RegistryObject<WithershotItem> WITHERSHOT = ITEMS.register("withershot", () -> new WithershotItem(createBaseProperties().stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.EXOTIC)));
	public static final RegistryObject<LongClawItem> LONG_CLAW = ITEMS.register("long_range_claw", () -> new LongClawItem(ModTiers.BIOFLESH, 3, -2.4f, 60, createBaseProperties().rarity(ModRarities.EXOTIC)));

	//# Creature
	public static final RegistryObject<BoomlingItem> BOOMLING = ITEMS.register("boomling", () -> new BoomlingItem(createBaseProperties().stacksTo(1)));

	//# Food/Fuel
	public static final RegistryObject<Item> NUTRIENT_PASTE = ITEMS.register("nutrient_paste", ModItems::createSimpleItem);
	public static final RegistryObject<Item> NUTRIENT_BAR = ITEMS.register("nutrient_bar", () -> new EffectCureItem(createBaseProperties().food(ModFoods.NUTRIENT_BAR)));
//	public static final RegistryObject<Item> PROTEIN_BAR = ITEMS.register("protein_bar", () -> new EffectCureItem(createProperties().food(ModFoods.PROTEIN_BAR)));

	//# Block Items

	//## Machine
	public static final RegistryObject<BEWLBlockItem> CREATOR = ITEMS.register("creator", () -> new BEWLBlockItem(ModBlocks.CREATOR.get(), createBaseProperties().rarity(ModRarities.EXOTIC)));
	public static final RegistryObject<SimpleBlockItem> BIO_FORGE = ITEMS.register("bio_forge", () -> new SimpleBlockItem(ModBlocks.BIO_FORGE.get(), createBaseProperties().rarity(ModRarities.MACHINE)));
	public static final RegistryObject<SimpleBlockItem> DECOMPOSER = ITEMS.register("decomposer", () -> new SimpleBlockItem(ModBlocks.DECOMPOSER.get(), createBaseProperties().rarity(ModRarities.MACHINE)));
	//	public static final RegistryObject<SimpleBlockItem> GLAND = ITEMS.register("gland", () -> new SimpleBlockItem(ModBlocks.GLAND.get(), createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<BEWLBlockItem> BIO_LAB = ITEMS.register("bio_lab", () -> new BEWLBlockItem(ModBlocks.BIO_LAB.get(), createBaseProperties().rarity(ModRarities.MACHINE)));
	public static final RegistryObject<SimpleBlockItem> DIGESTER = ITEMS.register("digester", () -> new SimpleBlockItem(ModBlocks.DIGESTER.get(), createBaseProperties().rarity(ModRarities.MACHINE)));

	//## Storage & Automation
	public static final RegistryObject<SimpleBlockItem> TONGUE = ITEMS.register("tongue", () -> new SimpleBlockItem(ModBlocks.TONGUE.get(), createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<SimpleBlockItem> GULGE = ITEMS.register("gulge", () -> new SimpleBlockItem(ModBlocks.GULGE.get(), createBaseProperties().rarity(ModRarities.EXOTIC)));
	public static final RegistryObject<SimpleBlockItem> SAC = ITEMS.register("sac", () -> new SimpleBlockItem(ModBlocks.SAC.get(), createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_CHEST = ITEMS.register("fleshkin_chest", () -> new SimpleBlockItem(ModBlocks.FLESHKIN_CHEST.get(), createBaseProperties().rarity(ModRarities.UNCOMMON)));

	//# Ownable
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_DOOR = ITEMS.register("fleshkin_door", () -> new SimpleBlockItem(ModBlocks.FLESHKIN_DOOR.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_TRAPDOOR = ITEMS.register("fleshkin_trapdoor", () -> new SimpleBlockItem(ModBlocks.FLESHKIN_TRAPDOOR.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_PRESSURE_PLATE = ITEMS.register("fleshkin_pressure_plate", () -> new SimpleBlockItem(ModBlocks.FLESHKIN_PRESSURE_PLATE.get(), createBaseProperties()));

	//## Misc
	public static final RegistryObject<SimpleBlockItem> FLESH_BLOCK = ITEMS.register("flesh_block", () -> new SimpleBlockItem(ModBlocks.FLESH_BLOCK.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> FLESH_BLOCK_SLAB = ITEMS.register("flesh_block_slab", () -> new SimpleBlockItem(ModBlocks.FLESH_BLOCK_SLAB.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> FLESH_BLOCK_STAIRS = ITEMS.register("flesh_block_stairs", () -> new SimpleBlockItem(ModBlocks.FLESH_BLOCK_STAIRS.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> NECROTIC_FLESH_BLOCK = ITEMS.register("necrotic_flesh_block", () -> new SimpleBlockItem(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createBaseProperties()));

	public static final RegistryObject<SimpleBlockItem> VOICE_BOX = ITEMS.register("voice_box", () -> new SimpleBlockItem(ModBlocks.VOICE_BOX.get(), createBaseProperties()));
	public static final RegistryObject<SimpleBlockItem> FLESH_IRISDOOR = ITEMS.register("flesh_irisdoor", () -> new SimpleBlockItem(ModBlocks.FLESH_IRISDOOR.get(), createBaseProperties()));

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
//	public static final RegistryObject<OculiGogglesArmorItem> OCULI_OF_UNVEILING = ITEMS.register("unveiling_oculi", () -> new OculiGogglesArmorItem(ArmorMaterial.IRON, createItemProperties().rarity(ModRarities.EXOTIC)));

	//# Weapons
//	public static final RegistryObject<BoomlingHiveGunItem> BOOMLING_HIVE_GUN = ITEMS.register("boomling_hive_gun", () -> new BoomlingHiveGunItem(createItemProperties().stacksTo(1).durability(ModItemTier.BIOMETAL.getUses()).rarity(ModRarities.EXOTIC)));
//	public static final RegistryObject<BeeHiveGunItem> BEE_HIVE_GUN = ITEMS.register("bee_hive_gun", () -> new BeeHiveGunItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.BIOMETAL.getMaxUses()).rarity(ModRarities.EXOTIC)));

	//# Tools
//	public static final RegistryObject<ItemStorageBagItem> SINGLE_ITEM_BAG_ITEM = ITEMS.register("single_item_bag", () -> new ItemStorageBagItem(createItemProperties().stacksTo(1).rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<EntityStorageBagItem> SMALL_ENTITY_BAG_ITEM = ITEMS.register("small_entity_bag", () -> new EntityStorageBagItem(4f, (byte) 1, createItemProperties().stacksTo(1).rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<EntityStorageBagItem> LARGE_ENTITY_BAG_ITEM = ITEMS.register("large_entity_bag", () -> new EntityStorageBagItem(10f, (byte) 8, createItemProperties().stacksTo(1).rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AccessKeyItem> OCULUS_KEY = ITEMS.register("oculus_key", () -> new AccessKeyItem(createItemProperties()));
	// Adaptive Tools
//	public static final RegistryObject<AdaptivePickaxeItem> FLESHBORN_PICKAXE = ITEMS.register("fleshborn_pickaxe", () -> new AdaptivePickaxeItem(ModItemTier.LESSER_BIOMETAL, 1, -2.8f, createItemProperties().rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AdaptiveShovelItem> FLESHBORN_SHOVEL = ITEMS.register("fleshborn_shovel", () -> new AdaptiveShovelItem(ModItemTier.LESSER_BIOMETAL, 1.5f, -3f, createItemProperties().rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AdaptiveAxeItem> FLESHBORN_AXE = ITEMS.register("fleshborn_axe", () -> new AdaptiveAxeItem(ModItemTier.LESSER_BIOMETAL, 6f, -3f, createItemProperties().rarity(ModRarities.UNCOMMON)));

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


//	public static final RegistryObject<SimpleBlockItem> SCENT_DIFFUSER = ITEMS.register("scent_diffuser", () -> new SimpleBlockItem(ModBlocks.SCENT_DIFFUSER.get(), createItemProperties()));

	private ModItems() {}

	private static SimpleItem createSimpleItem() {
		return new SimpleItem(createBaseProperties());
	}

	private static Item.Properties createBaseProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB).rarity(ModRarities.COMMON);
	}

	private static Item.Properties createProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB);
	}

}
