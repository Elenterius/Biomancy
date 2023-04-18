package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ArmorShredEffect extends StatusEffect {

	private final int maxAttributeMultiplier;

	public ArmorShredEffect(MobEffectCategory category, int maxAttributeMultiplier, int color) {
		super(category, color, false);
		this.maxAttributeMultiplier = maxAttributeMultiplier;
	}

	@Override
	public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
		int multiplier = amplifier + 1;
		return modifier.getAmount() * Math.min(multiplier, maxAttributeMultiplier);
	}

}
