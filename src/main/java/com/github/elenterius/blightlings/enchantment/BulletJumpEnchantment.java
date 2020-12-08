package com.github.elenterius.blightlings.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

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

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public boolean canVillagerTrade() {
        return false;
    }

    @Override
    public boolean canGenerateInLoot() {
        return false;
    }

    public void executeBulletJump(PlayerEntity entity, int level, boolean applySneakFix) {
        Vector3d lookVec = entity.getLookVec();
        Vector3d motion = entity.getMotion();

        boolean startedFaLlFlying = tryToStartFallFlying(entity); // get free boosted elytra takeoff without rockets :)
        float factor = startedFaLlFlying ? 0.3f : 0.5f;

        if (!startedFaLlFlying && !entity.getEntityWorld().isRemote()) {
            EffectInstance effectInstance = new EffectInstance(Effects.SLOW_FALLING, 5, 0, false, false);
            if (entity.isPotionApplicable(effectInstance)) {
                entity.addPotionEffect(effectInstance);
            }
        }

        double jumpMotion = motion.y + factor * level + 0.15f;
        entity.setMotion(motion.x + lookVec.x * jumpMotion, motion.y + lookVec.y * ((factor - 0.1f) * level), motion.z + lookVec.z * jumpMotion);
        entity.isAirBorne = true;

        //WARNING: "Dev moved wrongly!" --> caused by sneaking and trying to jump off block ledge (fall off prevention)
        if (applySneakFix) entity.setSneaking(false); // the fix

        entity.addExhaustion(0.3F);
    }

    private static boolean tryToStartFallFlying(PlayerEntity entity) {
        if (!entity.isElytraFlying() && !entity.isInWater() && !entity.isPotionActive(Effects.LEVITATION)) {
            ItemStack itemstack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (itemstack.canElytraFly(entity)) {
                entity.startFallFlying();
                return true;
            }
        }
        return false;
    }
}
