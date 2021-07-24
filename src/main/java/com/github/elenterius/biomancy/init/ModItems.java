package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.tileentity.FleshbornChestTileEntityRender;
import com.github.elenterius.biomancy.item.*;
import com.github.elenterius.biomancy.item.weapon.*;
import com.github.elenterius.biomancy.item.weapon.shootable.BeeHiveGunItem;
import com.github.elenterius.biomancy.item.weapon.shootable.BoomlingHiveGunItem;
import com.github.elenterius.biomancy.item.weapon.shootable.ToothGunItem;
import com.github.elenterius.biomancy.item.weapon.shootable.WithershotItem;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BiomancyMod.MOD_ID);

	// Armor
	public static final RegistryObject<OculiGogglesArmorItem> OCULI_OF_UNVEILING = ITEMS.register("unveiling_oculi", () -> new OculiGogglesArmorItem(ArmorMaterial.IRON, createItemProperties().rarity(Rarity.EPIC)));

	// Weapons
	public static final RegistryObject<WarAxeItem> FLESHBORN_WAR_AXE = ITEMS.register("fleshborn_war_axe", () -> new WarAxeItem(ModItemTier.BIOMETAL, 5F, -3.15F, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<WithershotItem> WITHERSHOT = ITEMS.register("withershot", () -> new WithershotItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.BIOMETAL.getMaxUses()).rarity(Rarity.EPIC)));
	public static final RegistryObject<ToothGunItem> TOOTH_GUN = ITEMS.register("tooth_gun", () -> new ToothGunItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.LESSER_BIOMETAL.getMaxUses()).rarity(Rarity.EPIC)));
	public static final RegistryObject<LeechClawItem> LEECH_CLAW = ITEMS.register("leech_claw", () -> new LeechClawItem(ModItemTier.BIOMETAL, 3, -2.2f, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<LongRangeClawItem> LONG_RANGE_CLAW = ITEMS.register("long_range_claw", () -> new LongRangeClawItem(ModItemTier.BIOMETAL, 3, -2.4f, 60, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<FleshbornGuanDaoItem> FLESHBORN_GUAN_DAO = ITEMS.register("fleshborn_guan_dao", () -> new FleshbornGuanDaoItem(ModItemTier.BIOMETAL, 4, -3F, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BoomlingHiveGunItem> BOOMLING_HIVE_GUN = ITEMS.register("boomling_hive_gun", () -> new BoomlingHiveGunItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.BIOMETAL.getMaxUses()).rarity(Rarity.EPIC)));
	public static final RegistryObject<BeeHiveGunItem> BEE_HIVE_GUN = ITEMS.register("bee_hive_gun", () -> new BeeHiveGunItem(createItemProperties().maxStackSize(1).maxDamage(ModItemTier.BIOMETAL.getMaxUses()).rarity(Rarity.EPIC)));

	// Weapon-Creature
	public static final RegistryObject<ThrowableBoomlingItem> BOOMLING_GRENADE = ITEMS.register("boomling_grenade", () -> new ThrowableBoomlingItem(createItemProperties().maxStackSize(1), 20f));

	// Tools
	public static final RegistryObject<ItemStorageBagItem> SINGLE_ITEM_BAG_ITEM = ITEMS.register("single_item_bag", () -> new ItemStorageBagItem(createItemProperties().maxStackSize(1).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<EntityStorageBagItem> ENTITY_STORAGE_ITEM = ITEMS.register("entity_storage", () -> new EntityStorageBagItem(createItemProperties().maxStackSize(1).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<SwordItem> SHARP_BONE = ITEMS.register("sharp_bone", () -> new SwordItem(ModItemTier.BONE, 3, -2.4F, createItemProperties()));
	public static final RegistryObject<Item> SEWING_KIT_EMPTY = ITEMS.register("sewing_kit_empty", () -> new Item(createItemProperties().maxStackSize(1)));
	public static final RegistryObject<SewingKitItem> SEWING_KIT = ITEMS.register("sewing_kit", () -> new SewingKitItem(createItemProperties().maxStackSize(1).maxDamage(64)));
	public static final RegistryObject<AccessKeyItem> OCULUS_KEY = ITEMS.register("oculus_key", () -> new AccessKeyItem(createItemProperties()));
	public static final RegistryObject<Item> GLASS_VIAL = ITEMS.register("glass_vial", () -> new Item(createItemProperties()));
	public static final RegistryObject<ReagentItem> REAGENT = ITEMS.register("reagent", () -> new ReagentItem(createItemProperties().maxStackSize(8)));
	public static final RegistryObject<InjectionDeviceItem> INJECTION_DEVICE = ITEMS.register("injection_device", () -> new InjectionDeviceItem(createItemProperties().maxStackSize(1)));
	// Adaptive Tools
	public static final RegistryObject<AdaptivePickaxeItem> FLESHBORN_PICKAXE = ITEMS.register("fleshborn_pickaxe", () -> new AdaptivePickaxeItem(ModItemTier.LESSER_BIOMETAL, 1, -2.8f, createItemProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<AdaptiveShovelItem> FLESHBORN_SHOVEL = ITEMS.register("fleshborn_shovel", () -> new AdaptiveShovelItem(ModItemTier.LESSER_BIOMETAL, 1.5f, -3f, createItemProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<AdaptiveAxeItem> FLESHBORN_AXE = ITEMS.register("fleshborn_axe", () -> new AdaptiveAxeItem(ModItemTier.LESSER_BIOMETAL, 6f, -3f, createItemProperties().rarity(Rarity.UNCOMMON)));

	//Fluids
	public static final RegistryObject<Item> NUTRIENT_SLURRY_BUCKET = ITEMS.register("nutrient_slurry_bucket", () -> new BucketItem(ModFluids.NUTRIENT_SLURRY, createItemProperties().containerItem(Items.BUCKET).maxStackSize(1)));

	// Material
	public static final RegistryObject<Item> SKIN_CHUNK = ITEMS.register("skin_chunk", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> BONE_SCRAPS = ITEMS.register("bone_scraps", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> FLESH_LUMP = ITEMS.register("flesh_lump", () -> new Item(createItemProperties().food(ModFoods.FLESH_LUMP)));
	public static final RegistryObject<Item> MENDED_SKIN = ITEMS.register("mended_skin", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> OCULUS = ITEMS.register("oculus", () -> new Item(createItemProperties().food(ModFoods.OCULUS)));
	public static final RegistryObject<Item> MENISCUS_LENS = ITEMS.register("lens", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> NECROTIC_FLESH = ITEMS.register("necrotic_flesh", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> BIOMETAL = ITEMS.register("biometal", () -> new Item(createItemProperties()));

	public static final RegistryObject<Item> ARTIFICIAL_STOMACH = ITEMS.register("artificial_stomach", () -> new Item(createItemProperties().food(ModFoods.ARTIFICIAL_STOMACH)));
	public static final RegistryObject<Item> STOMACH = ITEMS.register("stomach", () -> new Item(createItemProperties().food(ModFoods.STOMACH)));

	public static final RegistryObject<Item> BOLUS = ITEMS.register("bolus", () -> new Item(createItemProperties())); //crushed biomass
	public static final RegistryObject<Item> DIGESTATE = ITEMS.register("digestate", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> NUTRIENT_PASTE = ITEMS.register("nutrient_paste", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> OXIDE_POWDER = ITEMS.register("oxide_powder", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> SILICATE_PASTE = ITEMS.register("silicate_paste", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> KERATIN_FILAMENTS = ITEMS.register("keratin_filaments", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> MILK_GEL = ITEMS.register("milk_gel", () -> new Item(createItemProperties()));

	// Secretions
	public static final RegistryObject<Item> MUTAGENIC_BILE = ITEMS.register("mutagenic_bile", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> REJUVENATING_MUCUS = ITEMS.register("rejuvenating_mucus", () -> new Item(createItemProperties()));
	public static final RegistryObject<DecayingItem> ERODING_BILE = ITEMS.register("eroding_bile", () -> new DecayingItem(6 * 60, 0.5f, createItemProperties()));
	public static final RegistryObject<Item> HORMONE_BILE = ITEMS.register("hormone_bile", () -> new Item(createItemProperties()));

	// Food
//	public static final RegistryObject<Item> VILE_MELON_SLICE = ITEMS.register("vile_melon_slice", () -> new Item(createItemProperties().food(ModFoods.VILE_MELON_SLICE)));
//	public static final RegistryObject<Item> COOKED_VILE_MELON_SLICE = ITEMS.register("cooked_vile_melon_slice", () -> new Item(createItemProperties().food(ModFoods.COOKED_VILE_MELON_SLICE)));
	public static final RegistryObject<Item> NUTRIENT_BAR = ITEMS.register("nutrient_bar", () -> new Item(createItemProperties().food(ModFoods.NUTRIENT_BAR)));

	// Spawn Eggs
	public static final RegistryObject<ModSpawnEggItem> FLESH_BLOB_SPAWN_EGG = ITEMS.register("flesh_blob_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> FLESHKIN_SPAWN_EGG = ITEMS.register("fleshkin_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FLESHKIN, 0xe9967a, 0xf6d2c6, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> BOOMLING_SPAWN_EGG = ITEMS.register("boomling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BOOMLING, 0x3e3e3e, 0xcfcfcf, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> FAILED_SHEEP_SPAWN_EGG = ITEMS.register("failed_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> FAILED_COW_SPAWN_EGG = ITEMS.register("failed_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_COW, 0xe9967a, 0xf6d2c6, createItemProperties()));

	//	public static final RegistryObject<ModSpawnEggItem> CHROMA_SHEEP_SPAWN_EGG = ITEMS.register("chroma_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.CHROMA_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> SILKY_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("silky_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.SILKY_WOOL_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> THICK_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("thick_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.THICK_WOOL_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> NUTRIENT_COW_SPAWN_EGG = ITEMS.register("nutrient_slurry_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.NUTRIENT_SLURRY_COW, 0xfbf0cf, 0xccd65b, createItemProperties()));

//	public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEMS.register("brood_mother_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> BEETLING_SPAWN_EGG = ITEMS.register("beetling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BEETLING, 0x764da2, 0xff40ff, createItemProperties()));

	/* **** Block Items ********************************************* */

	//crops
//	public static final RegistryObject<Item> VILE_MELON_SEEDS = ITEMS.register("vile_melon_seeds", () -> new BlockNamedItem(ModBlocks.VILE_MELON_CROP.get(), createItemProperties()));
//	public static final RegistryObject<Item> VILE_MELON_BLOCK = ITEMS.register("vile_melon_block", () -> new BlockItem(ModBlocks.VILE_MELON_BLOCK.get(), createItemProperties()));

	//decoration blocks
	public static final RegistryObject<BlockItem> FLESH_TENTACLE = ITEMS.register("flesh_tentacle", () -> new BlockItem(ModBlocks.FLESH_TENTACLE.get(), createItemProperties()));

	//material blocks
	public static final RegistryObject<BlockItem> FLESH_BLOCK = ITEMS.register("flesh_block", () -> new BlockItem(ModBlocks.FLESH_BLOCK.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> FLESH_BLOCK_SLAB = ITEMS.register("flesh_block_slab", () -> new BlockItem(ModBlocks.FLESH_BLOCK_SLAB.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> FLESH_BLOCK_STAIRS = ITEMS.register("flesh_block_stairs", () -> new BlockItem(ModBlocks.FLESH_BLOCK_STAIRS.get(), createItemProperties()));
	//	public static final RegistryObject<BlockItem> MUTATED_FLESH_BLOCK = ITEMS.register("mutated_flesh_block", () -> new BlockItem(ModBlocks.MUTATED_FLESH_BLOCK.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> NECROTIC_FLESH_BLOCK = ITEMS.register("necrotic_flesh_block", () -> new BlockItem(ModBlocks.NECROTIC_FLESH_BLOCK.get(), createItemProperties()));

	//bio-construct blocks
	public static final RegistryObject<BlockItem> FLESHBORN_DOOR = ITEMS.register("fleshborn_door", () -> new BlockItem(ModBlocks.FLESHBORN_DOOR.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> FLESHBORN_TRAPDOOR = ITEMS.register("fleshborn_trapdoor", () -> new BlockItem(ModBlocks.FLESHBORN_TRAPDOOR.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> FLESHBORN_PRESSURE_PLATE = ITEMS.register("fleshborn_pressure_plate", () -> new BlockItem(ModBlocks.FLESHBORN_PRESSURE_PLATE.get(), createItemProperties()));

	//semi-container blocks

	//container blocks
	public static final RegistryObject<BlockItem> GULGE = ITEMS.register("gulge", () -> new BlockItem(ModBlocks.GULGE.get(), createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> FLESH_CHEST = ITEMS.register("fleshborn_chest", () -> new BlockItem(ModBlocks.FLESHBORN_CHEST.get(), createItemProperties().rarity(Rarity.UNCOMMON).setISTER(() -> FleshbornChestTileEntityRender::new)));
	public static final RegistryObject<BlockItem> CHEWER = ITEMS.register("chewer", () -> new BlockItem(ModBlocks.CHEWER.get(), createItemProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> DIGESTER = ITEMS.register("digester", () -> new BlockItem(ModBlocks.DIGESTER.get(), createItemProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> SOLIDIFIER = ITEMS.register("solidifier", () -> new BlockItem(ModBlocks.SOLIDIFIER.get(), createItemProperties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> DECOMPOSER = ITEMS.register("decomposer", () -> new BlockItem(ModBlocks.DECOMPOSER.get(), createItemProperties().rarity(Rarity.UNCOMMON)));

	private ModItems() {}

	private static Item.Properties createItemProperties() {
		return new Item.Properties().group(BiomancyMod.ITEM_GROUP);
	}
}
