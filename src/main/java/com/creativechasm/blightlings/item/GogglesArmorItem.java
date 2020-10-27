package com.creativechasm.blightlings.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class GogglesArmorItem extends ArmorItem implements IRevealInvisible<GogglesArmorItem>
{
    public GogglesArmorItem(IArmorMaterial materialIn, Properties properties) {
        super(materialIn, EquipmentSlotType.HEAD, properties);
    }

    @Override
    public boolean canRevealInvisibleEntity(ItemStack stack, PlayerEntity player, Entity invisibleEntity) {
        return true;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (world.isRemote()) return;

        if (player.isPotionActive(Effects.BLINDNESS)) {
            player.removePotionEffect(Effects.BLINDNESS);
        }

        EffectInstance activeEffect = player.getActivePotionEffect(Effects.NIGHT_VISION);
        if (activeEffect == null) {
            EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, 360, 0, false, false);
            if (player.isPotionApplicable(effectInstance)) {
                player.addPotionEffect(effectInstance);
            }
        }
        else if (player.ticksExisted % 20 == 0 && activeEffect.getDuration() < 250) {
            EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, 360, 0, false, false);
            if (player.isPotionApplicable(effectInstance)) {
                player.addPotionEffect(effectInstance);
            }
        }
    }

}
