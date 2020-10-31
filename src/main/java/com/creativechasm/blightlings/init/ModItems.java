package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.item.BlightbringerAxeItem;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import com.creativechasm.blightlings.item.ModItemTier;
import com.creativechasm.blightlings.item.ModSpawnEggItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems
{
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<GogglesArmorItem> TRUE_SIGHT_GOGGLES = ITEM_REGISTRY.register("true_sight_goggles", () -> new GogglesArmorItem(ArmorMaterial.IRON, new Item.Properties().group(BlightlingsMod.ITEM_GROUP).rarity(Rarity.EPIC)));
    public static final RegistryObject<BlightbringerAxeItem> BLIGHTBRINGER_AXE = ITEM_REGISTRY.register("blightbringer_axe",
            () -> new BlightbringerAxeItem(ModItemTier.BLIGHT_AMALGAM, 5F, -3.15F, new Item.Properties().group(BlightlingsMod.ITEM_GROUP).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> BLIGHT_SHARD = ITEM_REGISTRY.register("blight_shard", () -> new Item(new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));
    public static final RegistryObject<Item> BLIGHT_SAC = ITEM_REGISTRY.register("blight_sac", () -> new Item(new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));
    public static final RegistryObject<Item> BLIGHT_GOO = ITEM_REGISTRY.register("blight_goo", () -> new Item(new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));
    public static final RegistryObject<Item> BLIGHT_STRING = ITEM_REGISTRY.register("blight_string", () -> new Item(new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));
    public static final RegistryObject<Item> BLIGHT_EYE = ITEM_REGISTRY.register("blight_eye", () -> new Item(new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));

    public static final RegistryObject<ModSpawnEggItem> BLOBLING_SPAWN_EGG = ITEM_REGISTRY.register("blobling_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.BLOBLING, 0x764da2, 0xff40ff, new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));
    public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEM_REGISTRY.register("brood_mother_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.BROOD_MOTHER, 0x49345e, 0xda70d6, new Item.Properties().group(BlightlingsMod.ITEM_GROUP)));

}
