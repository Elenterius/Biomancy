package com.github.elenterius.blightlings.statuseffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StatusEffect extends Effect {
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
		//do nothing
	}

	@Override
	public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {
		//do nothing
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return false;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return isCurable ? super.getCurativeItems() : Collections.emptyList();
	}
}
