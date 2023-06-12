package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.util.Mth;

public interface StackingStatusEffect {

	static int computeAmplifierFrom(StackingStatusEffect statusEffect, int amplifierA, int amplifierB) {
		int maxAmplifier = statusEffect.getMaxEffectStack() - 1;
		int combinedAmplifier = Math.max(amplifierA, amplifierB) + 1;
		return Mth.clamp(combinedAmplifier, 0, maxAmplifier);
	}

	int getMaxEffectStack();
}
