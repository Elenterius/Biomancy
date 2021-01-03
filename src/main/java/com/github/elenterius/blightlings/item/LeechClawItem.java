package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.init.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.UUID;

public class LeechClawItem extends ClawWeaponItem {

	public static AttributeModifier ATTACK_REACH_MODIFIER = new AttributeModifier(UUID.fromString("d2589c7b-7b0f-4850-910a-31598f2f349d"), "attack_reach_modifier", -0.5f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier MOVEMENT_MODIFIER = new AttributeModifier(UUID.fromString("0823f523-d756-4725-9a6a-098059458c4b"), "movement_modifier", 0.1f, AttributeModifier.Operation.MULTIPLY_BASE);

	public LeechClawItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ModAttributes.getAttackDistance(), ATTACK_REACH_MODIFIER);
		builder.put(Attributes.MOVEMENT_SPEED, MOVEMENT_MODIFIER);
	}

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		super.onCriticalHitEntity(stack, attacker, target);

		EffectInstance effect = attacker.getActivePotionEffect(Effects.ABSORPTION);
		if (effect != null) {
			attacker.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 240, Math.min(3, effect.getAmplifier() + 1)));
		}
		else {
			attacker.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 240, 0));
		}

		if (!attacker.world.isRemote()) {
			stack.getOrCreateTag().putLong("LastCriticalHitTime", attacker.world.getGameTime());
		}
	}

	@Override
	public void onDamageEntity(ItemStack stack, LivingEntity attacker, LivingEntity target, float amount) {
		super.onDamageEntity(stack, attacker, target, amount);

		if (!attacker.world.isRemote()) {
			CompoundNBT nbt = stack.getOrCreateTag();
			long elapsedTime = attacker.world.getGameTime() - nbt.getLong("LastCriticalHitTime");
			if (elapsedTime <= 20) {
				nbt.putLong("LastCriticalHitTime", nbt.getLong("LastCriticalHitTime") - 21);
				attacker.heal(amount * 0.1f); // Heal the wielder by 10% of the damage dealt
			}
		}
	}
}
