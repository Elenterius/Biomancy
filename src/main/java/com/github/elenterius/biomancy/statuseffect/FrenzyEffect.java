package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;

public class FrenzyEffect extends AttackDamageEffect implements StackingStatusEffect {

	public FrenzyEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public int getMaxEffectStack() {
		return 3;
	}

}
