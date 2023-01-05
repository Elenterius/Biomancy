package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.*;
import com.github.elenterius.biomancy.world.item.weapon.BileSpitterItem;
import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import com.github.elenterius.biomancy.world.item.weapon.LivingLongClawsItem;
import com.github.elenterius.biomancy.world.item.weapon.SimpleSwordItem;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.tags.TagKey;
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
	public static final RegistryObject<MobLootItem> MOB_FANG = registerMobLootItem("mob_fang", ModTags.EntityTypes.SHARP_FANG);
	public static final RegistryObject<MobLootItem> MOB_CLAW = registerMobLootItem("mob_claw", ModTags.EntityTypes.SHARP_CLAW);
	public static final RegistryObject<MobLootItem> MOB_SINEW = registerMobLootItem("mob_sinew", ModTags.EntityTypes.SINEW, ModRarities.UNCOMMON);
	public static final RegistryObject<BoneMarrowItem> MOB_MARROW = ITEMS.register("mob_marrow", () -> new BoneMarrowItem(ModTags.EntityTypes.BONE_MARROW, createBaseProperties().food(ModFoods.MARROW_FLUID).rarity(ModRarities.RARE)));
	public static final RegistryObject<BoneMarrowItem> WITHERED_MOB_MARROW = ITEMS.register("withered_mob_marrow", () -> new BoneMarrowItem(ModTags.EntityTypes.WITHERED_BONE_MARROW, createBaseProperties().food(ModFoods.CORROSIVE_FLUID).rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<MobLootItem> GENERIC_MOB_GLAND = registerMobLootItem("mob_gland", ModTags.EntityTypes.BILE_GLAND, () -> createBaseProperties().food(ModFoods.POOR_FLESH).rarity(ModRarities.UNCOMMON));
	public static final RegistryObject<MobLootItem> TOXIN_GLAND = registerMobLootItem("toxin_gland", ModTags.EntityTypes.TOXIN_GLAND, () -> createBaseProperties().food(ModFoods.TOXIN_GLAND).rarity(ModRarities.RARE));
	public static final RegistryObject<VolatileGlandItem> VOLATILE_GLAND = ITEMS.register("volatile_gland", () -> new VolatileGlandItem(ModTags.EntityTypes.VOLATILE_GLAND, createBaseProperties().food(ModFoods.VOLATILE_GLAND).rarity(ModRarities.RARE)));
	//## Special
	public static final RegistryObject<SimpleItem> LIVING_FLESH = registerSimpleItem("living_flesh", ModRarities.VERY_RARE);
	public static final RegistryObject<SimpleItem.ShinySimpleItem> EXALTED_LIVING_FLESH = ITEMS.register("exalted_living_flesh", () -> new SimpleItem.ShinySimpleItem(createBaseProperties().rarity(ModRarities.ULTRA_RARE).tab(null)));

	//# Components
	//## Complex
	public static final RegistryObject<SimpleItem> FLESH_BITS = registerSimpleItem("flesh_bits");
	public static final RegistryObject<SimpleItem> BONE_FRAGMENTS = registerSimpleItem("bone_fragments");
	public static final RegistryObject<SimpleItem> TOUGH_FIBERS = registerSimpleItem("tough_fibers");
	public static final RegistryObject<SimpleItem> ELASTIC_FIBERS = registerSimpleItem("elastic_fibers");
	public static final RegistryObject<SimpleItem> MINERAL_FRAGMENT = registerSimpleItem("mineral_fragment");
	public static final RegistryObject<SimpleItem> GEM_FRAGMENTS = registerSimpleItem("gem_fragments");
	//## Basic
	public static final RegistryObject<SimpleItem> NUTRIENTS = registerSimpleItem("nutrients");
	public static final RegistryObject<SimpleItem> ORGANIC_MATTER = registerSimpleItem("organic_matter");
	public static final RegistryObject<SimpleItem> BIO_LUMENS = registerSimpleItem("bio_lumens");
	public static final RegistryObject<SimpleItem> EXOTIC_DUST = registerSimpleItem("exotic_dust");
	public static final RegistryObject<SimpleItem> BIO_MINERALS = registerSimpleItem("bio_minerals");
	public static final RegistryObject<SimpleItem> STONE_POWDER = registerSimpleItem("stone_powder");
	//## Specific
	public static final RegistryObject<SimpleItem> REGENERATIVE_FLUID = registerSimpleItem("regenerative_fluid");
	public static final RegistryObject<SimpleItem> WITHERING_OOZE = registerSimpleItem("withering_ooze");
	public static final RegistryObject<SimpleItem> HORMONE_SECRETION = registerSimpleItem("hormone_secretion");
	public static final RegistryObject<SimpleItem> TOXIN_EXTRACT = registerSimpleItem("toxin_extract");
	public static final RegistryObject<SimpleItem> BILE = registerSimpleItem("bile");
	public static final RegistryObject<SimpleItem> VOLATILE_FLUID = registerSimpleItem("volatile_fluid");

	//# Serum
	public static final RegistryObject<SimpleItem> GLASS_VIAL = registerSimpleItem("glass_vial");
	public static final RegistryObject<SimpleItem> ORGANIC_COMPOUND = registerSimpleVialItem("organic_compound");
	public static final RegistryObject<SimpleItem> UNSTABLE_COMPOUND = registerSimpleVialItem("unstable_compound");
	public static final RegistryObject<SimpleItem> GENETIC_COMPOUND = registerSimpleVialItem("genetic_compound");
	public static final RegistryObject<SimpleItem> EXOTIC_COMPOUND = registerSimpleVialItem("exotic_compound");
	public static final RegistryObject<SimpleItem> HEALING_ADDITIVE = registerSimpleVialItem("healing_additive");
	public static final RegistryObject<SimpleItem> CORROSIVE_ADDITIVE = registerSimpleVialItem("corrosive_additive");
	public static final RegistryObject<SerumItem> REJUVENATION_SERUM = registerSerumItem(ModSerums.REJUVENATION_SERUM);
	public static final RegistryObject<SerumItem> AGEING_SERUM = registerSerumItem(ModSerums.AGEING_SERUM);
	public static final RegistryObject<SerumItem> ENLARGEMENT_SERUM = registerSerumItem(ModSerums.ENLARGEMENT_SERUM);
	public static final RegistryObject<SerumItem> SHRINKING_SERUM = registerSerumItem(ModSerums.SHRINKING_SERUM);
	public static final RegistryObject<SerumItem> BREEDING_STIMULANT = registerSerumItem(ModSerums.BREEDING_STIMULANT);
	public static final RegistryObject<SerumItem> ABSORPTION_BOOST = registerSerumItem(ModSerums.ABSORPTION_BOOST);
	public static final RegistryObject<SerumItem> CLEANSING_SERUM = registerSerumItem(ModSerums.CLEANSING_SERUM);
	public static final RegistryObject<SerumItem> INSOMNIA_CURE = registerSerumItem(ModSerums.INSOMNIA_CURE);
	//	public static final RegistryObject<SerumItem> ADRENALINE_SERUM = registerSerumItem(ModSerums.ADRENALINE_SERUM);
	//	public static final RegistryObject<SerumItem> DECAY_AGENT = registerSerumItem(ModSerums.DECAY_AGENT);

	//# Misc
	public static final RegistryObject<SimpleItem> CREATOR_MIX = registerSimpleItem("creator_mix");
	public static final RegistryObject<FertilizerItem> FERTILIZER = ITEMS.register("fertilizer", () -> new FertilizerItem(createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<EssenceItem> ESSENCE = ITEMS.register("essence", () -> new EssenceItem(createBaseProperties().tab(null)));
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = ITEMS.register("bio_extractor", () -> new BioExtractorItem(createBaseProperties().durability(200).tab(null)));
	public static final RegistryObject<InjectorItem> INJECTOR = ITEMS.register("injector", () -> new InjectorItem(createBaseProperties().durability(200).rarity(ModRarities.RARE)));
	//	public static final RegistryObject<ControlStaffItem> CONTROL_STAFF = ITEMS.register("control_staff", () -> new ControlStaffItem(createBaseProperties().stacksTo(1).rarity(ModRarities.ULTRA_RARE)));

	public static final RegistryObject<MaykerBannerPatternItem> MASCOT_BANNER_PATTERNS = ITEMS.register("mascot_patterns", () -> new MaykerBannerPatternItem(ModBannerPatterns.TAG_MASCOT, createBaseProperties()));

	//# Weapons
	public static final RegistryObject<SimpleSwordItem> BONE_CLEAVER = ITEMS.register("bone_cleaver", () -> new SimpleSwordItem(ModTiers.BONE, 3, -2.4f, createBaseProperties()));
	public static final RegistryObject<LivingLongClawsItem> LONG_CLAWS = ITEMS.register("long_claws", () -> new LivingLongClawsItem(ModTiers.BIOFLESH, -2, -2.4f, 0.5f, 1000, createLivingToolProperties()));
	public static final RegistryObject<DevArmCannonItem> DEV_ARM_CANNON = ITEMS.register("dev_arm_cannon", () -> new DevArmCannonItem(createBaseProperties().stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE).tab(null)));
	public static final RegistryObject<BileSpitterItem> BILE_SPITTER = ITEMS.register("bile_spitter", () -> new BileSpitterItem(createBaseProperties().stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE).tab(null)));

	//# Creature
	//	public static final RegistryObject<BoomlingItem> BOOMLING = ITEMS.register("boomling", () -> new BoomlingItem(createBaseProperties().rarity(ModRarities.RARE).stacksTo(1).tab(null)));

	//# Food/Fuel
	public static final RegistryObject<SimpleItem> NUTRIENT_PASTE = registerSimpleItem("nutrient_paste");
	public static final RegistryObject<EffectCureItem> NUTRIENT_BAR = ITEMS.register("nutrient_bar", () -> new EffectCureItem(createBaseProperties().food(ModFoods.NUTRIENT_BAR)));

	//# Block Items

	//## Machine
	public static final RegistryObject<BEWLBlockItem> PRIMORDIAL_CRADLE = ITEMS.register(ModBlocks.PRIMORDIAL_CRADLE.getId().getPath(), () -> new BEWLBlockItem(ModBlocks.PRIMORDIAL_CRADLE.get(), createBaseProperties().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<SimpleBlockItem> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final RegistryObject<BEWLBlockItem> BIO_LAB = ITEMS.register(ModBlocks.BIO_LAB.getId().getPath(), () -> new BEWLBlockItem(ModBlocks.BIO_LAB.get(), createBaseProperties().rarity(ModRarities.RARE)));
	public static final RegistryObject<SimpleBlockItem> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage & Automation
	public static final RegistryObject<SimpleBlockItem> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_CHEST = ITEMS.register(ModBlocks.FLESHKIN_CHEST.getId().getPath(), () -> new FleshkinChestBlockItem(ModBlocks.FLESHKIN_CHEST.get(), createBaseProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<StorageSacBlockItem> STORAGE_SAC = ITEMS.register(ModBlocks.STORAGE_SAC.getId().getPath(), () -> new StorageSacBlockItem(ModBlocks.STORAGE_SAC.get(), createBaseProperties()));

	//## Ownable
	//	public static final RegistryObject<SimpleBlockItem> FLESHKIN_DOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_DOOR);
	//	public static final RegistryObject<SimpleBlockItem> FLESHKIN_TRAPDOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_TRAPDOOR);
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_PRESSURE_PLATE = registerSimpleBlockItem(ModBlocks.FLESHKIN_PRESSURE_PLATE);

	//## Building Blocks
	public static final RegistryObject<SimpleBlockItem> FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FLESH);
	public static final RegistryObject<SimpleBlockItem> FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FLESH_STAIRS);
	public static final RegistryObject<SimpleBlockItem> FLESH_WALL = registerSimpleBlockItem(ModBlocks.FLESH_WALL);

	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PACKED_FLESH);
	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_STAIRS);
	public static final RegistryObject<SimpleBlockItem> PACKED_FLESH_WALL = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_WALL);

	public static final RegistryObject<SimpleBlockItem> PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH);
	public static final RegistryObject<SimpleBlockItem> PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> CORRUPTED_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.CORRUPTED_PRIMAL_FLESH);
	public static final RegistryObject<SimpleBlockItem> CORRUPTED_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.CORRUPTED_PRIMAL_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_VEINS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS);

	//## Misc
	public static final RegistryObject<SimpleBlockItem> VOICE_BOX = registerSimpleBlockItem(ModBlocks.VOICE_BOX, () -> createBaseProperties().tab(null));
	public static final RegistryObject<SimpleBlockItem> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final RegistryObject<SimpleBlockItem> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> BONE_SPIKE = registerSimpleBlockItem(ModBlocks.BONE_SPIKE);

	//# Spawn Eggs
	public static final RegistryObject<ForgeSpawnEggItem> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);

	private ModItems() {}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), createBaseProperties()));
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
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createBaseProperties().stacksTo(8).rarity(ModRarities.UNCOMMON), registryObject));
	}

	private static RegistryObject<SimpleItem> registerSimpleVialItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createBaseProperties().craftRemainder(GLASS_VIAL.get())));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createBaseProperties()));
	}

	private static RegistryObject<MobLootItem> registerMobLootItem(String name, TagKey<EntityType<?>> lootSource) {
		return ITEMS.register(name, () -> new MobLootItem(lootSource, createBaseProperties()));
	}

	private static RegistryObject<MobLootItem> registerMobLootItem(String name, TagKey<EntityType<?>> lootSource, Rarity rarity) {
		return registerMobLootItem(name, lootSource, () -> createBaseProperties().rarity(rarity));
	}

	private static RegistryObject<MobLootItem> registerMobLootItem(String name, TagKey<EntityType<?>> lootSource, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new MobLootItem(lootSource, properties.get()));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Rarity rarity) {
		return registerSimpleItem(name, () -> createBaseProperties().rarity(rarity));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	private static Item.Properties createBaseProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB).rarity(ModRarities.COMMON);
	}

	private static Item.Properties createLivingToolProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB).setNoRepair().rarity(ModRarities.VERY_RARE);
	}

}
