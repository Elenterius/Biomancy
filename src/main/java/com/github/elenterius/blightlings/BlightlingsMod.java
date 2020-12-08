package com.github.elenterius.blightlings;

import com.github.elenterius.blightlings.init.*;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(BlightlingsMod.MOD_ID)
public class BlightlingsMod
{
    public static final String MOD_ID = "blightlings";
    public static final Logger LOGGER = LogManager.getLogger();

    public BlightlingsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEM_REGISTRY.register(modEventBus);
        ModBlocks.BLOCK_REGISTRY.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPE_REGISTRY.register(modEventBus);
        ModEffects.EFFECT_REGISTRY.register(modEventBus);

//        ModFeatures.FEATURE_REGISTRY.register(modEventBus);
        ModFeatures.SURFACE_BUILDER_REGISTRY.register(modEventBus);
//        ModBiomes.BIOME_REGISTRY.register(modEventBus);
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, MOD_ID)
    {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.TRUE_SIGHT_GOGGLES.get());
        }

        @Override
        public void fill(@Nonnull NonNullList<ItemStack> items) {
            super.fill(items);
            items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.CLIMBING, 1)));
            items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP, 1)));
            items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP, 2)));
            items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP, 3)));
        }
    };

}
