package com.github.elenterius.blightlings.statuseffect;

import com.github.elenterius.blightlings.init.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StatusEffect extends Effect
{
    private final boolean isCurable;

    public StatusEffect(EffectType type, int liquidColor) {
        this(type, liquidColor, true);
    }

    public StatusEffect(EffectType type, int liquidColor, boolean isCurable) {
        super(type, liquidColor);
        this.isCurable = isCurable;
    }

    @Override
    public void performEffect(LivingEntity livingEntity, int amplifier) {
        if (this == ModEffects.BLIGHT_INFECTION.get()) {
            livingEntity.attackEntityFrom(DamageSource.MAGIC, (amplifier + 1f) * 0.5f);
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity) livingEntity).addExhaustion((amplifier + 1f) * 0.0025f);
            }
        }
        else if (this == ModEffects.FRENZY.get() && livingEntity instanceof PlayerEntity) {
            ((PlayerEntity) livingEntity).addExhaustion((amplifier + 1f) * 0.0025f);
        }
    }

    @Override
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {
        //do nothing
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        if (this == ModEffects.BLIGHT_INFECTION.get()) {
            int nTicks = 40 >> amplifier;
            return nTicks <= 0 || duration % nTicks == 0;
        }
        else if (this == ModEffects.FRENZY.get()) {
            int nTicks = 20 >> amplifier;
            return nTicks <= 0 || duration % nTicks == 0;
        }
        return false;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return isCurable ? super.getCurativeItems() : Collections.emptyList();
    }
}
