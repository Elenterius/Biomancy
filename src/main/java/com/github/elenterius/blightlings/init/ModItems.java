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

public abstract class ModItems
{
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BlightlingsMod.MOD_ID);

    private static Item.Properties createItemProperties() {
        return new Item.Properties().group(BlightlingsMod.ITEM_GROUP);
    }

    public static final RegistryObject<GogglesArmorItem> TRUE_SIGHT_GOGGLES = ITEM_REGISTRY.register("true_sight_goggles", () -> new GogglesArmorItem(ArmorMaterial.IRON, createItemProperties().rarity(Rarity.EPIC)));
    public static final RegistryObject<ModAxeItem> BLIGHTBRINGER_AXE = ITEM_REGISTRY.register("blightbringer_axe", () -> new ModAxeItem(ModItemTier.BLIGHT_AMALGAM, 5F, -3.15F, createItemProperties().rarity(Rarity.EPIC)));
    public static final RegistryObject<KhopeshItem> BLIGHT_KHOPESH = ITEM_REGISTRY.register("blight_khopesh", () -> new KhopeshItem(ModItemTier.BLIGHT_AMALGAM, 5F, -2.75F, createItemProperties().rarity(Rarity.EPIC)));
    public static final RegistryObject<SickleItem> BLIGHT_SICKLE = ITEM_REGISTRY.register("blight_sickle", () -> new SickleItem(ModItemTier.BLIGHT_AMALGAM, 3, -2.5F, createItemProperties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> BLIGHT_SHARD = ITEM_REGISTRY.register("blight_shard", () -> new Item(createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_SAC = ITEM_REGISTRY.register("blight_sac", () -> new Item(createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_GOO = ITEM_REGISTRY.register("blight_goo", () -> new Item(createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_STRING = ITEM_REGISTRY.register("blight_string", () -> new Item(createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_EYE = ITEM_REGISTRY.register("blight_eye", () -> new Item(createItemProperties()));

    public static final RegistryObject<Item> LUMINESCENT_SPORES = ITEM_REGISTRY.register("luminescent_spores", () -> new Item(createItemProperties()));
    public static final RegistryObject<PotionBeetleItem> POTION_BEETLE = ITEM_REGISTRY.register("potion_beetle", () -> new PotionBeetleItem(createItemProperties().maxStackSize(1), 20f));

    public static final RegistryObject<ModSpawnEggItem> BLOBLING_SPAWN_EGG = ITEM_REGISTRY.register("blobling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BLOBLING, 0x764da2, 0xff40ff, createItemProperties()));
    public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEM_REGISTRY.register("brood_mother_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, createItemProperties()));
    public static final RegistryObject<ModSpawnEggItem> BEETLING_SPAWN_EGG = ITEM_REGISTRY.register("beetling_spawn_egg", () -> new ModSpawnEggItem(ModEntityTypes.BEETLING, 0x764da2, 0xff40ff, createItemProperties()));

    /* **** Block Items ********************************************* */
    public static final RegistryObject<Item> INFERTILE_SOIL = ITEM_REGISTRY.register("infertile_soil", () -> new BlockItem(ModBlocks.INFERTILE_SOIL.get(), createItemProperties()));
    public static final RegistryObject<Item> LUMINOUS_SOIL = ITEM_REGISTRY.register("luminous_soil", () -> new BlockItem(ModBlocks.LUMINOUS_SOIL.get(), createItemProperties()));

    public static final RegistryObject<Item> LILYTREE_SAPLING = ITEM_REGISTRY.register("lilytree_sapling", () -> new BlockItem(ModBlocks.LILY_TREE_SAPLING.get(), createItemProperties()));
    public static final RegistryObject<Item> LILYTREE_STEM = ITEM_REGISTRY.register("lilytree_stem", () -> new BlockItem(ModBlocks.LILY_TREE_STEM.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_MOSS_SLAB = ITEM_REGISTRY.register("blight_moss_slab", () -> new BlockItem(ModBlocks.BLIGHT_MOSS_SLAB.get(), createItemProperties()));

    public static final RegistryObject<Item> BLIGHT_PUSTULE_SMALL = ITEM_REGISTRY.register("blight_pustule_0", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_SMALL.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_PUSTULE_BIG = ITEM_REGISTRY.register("blight_pustule_1", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_BIG.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_PUSTULE_BIG_SMALL = ITEM_REGISTRY.register("blight_pustule_2", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_BIG_AND_SMALL.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_PUSTULE_SMALL_GROUP = ITEM_REGISTRY.register("blight_pustule_3", () -> new BlockItem(ModBlocks.BLIGHT_PUSTULE_SMALL_GROUP.get(), createItemProperties()));

    public static final RegistryObject<Item> BLIGHT_SPROUT = ITEM_REGISTRY.register("blight_sprout", () -> new BlockItem(ModBlocks.BLIGHT_SPROUT.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_SPROUT_SMALL = ITEM_REGISTRY.register("blight_sprout_small", () -> new BlockItem(ModBlocks.BLIGHT_SPROUT_SMALL.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_TENTACLE_0 = ITEM_REGISTRY.register("blight_tentacle_0", () -> new BlockItem(ModBlocks.BLIGHT_TENTACLE_0.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_TENTACLE_1 = ITEM_REGISTRY.register("blight_tentacle_1", () -> new BlockItem(ModBlocks.BLIGHT_TENTACLE_1.get(), createItemProperties()));
    public static final RegistryObject<Item> BLIGHT_SHROOM_TALL = ITEM_REGISTRY.register("blight_shroom_tall", () -> new BlockItem(ModBlocks.BLIGHT_SHROOM_TALL.get(), createItemProperties()));
    public static final RegistryObject<Item> CANDELABRA_FUNGUS = ITEM_REGISTRY.register("candelabra_fungus", () -> new BlockItem(ModBlocks.CANDELABRA_FUNGUS.get(), createItemProperties()));
}
