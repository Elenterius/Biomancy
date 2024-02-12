package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.item.*;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.weapon.*;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
	public static final RegistryObject<MobLootItem> MOB_FANG = registerMobLootItem("mob_fang", ModEntityTags.SHARP_FANG);
	public static final RegistryObject<MobLootItem> MOB_CLAW = registerMobLootItem("mob_claw", ModEntityTags.SHARP_CLAW);
	public static final RegistryObject<MobLootItem> MOB_SINEW = registerMobLootItem("mob_sinew", ModEntityTags.SINEW, ModRarities.UNCOMMON);
	public static final RegistryObject<BoneMarrowItem> MOB_MARROW = registerItem("mob_marrow", props -> new BoneMarrowItem(ModEntityTags.BONE_MARROW, props.food(ModFoods.MARROW_FLUID).rarity(ModRarities.RARE)));
	public static final RegistryObject<BoneMarrowItem> WITHERED_MOB_MARROW = registerItem("withered_mob_marrow", props -> new BoneMarrowItem(ModEntityTags.WITHERED_BONE_MARROW, props.food(ModFoods.CORROSIVE_FLUID).rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<MobLootItem> GENERIC_MOB_GLAND = registerItem("mob_gland", props -> new MobLootItem(ModEntityTags.BILE_GLAND, props.food(ModFoods.POOR_FLESH).rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<MobLootItem> TOXIN_GLAND = registerItem("toxin_gland", props -> new MobLootItem(ModEntityTags.TOXIN_GLAND, props.food(ModFoods.TOXIN_GLAND).rarity(ModRarities.RARE)));
	public static final RegistryObject<VolatileGlandItem> VOLATILE_GLAND = registerItem("volatile_gland", props -> new VolatileGlandItem(ModEntityTags.VOLATILE_GLAND, props.food(ModFoods.VOLATILE_GLAND).rarity(ModRarities.RARE)));

	//## Special
	public static final RegistryObject<SimpleItem> LIVING_FLESH = registerSimpleItem("living_flesh", ModRarities.VERY_RARE);
	public static final RegistryObject<SimpleItem> PRIMORDIAL_CORE = registerSimpleItem("primordial_core", ModRarities.VERY_RARE);

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
	public static final RegistryObject<SimpleItem> VIAL = registerSimpleItem("vial");
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

	//# Buckets
	public static final RegistryObject<BucketItem> ACID_BUCKET = registerItem("acid_bucket", properties -> new BucketItem(ModFluids.ACID, properties.craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON)));

	//# Misc
	public static final RegistryObject<GiftSacItem> GIFT_SAC = registerItem("gift_sac", props -> new GiftSacItem(props.stacksTo(1).rarity(ModRarities.ULTRA_RARE)));
	public static final RegistryObject<SimpleItem> CREATOR_MIX = registerSimpleItem("creator_mix");
	public static final RegistryObject<FertilizerItem> FERTILIZER = registerItem("fertilizer", props -> new FertilizerItem(props.rarity(ModRarities.UNCOMMON)));
	public static final RegistryObject<EssenceItem> ESSENCE = registerItem("essence", EssenceItem::new);
	public static final RegistryObject<BioExtractorItem> BIO_EXTRACTOR = registerItem("bio_extractor", props -> new BioExtractorItem(props.durability(200)));
	public static final RegistryObject<InjectorItem> INJECTOR = registerItem("injector", props -> new InjectorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final RegistryObject<GuideBookItem> GUIDE_BOOK = registerItem("guide_book", props -> new GuideBookItem(props.stacksTo(1).rarity(ModRarities.RARE)));

	public static final RegistryObject<MaykerBannerPatternItem> MASCOT_BANNER_PATTERNS = registerItem("mascot_patterns", props -> new MaykerBannerPatternItem(ModBannerPatterns.TAG_MASCOT, props));

	//# Weapons
	public static final RegistryObject<DespoilingSwordItem> DESPOIL_SICKLE = registerItem("despoil_sickle", props -> SwordSmithy.forge(DespoilingSwordItem::new, ModTiers.PRIMAL_FLESH, 12, 1, props.rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<ToxicusItem> TOXICUS = registerItem("toxicus", props -> SwordSmithy.forge(ToxicusItem::new, ModTiers.LESSER_BIO_FLESH, 14, 0.7f, props));
	public static final RegistryObject<RavenousClawsItem> RAVENOUS_CLAWS = registerItem("ravenous_claws", props -> new RavenousClawsItem(ModTiers.BIOFLESH, 3, 4, 250, props.setNoRepair().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<DevArmCannonItem> DEV_ARM_CANNON = registerItem("dev_arm_cannon", props -> new DevArmCannonItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE)));
	public static final RegistryObject<BileSpitterItem> BILE_SPITTER = registerItem("bile_spitter", props -> new BileSpitterItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE)));

	//# Food/Fuel
	public static final RegistryObject<EffectCureItem> NUTRIENT_PASTE = registerItem("nutrient_paste", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final RegistryObject<EffectCureItem> NUTRIENT_BAR = registerItem("nutrient_bar", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_BAR)));
	public static final RegistryObject<BloomberryItem> BLOOMBERRY = registerItem("bloomberry", props -> new BloomberryItem(props.food(ModFoods.NUTRIENT_PASTE)));

	//# Block Items

	//## Machine
	public static final RegistryObject<BEWLBlockItem> PRIMORDIAL_CRADLE = registerBlockItem(ModBlocks.PRIMORDIAL_CRADLE, block -> new BEWLBlockItem(block, createProperties().rarity(ModRarities.VERY_RARE)));
	public static final RegistryObject<SimpleBlockItem> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> BIO_LAB = registerSimpleBlockItem(ModBlocks.BIO_LAB, ModRarities.RARE);
	public static final RegistryObject<SimpleBlockItem> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage & Automation
	public static final RegistryObject<SimpleBlockItem> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final RegistryObject<SimpleBlockItem> MAW_HOPPER = registerSimpleBlockItem(ModBlocks.MAW_HOPPER, ModRarities.UNCOMMON);
	public static final RegistryObject<FleshkinChestBlockItem> FLESHKIN_CHEST = registerBlockItem(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockItem::new, ModRarities.UNCOMMON);
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
	public static final RegistryObject<ObfuscatedTooltipBlockItem> FLESH_PILLAR = registerBlockItem(ModBlocks.FLESH_PILLAR, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> FIBROUS_FLESH_BLOCK = registerBlockItem(ModBlocks.FIBROUS_FLESH, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> CHISELED_FLESH_BLOCK = registerBlockItem(ModBlocks.CHISELED_FLESH, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> ORNATE_FLESH_BLOCK = registerBlockItem(ModBlocks.ORNATE_FLESH, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> TUBULAR_FLESH_BLOCK = registerBlockItem(ModBlocks.TUBULAR_FLESH_BLOCK, ObfuscatedTooltipBlockItem::new);

	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_FLESH_BLOCK = registerBlockItem(ModBlocks.PRIMAL_FLESH, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_FLESH_SLAB = registerBlockItem(ModBlocks.PRIMAL_FLESH_SLAB, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_FLESH_STAIRS = registerBlockItem(ModBlocks.PRIMAL_FLESH_STAIRS, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_FLESH_WALL = registerBlockItem(ModBlocks.PRIMAL_FLESH_WALL, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> MALIGNANT_FLESH_BLOCK = registerBlockItem(ModBlocks.MALIGNANT_FLESH, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> MALIGNANT_FLESH_SLAB = registerBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> MALIGNANT_FLESH_STAIRS = registerBlockItem(ModBlocks.MALIGNANT_FLESH_STAIRS, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> MALIGNANT_FLESH_WALL = registerBlockItem(ModBlocks.MALIGNANT_FLESH_WALL, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> MALIGNANT_FLESH_VEINS = registerBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_BLOOM = registerBlockItem(ModBlocks.PRIMAL_BLOOM, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMAL_ORIFICE = registerBlockItem(ModBlocks.PRIMAL_ORIFICE, ObfuscatedTooltipBlockItem::new);

	//## Utility
	//	public static final RegistryObject<SimpleBlockItem> VOICE_BOX = registerSimpleBlockItem(ModBlocks.VOICE_BOX, () -> createProperties());
	public static final RegistryObject<SimpleBlockItem> IMPERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE);
	public static final RegistryObject<SimpleBlockItem> IMPERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE_PANE);
	public static final RegistryObject<SimpleBlockItem> BABY_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE);
	public static final RegistryObject<SimpleBlockItem> BABY_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE);
	public static final RegistryObject<SimpleBlockItem> ADULT_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE);
	public static final RegistryObject<SimpleBlockItem> ADULT_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE);
	public static final RegistryObject<SimpleBlockItem> PRIMAL_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE);
	public static final RegistryObject<SimpleBlockItem> PRIMAL_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE);
	public static final RegistryObject<SimpleBlockItem> UNDEAD_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE);
	public static final RegistryObject<SimpleBlockItem> UNDEAD_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE);

	//public static final RegistryObject<SimpleBlockItem> NEURAL_INTERCEPTOR = registerSimpleBlockItem(ModBlocks.NEURAL_INTERCEPTOR, ModRarities.VERY_RARE);

	//## Misc
	public static final RegistryObject<SimpleBlockItem> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final RegistryObject<SimpleBlockItem> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final RegistryObject<SimpleBlockItem> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> FULL_FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FULL_FLESH_DOOR);
	public static final RegistryObject<SimpleBlockItem> FLESH_SPIKE = registerSimpleBlockItem(ModBlocks.FLESH_SPIKE);
	public static final RegistryObject<SimpleBlockItem> YELLOW_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.YELLOW_BIO_LANTERN);
	public static final RegistryObject<SimpleBlockItem> BLUE_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.BLUE_BIO_LANTERN);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> PRIMORDIAL_BIO_LANTERN = registerBlockItem(ModBlocks.PRIMORDIAL_BIO_LANTERN, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<ObfuscatedTooltipBlockItem> BLOOMLIGHT = registerBlockItem(ModBlocks.BLOOMLIGHT, ObfuscatedTooltipBlockItem::new);
	public static final RegistryObject<FleshChainBlockItem> TENDON_CHAIN = registerBlockItem(ModBlocks.TENDON_CHAIN, FleshChainBlockItem::new);
	public static final RegistryObject<SimpleBlockItem> VIAL_HOLDER = registerSimpleBlockItem(ModBlocks.VIAL_HOLDER);

	//# Spawn Eggs
	public static final RegistryObject<ForgeSpawnEggItem> HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.HUNGRY_FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final RegistryObject<ForgeSpawnEggItem> LEGACY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.LEGACY_FLESH_BLOB, 0xeec5da, 0xffc0cb);
	public static final RegistryObject<ForgeSpawnEggItem> PRIMORDIAL_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_FLESH_BLOB, 0xde6074, 0xc343fe);
	public static final RegistryObject<ForgeSpawnEggItem> PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB, 0x752144, 0x752144);

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

	private static <T extends Block, I extends BlockItem> RegistryObject<I> registerBlockItem(RegistryObject<T> blockHolder, IBlockItemFactory<T, I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties()));
	}

	private static <T extends Block, I extends BlockItem> RegistryObject<I> registerBlockItem(RegistryObject<T> blockHolder, IBlockItemFactory<T, I> factory, Rarity rarity) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties().rarity(rarity)));
	}

	private static <T extends EntityType<? extends Mob>> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(RegistryObject<T> mobHolder, int primaryColor, int accentColor) {
		return ITEMS.register(mobHolder.getId().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(mobHolder, primaryColor, accentColor, createProperties()));
	}

	private static <T extends Serum> RegistryObject<SerumItem> registerSerumItem(RegistryObject<T> registryObject) {
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createProperties().stacksTo(8).rarity(ModRarities.UNCOMMON), registryObject));
	}

	private static RegistryObject<SimpleItem> registerSimpleVialItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
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
		return new Item.Properties().rarity(ModRarities.COMMON);
	}

	private static RegistryObject<SimpleItem> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	private interface SwordSmithy<T extends SwordItem> {
		AttributeSupplier PLAYER_ATTRIBUTES = Player.createAttributes().build();

		static <T extends SwordItem> T forge(SwordSmithy<T> smithy, Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
			int attackDamageModifier = Mth.floor(attackDamage - (PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_DAMAGE) + tier.getAttackDamageBonus()));
			float attackSpeedModifier = attackSpeed - (float) PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_SPEED);
			return smithy.forge(tier, attackDamageModifier, attackSpeedModifier, properties);
		}

		T forge(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties);
	}

	interface IBlockItemFactory<T extends Block, I extends BlockItem> {
		I create(T block, Item.Properties properties);
	}

}
