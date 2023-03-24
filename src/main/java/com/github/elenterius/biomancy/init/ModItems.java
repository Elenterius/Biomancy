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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final RegistryObject<MobLootItem> MOB_FANG = registerMobLootItem("mob_fang", ModTags.EntityTypes.SHARP_FANG);
	public static final RegistryObject<MobLootItem> MOB_CLAW = registerMobLootItem("mob_claw", ModTags.EntityTypes.SHARP_CLAW);
	public static final RegistryObject<MobLootItem> MOB_SINEW = registerMobLootItem("mob_sinew", ModTags.EntityTypes.SINEW, ModRarities.UNCOMMON);
	public static final RegistryObject<BoneMarrowItem> MOB_MARROW = registerItem("mob_marrow", props -> new BoneMarrowItem(ModTags.EntityTypes.BONE_MARROW, props.food(ModFoods.MARROW_FLUID).rarity(ModRarities.RARE)));
	public static final RegistryObject<BoneMarrowItem> WITHERED_MOB_MARROW = registerItem("withered_mob_marrow", props -> new BoneMarrowItem(ModTags.EntityTypes.WITHERED_BONE_MARROW, props.food(ModFoods.CORROSIVE_FLUID).rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<MobLootItem> GENERIC_MOB_GLAND = registerItem("mob_gland", props -> new MobLootItem(ModTags.EntityTypes.BILE_GLAND, props.food(ModFoods.POOR_FLESH).rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<MobLootItem> TOXIN_GLAND = registerItem("toxin_gland", props -> new MobLootItem(ModTags.EntityTypes.TOXIN_GLAND, props.food(ModFoods.TOXIN_GLAND).rarity(ModRarities.RARE)));
	public static final RegistryObject<VolatileGlandItem> VOLATILE_GLAND = registerItem("volatile_gland", props -> new VolatileGlandItem(ModTags.EntityTypes.VOLATILE_GLAND, props.food(ModFoods.VOLATILE_GLAND).rarity(ModRarities.RARE)));

	//## Special
	public static final RegistryObject<SimpleItem> LIVING_FLESH = registerSimpleItem("living_flesh", ModRarities.VERY_RARE);
	public static final RegistryObject<SimpleItem> PRIMORDIAL_LIVING_FLESH = registerItem("primordial_living_flesh", props -> new SimpleItem(props.rarity(ModRarities.ULTRA_RARE)));
	public static final RegistryObject<SimpleItem> PRIMORDIAL_LIVING_OCULUS = registerItem("primordial_living_oculus", props -> new SimpleItem(props.rarity(ModRarities.ULTRA_RARE)));

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
	public static final RegistryObject<UnstableCompoundItem> UNSTABLE_COMPOUND = registerItem("unstable_compound", UnstableCompoundItem::new);
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
	public static final RegistryObject<FertilizerItem> FERTILIZER = registerItem("fertilizer", props -> new FertilizerItem(props.rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<EssenceItem> ESSENCE = registerItem("essence", props -> new EssenceItem(props.tab(null)));
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = registerItem("bio_extractor", props -> new BioExtractorItem(props.durability(200).tab(null)));
	public static final RegistryObject<InjectorItem> INJECTOR = registerItem("injector", props -> new InjectorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final RegistryObject<GuideBookItem> GUIDE_BOOK = registerItem("guide_book", props -> new GuideBookItem(props.stacksTo(1).rarity(ModRarities.RARE)));
	//	public static final RegistryObject<ControlStaffItem> CONTROL_STAFF = registerItem("control_staff", props -> new ControlStaffItem(props.stacksTo(1).rarity(ModRarities.ULTRA_RARE)));

	public static final RegistryObject<MaykerBannerPatternItem> MASCOT_BANNER_PATTERNS = registerItem("mascot_patterns", props -> new MaykerBannerPatternItem(ModBannerPatterns.TAG_MASCOT, props));

	//# Weapons
	public static final RegistryObject<SimpleSwordItem> BONE_CLEAVER = registerItem("bone_cleaver", props -> new SimpleSwordItem(ModTiers.BONE, 3, -2.4f, props));
	public static final RegistryObject<LivingLongClawsItem> LONG_CLAWS = registerItem("long_claws", props -> new LivingLongClawsItem(ModTiers.BIOFLESH, -2, -2.4f, 0.5f, 1000, props.setNoRepair().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<DevArmCannonItem> DEV_ARM_CANNON = registerItem("dev_arm_cannon", props -> new DevArmCannonItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE).tab(null)));
	public static final RegistryObject<BileSpitterItem> BILE_SPITTER = registerItem("bile_spitter", props -> new BileSpitterItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE).tab(null)));

	//# Creature
	//	public static final RegistryObject<BoomlingItem> BOOMLING = ITEMS.register("boomling", () -> new BoomlingItem(createBaseProperties().rarity(ModRarities.RARE).stacksTo(1).tab(null)));

	//# Food/Fuel
	public static final RegistryObject<EffectCureItem> NUTRIENT_PASTE = registerItem("nutrient_paste", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final RegistryObject<EffectCureItem> NUTRIENT_BAR = registerItem("nutrient_bar", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_BAR)));

	//# Block Items

	//## Machine
	public static final RegistryObject<BEWLBlockItem> PRIMORDIAL_CRADLE = registerBlockItem(ModBlocks.PRIMORDIAL_CRADLE, block -> new BEWLBlockItem(block, createProperties().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<SimpleBlockItem> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final RegistryObject<BEWLBlockItem> BIO_LAB = registerBlockItem(ModBlocks.BIO_LAB, block -> new BEWLBlockItem(block, createProperties().rarity(ModRarities.RARE)));
	public static final RegistryObject<SimpleBlockItem> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage & Automation
	public static final RegistryObject<SimpleBlockItem> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final RegistryObject<BEWLBlockItem> MAW_HOPPER = registerBlockItem(ModBlocks.MAW_HOPPER, block -> new BEWLBlockItem(block, createProperties().rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<SimpleBlockItem> FLESHKIN_CHEST = registerBlockItem(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockItem::new, ModRarities.UNCOMMON);
	public static final RegistryObject<StorageSacBlockItem> STORAGE_SAC = registerBlockItem(ModBlocks.STORAGE_SAC, StorageSacBlockItem::new);

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
	public static final RegistryObject<SimpleBlockItem> PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_STAIRS);
	public static final RegistryObject<SimpleBlockItem> CORRUPTED_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.CORRUPTED_PRIMAL_FLESH);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_STAIRS);
	public static final RegistryObject<SimpleBlockItem> MALIGNANT_FLESH_VEINS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS);

	//## Misc
	public static final RegistryObject<SimpleBlockItem> VOICE_BOX = registerSimpleBlockItem(ModBlocks.VOICE_BOX, () -> createProperties().tab(null));
	public static final RegistryObject<SimpleBlockItem> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final RegistryObject<SimpleBlockItem> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> FULL_FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FULL_FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> BONE_SPIKE = registerSimpleBlockItem(ModBlocks.BONE_SPIKE);
	public static final RegistryObject<SimpleBlockItem> BIO_LANTERN = registerSimpleBlockItem(ModBlocks.BIO_LANTERN);
	public static final RegistryObject<FleshChainBlockItem> TENDON_CHAIN = registerBlockItem(ModBlocks.TENDON_CHAIN, FleshChainBlockItem::new);

	//# Spawn Eggs
	public static final RegistryObject<ForgeSpawnEggItem> HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.HUNGRY_FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> MALIGNANT_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.MALIGNANT_FLESH_BLOB, 0xb7556e, 0x91354f);

	private ModItems() {}

	private static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> factory) {
		return ITEMS.register(name, () -> factory.apply(createProperties()));
	}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), createProperties()));
	}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder, Rarity rarity) {
		return registerSimpleBlockItem(blockHolder, () -> createProperties().rarity(rarity));
	}

	private static <T extends Block> RegistryObject<SimpleBlockItem> registerSimpleBlockItem(RegistryObject<T> blockHolder, Supplier<Item.Properties> properties) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), properties.get()));
	}

	private static <T extends Block, I extends BlockItem> RegistryObject<I> registerBlockItem(RegistryObject<T> blockHolder, Function<T, I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.apply(blockHolder.get()));
	}

	private static <T extends Block, I extends BlockItem> RegistryObject<I> registerBlockItem(RegistryObject<T> blockHolder, IBlockItemFactory<I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties()));
	}

	private static <T extends Block, I extends BlockItem> RegistryObject<I> registerBlockItem(RegistryObject<T> blockHolder, IBlockItemFactory<I> factory, Rarity rarity) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties().rarity(rarity)));
	}

	private static <T extends EntityType<? extends Mob>> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(RegistryObject<T> mobHolder, int primaryColor, int accentColor) {
		return ITEMS.register(mobHolder.getId().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(mobHolder, primaryColor, accentColor, createProperties()));
	}

	private static <T extends Serum> RegistryObject<SerumItem> registerSerumItem(RegistryObject<T> registryObject) {
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createProperties().stacksTo(8).rarity(ModRarities.UNCOMMON), registryObject));
	}

	private static RegistryObject<SimpleItem> registerSimpleVialItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties().craftRemainder(GLASS_VIAL.get())));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
	}

	private static RegistryObject<MobLootItem> registerMobLootItem(String name, TagKey<EntityType<?>> lootSource) {
		return ITEMS.register(name, () -> new MobLootItem(lootSource, createProperties()));
	}

	private static RegistryObject<MobLootItem> registerMobLootItem(String name, TagKey<EntityType<?>> lootSource, Rarity rarity) {
		return ITEMS.register(name, () -> new MobLootItem(lootSource, createProperties().rarity(rarity)));
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Rarity rarity) {
		return registerSimpleItem(name, () -> createProperties().rarity(rarity));
	}

	private static Item.Properties createProperties() {
		return new Item.Properties().tab(BiomancyMod.CREATIVE_TAB).rarity(ModRarities.COMMON);
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	interface IBlockItemFactory<I extends BlockItem> {
		I create(Block block, Item.Properties properties);
	}

}
