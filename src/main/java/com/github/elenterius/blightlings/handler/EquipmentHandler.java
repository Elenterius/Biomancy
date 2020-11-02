package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModEnchantments;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.item.GogglesArmorItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class EquipmentHandler
{
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!event.getEntityLiving().isServerWorld()) return;

        if (event.getSlot() == EquipmentSlotType.HEAD) {
            LivingEntity entity = event.getEntityLiving();
            ItemStack oldStack = event.getFrom();
            ItemStack newStack = event.getTo();

            GogglesArmorItem goggles = ModItems.TRUE_SIGHT_GOGGLES.get();
            if (oldStack.getItem() == goggles && newStack.getItem() != goggles) { // un-equip
                goggles.cancelEffect(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;

        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CLIMBING, event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET)) > 0) {
            ModEnchantments.CLIMBING.tryToClimb((PlayerEntity) event.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();

        if (!player.isPassenger() && (player.getFoodStats().getFoodLevel() > 6.0F || player.abilities.isCreativeMode)) {
            int bulletJumpLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BULLET_JUMP, player.getItemStackFromSlot(EquipmentSlotType.LEGS));
            if (bulletJumpLevel > 0 && event.getEntityLiving().isSneaking()) {
                ModEnchantments.BULLET_JUMP.executeBulletJump(player, bulletJumpLevel, true);
            }
        }
    }


}
