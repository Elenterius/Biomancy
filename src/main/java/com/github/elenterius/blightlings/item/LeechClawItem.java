package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.init.ModAttributes;
import com.github.elenterius.blightlings.util.TooltipUtil;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

	public static AttributeModifier ATTACK_DISTANCE_MODIFIER = new AttributeModifier(ModAttributes.UUIDS.ATTACK_DISTANCE, "attack_distance_modifier", -0.55f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier MOVEMENT_MODIFIER = new AttributeModifier(UUID.fromString("0823f523-d756-4725-9a6a-098059458c4b"), "movement_modifier", 0.1f, AttributeModifier.Operation.MULTIPLY_BASE);

	public LeechClawItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getTooltip(this).setStyle(TooltipUtil.LORE_STYLE));
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ModAttributes.getAttackDistance(), ATTACK_DISTANCE_MODIFIER);
		builder.put(Attributes.MOVEMENT_SPEED, MOVEMENT_MODIFIER);
	}

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		super.onCriticalHitEntity(stack, attacker, target);

		EffectInstance effect = attacker.getActivePotionEffect(Effects.ABSORPTION);
		if (effect != null) {
			attacker.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 15 * 20, Math.min(4, effect.getAmplifier() + 1)));
		}
		else {
			attacker.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 15 * 20, 0));
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
