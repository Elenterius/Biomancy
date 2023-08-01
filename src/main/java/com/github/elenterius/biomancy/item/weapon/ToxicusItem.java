package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.mixin.DamageSourceAccessor;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToxicusItem extends SimpleSwordItem implements CriticalHitEntityAction {

	public ToxicusItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level.isClientSide) return super.hurtEnemy(stack, target, attacker);

		boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
		if (isFullAttackStrength) {
			target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 0));
		}

		DamageSource lastDamageSource = target.getLastDamageSource();
		if (lastDamageSource instanceof EntityDamageSource && lastDamageSource.getEntity() == attacker) {
			((DamageSourceAccessor) lastDamageSource).biomancy$setMsgId(ModDamageSources.CORROSIVE_ACID.msgId);
		}

		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		int seconds = 4;
		target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), seconds * 20, 0));
		target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), seconds * 20, 1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		tooltip.add(ComponentUtil.emptyLine());

		tooltip.add(TextComponentUtil.getTooltipText("ability.shredding_strike").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getTooltipText("ability.shredding_strike.desc")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(TextComponentUtil.getTooltipText("ability.corrosive_proc").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getTooltipText("ability.corrosive_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ComponentUtil.emptyLine());

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}


}
