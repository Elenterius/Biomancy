package com.creativechasm.blightlings.handler;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.init.ModEnchantments;
import com.creativechasm.blightlings.init.ModItems;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
        PlayerEntity entity = (PlayerEntity) event.getEntityLiving();

        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CLIMBING, entity.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0) {
            if (entity.collidedHorizontally && (entity.getFoodStats().getFoodLevel() > 6.0F || entity.abilities.isCreativeMode)) {
                if (!entity.isOnLadder()) {  // normal climbing
                    Vector3d motion = entity.getMotion();
                    double minmax = 0.015f;
                    double mx = MathHelper.clamp(motion.x, -minmax, minmax);
                    double mz = MathHelper.clamp(motion.z, -minmax, minmax);
                    double my = entity.isSneaking() ? Math.max(motion.y, 0d) : 0.1d; //stick to wall when sneaking
                    entity.fallDistance = 0f;
                    entity.setMotion(mx, my, mz);
                }
                else if (!entity.isSneaking()) { // ladder climbing
                    entity.setMotion(entity.getMotion().add(0d, 0.08700634D, 0d));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity entity = (PlayerEntity) event.getEntityLiving();

        if (!entity.isPassenger() && (entity.getFoodStats().getFoodLevel() > 6.0F || entity.abilities.isCreativeMode)) {
            int bulletJumpLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BULLET_JUMP, entity.getItemStackFromSlot(EquipmentSlotType.LEGS));
            if (bulletJumpLevel > 0 && event.getEntityLiving().isSneaking()) {
                Vector3d lookVec = entity.getLookVec();
                Vector3d motion = entity.getMotion();

                boolean startedFalFlying = tryToStartFallFlying(entity); // get free boosted elytra takeoff without rockets :)
                float factor = startedFalFlying ? 0.3f : 0.5f;

                double jumpMotion = motion.y + factor * bulletJumpLevel + 0.15f;
                entity.setMotion(motion.x + lookVec.x * jumpMotion, motion.y + lookVec.y * ((factor - 0.1f) * bulletJumpLevel), motion.z + lookVec.z * jumpMotion);
                entity.isAirBorne = true;

                //WARNING: "Dev moved wrongly!" --> caused by sneaking and trying to jump off block ledge (fall off prevention)
                entity.setSneaking(false); // the fix

                entity.addExhaustion(0.3F);
            }
        }
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
