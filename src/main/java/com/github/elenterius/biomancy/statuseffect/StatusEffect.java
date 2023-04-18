package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StatusEffect extends MobEffect {

	protected final boolean isCurable;

	public StatusEffect(MobEffectCategory category, int color) {
		this(category, color, true);
	}

	public StatusEffect(MobEffectCategory category, int color, boolean isCurable) {
		super(category, color);
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
