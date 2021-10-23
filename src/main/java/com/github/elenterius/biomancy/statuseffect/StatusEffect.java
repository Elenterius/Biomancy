package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StatusEffect extends Effect {
	private final boolean isCurable;

	public StatusEffect(EffectType type, int color) {
		this(type, color, true);
	}

	public StatusEffect(EffectType type, int color, boolean isCurable) {
		super(type, color);
		this.isCurable = isCurable;
	}

	public <E extends StatusEffect> E addModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
		//noinspection unchecked
		return (E) addAttributeModifier(attribute, uuid, amount, operation);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		//do nothing
	}

	@Override
	public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
		//do nothing
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return false;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return isCurable ? super.getCurativeItems() : Collections.emptyList();
	}
}
