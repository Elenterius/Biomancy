package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class AttackDamageEffect extends StatusEffect {

	protected double damageMultiplier;
	protected UUID attackDamageUUID;

	public AttackDamageEffect(MobEffectCategory category, int color) {
		this(category, color, true);
	}

	public AttackDamageEffect(MobEffectCategory category, int color, boolean isCurable) {
		super(category, color, isCurable);
	}

	public AttackDamageEffect addAttackDamageModifier(String uuid, double damageMultiplier, double amount, AttributeModifier.Operation operation) {
		this.damageMultiplier = damageMultiplier;
		this.attackDamageUUID = UUID.fromString(uuid);
		addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid, amount, operation);
		return this;
	}

	@Override
	public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
		if (modifier.getId().equals(attackDamageUUID)) {
			return (amplifier + 1) * damageMultiplier;
		}
		return super.getAttributeModifierValue(amplifier, modifier);
	}

}
