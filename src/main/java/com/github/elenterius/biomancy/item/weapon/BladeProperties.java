package com.github.elenterius.biomancy.item.weapon;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public record BladeProperties(float attackDamageModifier, float attackSpeedModifier) {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		public static final AttributeSupplier PLAYER_ATTRIBUTES = Player.createAttributes().build();

		float attackDamage;
		float attackSpeed;

		public Builder attackSpeed(float value) {
			attackSpeed = value;
			return this;
		}

		public Builder attackDamage(float value) {
			attackDamage = value;
			return this;
		}

		public BladeProperties build() {
			float damageModifier = attackDamage - (float) PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_DAMAGE);
			float speedModifier = attackSpeed - (float) PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_SPEED);
			return new BladeProperties(damageModifier, speedModifier);
		}

	}

}
