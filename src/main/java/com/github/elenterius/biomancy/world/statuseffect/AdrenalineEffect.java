package com.github.elenterius.biomancy.world.statuseffect;

import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class AdrenalineEffect extends StatusEffect {

	protected double damageMultiplier;
	protected UUID attackDamageUUID;

	public AdrenalineEffect(MobEffectCategory category, int color) {
		super(category, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (!isBeneficial() && livingEntity instanceof Player player) {
			if (!livingEntity.isSleeping()) {
				player.awardStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 20 * 2); //increase insomnia counter
			}
			player.causeFoodExhaustion(0.05F * (amplifier + 1f));
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
