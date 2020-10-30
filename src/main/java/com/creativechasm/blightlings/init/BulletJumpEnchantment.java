package com.creativechasm.blightlings.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class BulletJumpEnchantment extends Enchantment
{
    public BulletJumpEnchantment(Rarity rarityIn) {
        super(rarityIn, EnchantmentType.ARMOR_LEGS, new EquipmentSlotType[]{EquipmentSlotType.LEGS});
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    //can't be acquired by enchantment table?
    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    //can be sold by villager
    public boolean func_230309_h_() {
        return false;
    }

    //can be enchanted on loot?
    public boolean func_230310_i_() {
        return false;
    }
}
