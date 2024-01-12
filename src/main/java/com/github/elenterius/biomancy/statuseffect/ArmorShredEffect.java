package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ArmorShredEffect extends StatusEffect implements StackingStatusEffect {

	private final int maxStackSize;

	public ArmorShredEffect(MobEffectCategory category, int maxStackSize, int color) {
		super(category, color, false);
		this.maxStackSize = maxStackSize;
	}

	@Override
	public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
		int multiplier = amplifier + 1;
		return modifier.getAmount() * multiplier;
	}

	@Override
	public int getMaxEffectStack() {
		return maxStackSize;
	}

}
