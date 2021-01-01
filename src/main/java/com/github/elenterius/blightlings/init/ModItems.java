package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.item.*;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BlightlingsMod.MOD_ID);

	public static final RegistryObject<GogglesArmorItem> TRUE_SIGHT_GOGGLES = ITEMS.register("true_sight_goggles", () -> new GogglesArmorItem(ArmorMaterial.IRON, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<ModAxeItem> BLIGHTBRINGER_AXE = ITEMS.register("blightbringer_axe", () -> new ModAxeItem(ModItemTier.BLIGHT_AMALGAM, 5F, -3.15F, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<KhopeshItem> BLIGHT_KHOPESH = ITEMS.register("blight_khopesh", () -> new KhopeshItem(ModItemTier.BLIGHT_AMALGAM, 5F, -2.75F, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<SickleItem> BLIGHT_SICKLE = ITEMS.register("blight_sickle", () -> new SickleItem(ModItemTier.BLIGHT_AMALGAM, 3, -2.5F, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<LeechClawItem> LEECH_CLAW = ITEMS.register("leech_claw", () -> new LeechClawItem(ModItemTier.BLIGHT_AMALGAM, 3, -2.2f, createItemProperties().rarity(Rarity.EPIC)));
	public static final RegistryObject<LongRangeClawItem> LONG_RANGE_CLAW = ITEMS.register("long_range_claw", () -> new LongRangeClawItem(ModItemTier.BLIGHT_AMALGAM, 3, -2.4f, 60, createItemProperties().rarity(Rarity.EPIC)));

	public static final RegistryObject<InfestedRifleItem> INFESTED_RIFLE = ITEMS.register("infested_rifle", () -> new InfestedRifleItem(createItemProperties().maxStackSize(1).maxDamage(384).rarity(Rarity.EPIC)));

	public static final RegistryObject<Item> LUMINESCENT_SPORES = ITEMS.register("luminescent_spores", () -> new Item(createItemProperties()));
	public static final RegistryObject<PotionBeetleItem> POTION_BEETLE = ITEMS.register("potion_beetle", () -> new PotionBeetleItem(createItemProperties().maxStackSize(1), 20f));
	public static final RegistryObject<MasonBeetleItem> MASON_BEETLE = ITEMS.register("mason_beetle", () -> new MasonBeetleItem(createItemProperties().maxStackSize(1), 20f));

	public static final RegistryObject<Item> BLIGHT_SHARD = ITEMS.register("blight_shard", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> BLIGHT_SAC = ITEMS.register("blight_sac", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> BLIGHT_STRING = ITEMS.register("blight_string", () -> new Item(createItemProperties()));
	public static final RegistryObject<Item> OCULUS = ITEMS.register("blight_eye", () -> new Item(createItemProperties()));
	public static final RegistryObject<DecayingItem> BLIGHT_GOO = ITEMS.register("blight_goo", () -> new DecayingItem(2 * 60, 0.5f, createItemProperties()));
	public static final RegistryObject<DecayingItem> BLIGHT_QUARTZ = ITEMS.register("blight_quartz", () -> new DecayingItem(20 * 60, 0.5f, createItemProperties()));

	public static final RegistryObject<ModSpawnEggItem> BLOBLING_SPAWN_EGG = ITEMS.register("blobling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BLOBLING, 0x764da2, 0xff40ff, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEMS.register("brood_mother_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, createItemProperties()));
	public static final RegistryObject<ModSpawnEggItem> BEETLING_SPAWN_EGG = ITEMS.register("beetling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BEETLING, 0x764da2, 0xff40ff, createItemProperties()));

	/* **** Block Items ********************************************* */
	public static final RegistryObject<BlockItem> INFERTILE_SOIL = ITEMS.register("infertile_soil", () -> new BlockItem(ModBlocks.INFERTILE_SOIL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> LUMINOUS_SOIL = ITEMS.register("luminous_soil", () -> new BlockItem(ModBlocks.LUMINOUS_SOIL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_QUARTZ_ORE = ITEMS.register("blight_quartz_ore", () -> new BlockItem(ModBlocks.BLIGHT_QUARTZ_ORE.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> PRESERVATION_JAR = ITEMS.register("preservation_jar", () -> new BlockItem(ModBlocks.PRESERVATION_JAR.get(), createItemProperties()));

	public static final RegistryObject<BlockItem> LILYTREE_SAPLING = ITEMS.register("lilytree_sapling", () -> new BlockItem(ModBlocks.LILY_TREE_SAPLING.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> LILYTREE_STEM = ITEMS.register("lilytree_stem", () -> new BlockItem(ModBlocks.LILY_TREE_STEM.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_MOSS_SLAB = ITEMS.register("blight_moss_slab", () -> new BlockItem(ModBlocks.BLIGHT_MOSS_SLAB.get(), createItemProperties()));

	public static final RegistryObject<BlockItem> BLIGHT_PUSTULE_SMALL = ITEMS.register("blight_pustule_0", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_SMALL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_PUSTULE_BIG = ITEMS.register("blight_pustule_1", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_BIG.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_PUSTULE_BIG_SMALL = ITEMS.register("blight_pustule_2", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_BIG_AND_SMALL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_PUSTULE_SMALL_GROUP = ITEMS.register("blight_pustule_3", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_SMALL_GROUP.get(), createItemProperties()));

	public static final RegistryObject<BlockItem> BLIGHT_SPROUT = ITEMS.register("blight_sprout", () -> new BlockItem(ModBlocks.BLIGHT_SPROUT.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_SPROUT_SMALL = ITEMS.register("blight_sprout_small", () -> new BlockItem(ModBlocks.BLIGHT_SPROUT_SMALL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_TENTACLE_0 = ITEMS.register("blight_tentacle_0", () -> new BlockItem(ModBlocks.BLIGHT_TENTACLE_0.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_TENTACLE_1 = ITEMS.register("blight_tentacle_1", () -> new BlockItem(ModBlocks.BLIGHT_TENTACLE_1.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> BLIGHT_SHROOM_TALL = ITEMS.register("blight_shroom_tall", () -> new BlockItem(ModBlocks.BLIGHT_SHROOM_TALL.get(), createItemProperties()));
	public static final RegistryObject<BlockItem> CANDELABRA_FUNGUS = ITEMS.register("candelabra_fungus", () -> new BlockItem(ModBlocks.CANDELABRA_FUNGUS.get(), createItemProperties()));

	private ModItems() {}

	private static Item.Properties createItemProperties() {
		return new Item.Properties().group(BlightlingsMod.ITEM_GROUP);
	}
}
