package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.*;
import com.github.elenterius.biomancy.world.item.weapon.BoomlingItem;
import com.github.elenterius.biomancy.world.item.weapon.LongClawItem;
import com.github.elenterius.biomancy.world.item.weapon.ToothGunItem;
import com.github.elenterius.biomancy.world.item.weapon.WithershotItem;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final RegistryObject<SimpleItem> MOB_FANG = registerSimpleItem("mob_fang");
	public static final RegistryObject<SimpleItem> MOB_CLAW = registerSimpleItem("mob_claw");
	public static final RegistryObject<SimpleItem> MOB_SINEW = registerSimpleItem("mob_sinew");
	public static final RegistryObject<SimpleItem> MOB_MARROW = registerSimpleItem("mob_marrow");
	public static final RegistryObject<SimpleItem> WITHERED_MOB_MARROW = registerSimpleItem("withered_mob_marrow");
	public static final RegistryObject<SimpleItem> MOB_GLAND = registerSimpleItem("mob_gland");
	public static final RegistryObject<SimpleItem> VENOM_GLAND = registerSimpleItem("venom_gland");
	public static final RegistryObject<SimpleItem> VOLATILE_GLAND = registerSimpleItem("volatile_gland");
	//## Special
	public static final RegistryObject<SimpleItem> LIVING_FLESH = registerSimpleItem("living_flesh", ModRarities.VERY_RARE);
	public static final RegistryObject<SimpleItem.ShinySimpleItem> EXALTED_LIVING_FLESH = ITEMS.register("exalted_living_flesh", () -> new SimpleItem.ShinySimpleItem(createBaseProperties().rarity(ModRarities.ULTRA_RARE).tab(null)));

	//# Components
	//## Complex
	public static final RegistryObject<SimpleItem> FLESH_BITS = registerSimpleItem("flesh_bits");
	public static final RegistryObject<SimpleItem> BONE_SCRAPS = registerSimpleItem("bone_scraps");
	public static final RegistryObject<SimpleItem> TOUGH_FIBERS = registerSimpleItem("tough_fibers");
	public static final RegistryObject<SimpleItem> ELASTIC_FIBERS = registerSimpleItem("elastic_fibers");
	public static final RegistryObject<SimpleItem> MINERAL_DUST = registerSimpleItem("mineral_dust");
	public static final RegistryObject<SimpleItem> GEM_DUST = registerSimpleItem("gem_dust");
	//## Basic
	public static final RegistryObject<SimpleItem> NUTRIENTS = registerSimpleItem("nutrients");
	public static final RegistryObject<SimpleItem> ORGANIC_MATTER = registerSimpleItem("organic_matter");
	public static final RegistryObject<SimpleItem> BIO_LUMENS = registerSimpleItem("bio_lumens");
	public static final RegistryObject<SimpleItem> EXOTIC_DUST = registerSimpleItem("exotic_dust");
	public static final RegistryObject<SimpleItem> BIO_MINERALS = registerSimpleItem("bio_minerals");
	public static final RegistryObject<SimpleItem> LITHIC_POWDER = registerSimpleItem("lithic_powder");
	//## Specific
	public static final RegistryObject<SimpleItem> REJUVENATING_MUCUS = registerSimpleItem("rejuvenating_mucus");
	public static final RegistryObject<SimpleItem> HORMONE_SECRETION = registerSimpleItem("hormone_secretion");
	public static final RegistryObject<SimpleItem> VENOM_EXTRACT = registerSimpleItem("venom_extract");
	public static final RegistryObject<SimpleItem> BILE_EXTRACT = registerSimpleItem("bile_extract");
	public static final RegistryObject<SimpleItem> VOLATILE_EXTRACT = registerSimpleItem("volatile_extract");

	//# Serum
	public static final RegistryObject<SimpleItem> GLASS_VIAL = registerSimpleItem("glass_vial");
	public static final RegistryObject<SimpleItem> ORGANIC_COMPOUND = registerSimpleItem("organic_compound");
	public static final RegistryObject<SimpleItem> UNSTABLE_COMPOUND = registerSimpleItem("volatile_compound");
	public static final RegistryObject<SimpleItem> GENETIC_COMPOUND = registerSimpleItem("genetic_compound");

	public static final RegistryObject<DynamicSerumItem> GENERIC_SERUM = ITEMS.register("generic_serum", () -> new DynamicSerumItem(createBaseProperties().stacksTo(8)));
	public static final RegistryObject<SerumItem> REJUVENATION_SERUM = registerSerumItem(ModSerums.REJUVENATION_SERUM);
	public static final RegistryObject<SerumItem> GROWTH_SERUM = registerSerumItem(ModSerums.GROWTH_SERUM);
	public static final RegistryObject<SerumItem> BREEDING_STIMULANT = registerSerumItem(ModSerums.BREEDING_STIMULANT);
	public static final RegistryObject<SerumItem> ABSORPTION_BOOST = registerSerumItem(ModSerums.ABSORPTION_BOOST);
	public static final RegistryObject<SerumItem> CLEANSING_SERUM = registerSerumItem(ModSerums.CLEANSING_SERUM);
	public static final RegistryObject<SerumItem> INSOMNIA_CURE = registerSerumItem(ModSerums.INSOMNIA_CURE);
	public static final RegistryObject<SerumItem> ADRENALINE_SERUM = registerSerumItem(ModSerums.ADRENALINE_SERUM);
	public static final RegistryObject<SerumItem> DECAY_AGENT = registerSerumItem(ModSerums.DECAY_AGENT);

//	public static final RegistryObject<Item> ICHOR_SERUM = ITEMS.register("ichor_serum", () -> new SimpleItem.WithFoilItem(createBaseProperties()));

	//# Misc
	public static final RegistryObject<SimpleItem> OCULUS = ITEMS.register("oculus", () -> new SimpleItem(createBaseProperties().food(ModFoods.OCULUS).rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<EssenceItem> ESSENCE = ITEMS.register("essence", () -> new EssenceItem(createBaseProperties()));
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = ITEMS.register("bio_extractor", () -> new BioExtractorItem(createBaseProperties().durability(200)));
	public static final RegistryObject<InjectorItem> INJECTOR = ITEMS.register("injector", () -> new InjectorItem(createBaseProperties().durability(200)));
//	public static final RegistryObject<ControlStaffItem> CONTROL_STAFF = ITEMS.register("control_staff", () -> new ControlStaffItem(createBaseProperties().stacksTo(1).rarity(ModRarities.ULTRA_RARE)));

	//# Weapons
//	public static final RegistryObject<SwordItem> BONE_SWORD = ITEMS.register("bone_sword", () -> new SwordItem(ModTiers.BONE, 3, -2.4f, createBaseProperties()));
	public static final RegistryObject<ToothGunItem> TOOTH_GUN = ITEMS.register("tooth_gun", () -> new ToothGunItem(createBaseProperties().stacksTo(1).durability(ModTiers.LESSER_BIOFLESH.getUses()).rarity(ModRarities.VERY_RARE).tab(null)));
	public static final RegistryObject<WithershotItem> WITHERSHOT = ITEMS.register("withershot", () -> new WithershotItem(createBaseProperties().stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.VERY_RARE).tab(null)));
	public static final RegistryObject<LongClawItem> LONG_CLAW = ITEMS.register("long_range_claw", () -> new LongClawItem(ModTiers.BIOFLESH, 3, -2.4f, 60, createBaseProperties().rarity(ModRarities.VERY_RARE)));

	//# Creature
	public static final RegistryObject<BoomlingItem> BOOMLING = ITEMS.register("boomling", () -> new BoomlingItem(createBaseProperties().rarity(ModRarities.RARE).stacksTo(1).tab(null)));

	//# Food/Fuel
	public static final RegistryObject<SimpleItem> NUTRIENT_PASTE = registerSimpleItem("nutrient_paste");
	public static final RegistryObject<EffectCureItem> NUTRIENT_BAR = ITEMS.register("nutrient_bar", () -> new EffectCureItem(createBaseProperties().food(ModFoods.NUTRIENT_BAR).rarity(ModRarities.COMMON)));
//	public static final RegistryObject<Item> PROTEIN_BAR = ITEMS.register("protein_bar", () -> new EffectCureItem(createProperties().food(ModFoods.PROTEIN_BAR)));

	//# Block Items

	//## Machine
	public static final RegistryObject<BEWLBlockItem> CREATOR = ITEMS.register(ModBlocks.CREATOR.getId().getPath(), () -> new BEWLBlockItem(ModBlocks.CREATOR.get(), createBaseProperties().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<SimpleBlockItem> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final RegistryObject<BEWLBlockItem> BIO_LAB = ITEMS.register(ModBlocks.BIO_LAB.getId().getPath(), () -> new BEWLBlockItem(ModBlocks.BIO_LAB.get(), createBaseProperties().rarity(ModRarities.RARE)));
	public static final RegistryObject<SimpleBlockItem> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage & Automation
	public static final RegistryObject<SimpleBlockItem> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_CHEST = registerSimpleBlockItem(ModBlocks.FLESHKIN_CHEST, ModRarities.UNCOMMON);

	//## Ownable
//	public static final RegistryObject<SimpleBlockItem> FLESHKIN_DOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_DOOR);
//	public static final RegistryObject<SimpleBlockItem> FLESHKIN_TRAPDOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_TRAPDOOR);
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_PRESSURE_PLATE = registerSimpleBlockItem(ModBlocks.FLESHKIN_PRESSURE_PLATE);

	//## Misc
	public static final RegistryObject<SimpleBlockItem> FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FLESH);
	public static final RegistryObject<SimpleBlockItem> FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FLESH_STAIRS);

	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PACKED_FLESH);
	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_STAIRS);

	public static final RegistryObject<SimpleBlockItem> PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH);
	public static final RegistryObject<SimpleBlockItem> PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> CORRUPTED_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.CORRUPTED_PRIMAL_FLESH);
	public static final RegistryObject<SimpleBlockItem> CORRUPTED_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.CORRUPTED_PRIMAL_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_VEINS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS);

	public static final RegistryObject<SimpleBlockItem> VOICE_BOX = registerSimpleBlockItem(ModBlocks.VOICE_BOX, () -> createBaseProperties().tab(null));
	public static final RegistryObject<SimpleBlockItem> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final RegistryObject<SimpleBlockItem> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);

	//# Spawn Eggs
	public static final RegistryObject<ForgeSpawnEggItem> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> FLESHKIN_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESHKIN, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> BOOMLING_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.BOOMLING, 0x3e3e3e, 0xcfcfcf, () -> createBaseProperties().tab(null));

	//# Tools
//	public static final RegistryObject<EntityStorageBagItem> SMALL_ENTITY_BAG_ITEM = ITEMS.register("small_entity_bag", () -> new EntityStorageBagItem(4f, (byte) 1, createItemProperties().stacksTo(1).rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<EntityStorageBagItem> LARGE_ENTITY_BAG_ITEM = ITEMS.register("large_entity_bag", () -> new EntityStorageBagItem(10f, (byte) 8, createItemProperties().stacksTo(1).rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AccessKeyItem> OCULUS_KEY = ITEMS.register("oculus_key", () -> new AccessKeyItem(createItemProperties()));
	// Adaptive Tools
//	public static final RegistryObject<AdaptivePickaxeItem> FLESHBORN_PICKAXE = ITEMS.register("fleshborn_pickaxe", () -> new AdaptivePickaxeItem(ModItemTier.LESSER_BIOMETAL, 1, -2.8f, createItemProperties().rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AdaptiveShovelItem> FLESHBORN_SHOVEL = ITEMS.register("fleshborn_shovel", () -> new AdaptiveShovelItem(ModItemTier.LESSER_BIOMETAL, 1.5f, -3f, createItemProperties().rarity(ModRarities.UNCOMMON)));
//	public static final RegistryObject<AdaptiveAxeItem> FLESHBORN_AXE = ITEMS.register("fleshborn_axe", () -> new AdaptiveAxeItem(ModItemTier.LESSER_BIOMETAL, 6f, -3f, createItemProperties().rarity(ModRarities.UNCOMMON)));

	//# Spawn Eggs
//	public static final RegistryObject<ModSpawnEggItem> OCULUS_OBSERVER_SPAWN_EGG = ITEMS.register("oculus_observer_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.OCULUS_OBSERVER, 0xe9967a, 0xeff0f1, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> FAILED_SHEEP_SPAWN_EGG = ITEMS.register("failed_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> THICK_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("thick_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.THICK_WOOL_SHEEP, 0xe7e7e7, 0xf1ddcf, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> SILKY_WOOL_SHEEP_SPAWN_EGG = ITEMS.register("silky_wool_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.SILKY_WOOL_SHEEP, 0xe7e7e7, 0xfae6fa, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> FAILED_COW_SPAWN_EGG = ITEMS.register("failed_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.FAILED_COW, 0xe9967a, 0xf6d2c6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> NUTRIENT_COW_SPAWN_EGG = ITEMS.register("nutrient_slurry_cow_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.NUTRIENT_SLURRY_COW, 0x443626, 0xccd65b, createItemProperties()));

//	public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEMS.register("brood_mother_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, createItemProperties()));
//	public static final RegistryObject<ModSpawnEggItem> CHROMA_SHEEP_SPAWN_EGG = ITEMS.register("chroma_sheep_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.CHROMA_SHEEP, 0xe9967a, 0xf6d2c6, createItemProperties()));

	private ModItems() {}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder) {
		return registerSimpleBlockItem(blockHolder, ModRarities.COMMON);
	}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder, Rarity rarity) {
		return registerSimpleBlockItem(blockHolder, () -> createBaseProperties().rarity(rarity));
	}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder, Supplier<Item.Properties> properties) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), properties.get()));
	}

	private static <T extends EntityType<? extends Mob>> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(RegistryObject<T> mobHolder, int primaryColor, int accentColor) {
		return registerSpawnEgg(mobHolder, primaryColor, accentColor, ModItems::createBaseProperties);
	}

	private static <T extends EntityType<? extends Mob>> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(RegistryObject<T> mobHolder, int primaryColor, int accentColor, Supplier<Item.Properties> properties) {
		return ITEMS.register(mobHolder.getId().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(mobHolder, primaryColor, accentColor, properties.get()));
	}

	private static <T extends Serum> RegistryObject<SerumItem> registerSerumItem(RegistryObject<T> registryObject) {
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createBaseProperties().stacksTo(8), registryObject));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name) {
		return registerSimpleItem(name, ModRarities.COMMON);
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Rarity rarity) {
		return registerSimpleItem(name, () -> createBaseProperties().rarity(rarity));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	private static Item.Properties createBaseProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB);
	}

}
