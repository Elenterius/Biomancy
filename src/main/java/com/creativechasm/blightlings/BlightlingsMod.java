package com.creativechasm.blightlings;

import com.creativechasm.blightlings.init.ModEnchantments;
import com.creativechasm.blightlings.init.ModEntityTypes;
import com.creativechasm.blightlings.init.ModItems;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
        ModItems.ITEM_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityTypes.ENTITY_TYPE_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
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
