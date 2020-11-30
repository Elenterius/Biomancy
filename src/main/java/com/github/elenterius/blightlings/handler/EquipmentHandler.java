package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModEnchantments;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.item.GogglesArmorItem;
import com.github.elenterius.blightlings.item.KhopeshItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class EquipmentHandler
{
    @SubscribeEvent
    public static void onLivingEquipmentChange(final LivingEquipmentChangeEvent event) {
        if (!event.getEntityLiving().isServerWorld()) return;

        if (event.getSlot() == EquipmentSlotType.HEAD) {
            GogglesArmorItem goggles = ModItems.TRUE_SIGHT_GOGGLES.get();
            if (event.getFrom().getItem() == goggles && event.getTo().getItem() != goggles) { // un-equip
                goggles.cancelEffect(event.getEntityLiving());
            }
        }

        if (event.getSlot() == EquipmentSlotType.MAINHAND) {
            //old item
            if (event.getFrom().getItem() instanceof KhopeshItem) {
                ModifiableAttributeInstance modifiableAttributeInstance = event.getEntityLiving().getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
                if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER)) {
                    modifiableAttributeInstance.removeModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER);
                }
            }

            //new item
            if (event.getTo().getItem() instanceof KhopeshItem && event.getEntityLiving().isPassenger()) {
                ModifiableAttributeInstance modifiableAttributeInstance = event.getEntityLiving().getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
                if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER)) {
                    modifiableAttributeInstance.applyNonPersistentModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityMount(final EntityMountEvent event) {
        //on dismounting make sure the riding attack modifier is removed
        if (event.isDismounting() && event.getEntityMounting() instanceof LivingEntity) {
            ModifiableAttributeInstance modifiableAttributeInstance = ((LivingEntity) event.getEntityMounting()).getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
            if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER)) {
                modifiableAttributeInstance.removeModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER);
            }
        }

        //on mounting add attack modifier when holding a khopesh weapon
        if (event.isMounting() && event.getEntityMounting() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) event.getEntityMounting()).getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof KhopeshItem) {
                ModifiableAttributeInstance modifiableAttributeInstance = ((LivingEntity) event.getEntityMounting()).getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
                if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER)) {
                    modifiableAttributeInstance.applyNonPersistentModifier(KhopeshItem.RIDING_ATTACK_DAMAGE_MODIFIER);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;

        if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CLIMBING, event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET)) > 0) {
            ModEnchantments.CLIMBING.tryToClimb((PlayerEntity) event.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingJump(final LivingEvent.LivingJumpEvent event) {
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
