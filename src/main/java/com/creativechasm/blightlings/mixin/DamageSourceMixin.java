package com.creativechasm.blightlings.mixin;

import com.creativechasm.blightlings.handler.ModEntityDamageSource;
import com.creativechasm.blightlings.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public class DamageSourceMixin
{
    @Inject(method = "causePlayerDamage", at = @At("HEAD"), cancellable = true)
    private static void onCausePlayerDamage(PlayerEntity player, CallbackInfoReturnable<DamageSource> cir) {
        if (player.getHeldItemMainhand().getItem() == ModItems.BLIGHTBRINGER_AXE.get()) {
            ModEntityDamageSource damageSource = new ModEntityDamageSource("player", "blight_thorn", player, (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE));
            damageSource.setIsThornsDamage().setMagicDamage(); //mutate damage
            cir.setReturnValue(damageSource);
        }
    }

    @Inject(method = "causeMobDamage", at = @At("HEAD"), cancellable = true)
    private static void onCauseMobDamage(LivingEntity mob, CallbackInfoReturnable<DamageSource> cir) {
        if (mob.getHeldItemMainhand().getItem() == ModItems.BLIGHTBRINGER_AXE.get()) {
            ModEntityDamageSource damageSource = new ModEntityDamageSource("mob", "blight_thorn", mob, (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
            damageSource.setIsThornsDamage().setMagicDamage(); //mutate damage
            cir.setReturnValue(damageSource);
        }
    }
}
