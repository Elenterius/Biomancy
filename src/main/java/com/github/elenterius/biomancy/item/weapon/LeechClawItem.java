package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LeechClawItem extends ClawWeaponItem {

	public static final AttributeModifier ATTACK_REACH_MODIFIER = new AttributeModifier(ModAttributes.UUIDS.ATTACK_REACH, "Weapon Modifier", -1.5f, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier MAX_HEALTH_MODIFIER = new AttributeModifier(UUID.fromString("00ec2879-aec7-4023-8668-07c940ae8f8a"), "max_health_modifier", -4f, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier MOVEMENT_MODIFIER = new AttributeModifier(UUID.fromString("0823f523-d756-4725-9a6a-098059458c4b"), "movement_modifier", 0.1f, AttributeModifier.Operation.MULTIPLY_BASE);

	public LeechClawItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ModAttributes.getAttackReach(), ATTACK_REACH_MODIFIER);
		builder.put(Attributes.MOVEMENT_SPEED, MOVEMENT_MODIFIER);
		builder.put(Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER);
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		super.onCriticalHitEntity(stack, attacker, target);

		EffectInstance effect = attacker.getEffect(Effects.ABSORPTION);
		if (effect != null) {
			attacker.addEffect(new EffectInstance(Effects.ABSORPTION, 15 * 20, Math.min(4, effect.getAmplifier() + 1)));
		}
		else {
			attacker.addEffect(new EffectInstance(Effects.ABSORPTION, 15 * 20, 0));
		}
	}

	@Override
	public void onDamageEntity(ItemStack stack, LivingEntity attacker, LivingEntity target, float amount) {
		super.onDamageEntity(stack, attacker, target, amount);

		if (!attacker.level.isClientSide()) {
			attacker.heal(amount * 0.1f); // Heal the wielder by 10% of the damage dealt
		}
	}

}
