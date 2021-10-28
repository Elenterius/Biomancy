package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.stats.Stats;

import java.util.UUID;

public class AdrenalineEffect extends StatusEffect {

	protected double damageMultiplier;
	protected UUID attackDamageUUID;

	public AdrenalineEffect(EffectType type, int color) {
		super(type, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (!isBeneficial() && livingEntity instanceof PlayerEntity) {
			if (!livingEntity.isSleeping()) {
				((PlayerEntity) livingEntity).awardStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 20 * 2); //increase insomnia counter
			}
			((PlayerEntity) livingEntity).causeFoodExhaustion(0.05F * (amplifier + 1f));
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	public AdrenalineEffect addAttackDamageModifier(String uuid, double damageMultiplier, double amount, AttributeModifier.Operation operation) {
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
