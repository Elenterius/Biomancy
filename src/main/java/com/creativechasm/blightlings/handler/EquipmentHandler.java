package com.creativechasm.blightlings.handler;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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

            if (oldStack.getItem() == ModItems.TRUE_SIGHT_GOGGLES && newStack.getItem() != ModItems.TRUE_SIGHT_GOGGLES) { // un-equip
                ModItems.TRUE_SIGHT_GOGGLES.cancelEffect(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity entity = (PlayerEntity) event.getEntityLiving();

        if (entity.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == ModItems.CLIMBING_BOOTS) {
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

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity entity = (PlayerEntity) event.getEntityLiving();

        if (entity.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == ModItems.JUMPING_PANTS) {
            if (!entity.isPassenger() && (entity.getFoodStats().getFoodLevel() > 6.0F || entity.abilities.isCreativeMode)) {
                if (event.getEntityLiving().isSneaking()) {
                    Vector3d vec = entity.getLookVec();
                    Vector3d motion = entity.getMotion();
                    float xz = 1 + entity.jumpMovementFactor * 8;
                    entity.setVelocity(motion.x + vec.x * xz, motion.y + vec.y * (1 + entity.jumpMovementFactor * 2), motion.z + vec.z * xz);
                    entity.addExhaustion(0.2F);
                }
            }
        }
    }
}
