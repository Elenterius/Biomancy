package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.mob.ai.goal.FrenzyAttackableTargetGoal;
import com.github.elenterius.biomancy.entity.mob.ai.goal.FrenzyMeleeAttackGoal;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FrenzySerum extends BasicSerum {

	public static final int DEFAULT_DURATION_TICKS = 20 * (2 * 60 + 30); // 2 min : 30 sec
	public static final int MIN_DURATION_TICKS = 20 * 30; // 30 sec
	public static final double ATTACK_DAMAGE_FALLBACK = 1;

	public FrenzySerum(int colorIn) {
		super(colorIn);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Rabbit rabbit && rabbit.getVariant() != Rabbit.Variant.EVIL) {
			rabbit.setVariant(Rabbit.Variant.EVIL);
			return;
		}

		addStatusEffect(target);

		if (target instanceof Mob mob) {
			injectAIBehavior(mob);
		}
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		addStatusEffect(targetSelf);
	}

	private void addStatusEffect(LivingEntity target) {
		int duration = DEFAULT_DURATION_TICKS;

		MobEffectInstance withdrawalEffect = target.getEffect(ModMobEffects.WITHDRAWAL.get());
		if (withdrawalEffect != null) {
			duration -= Math.min(withdrawalEffect.getDuration() / 2, MIN_DURATION_TICKS);
			target.removeEffect(ModMobEffects.WITHDRAWAL.get());
		}

		target.addEffect(new MobEffectInstance(ModMobEffects.FRENZY.get(), duration, 0));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey()).withStyle(TextStyles.LORE));
		}

		addEffectToClientTooltip(tooltip, ModMobEffects.FRENZY.get(), 0, DEFAULT_DURATION_TICKS);
	}

	public void addEffectToClientTooltip(List<Component> tooltips, MobEffect effect, int amplifier, int duration) {
		MutableComponent effectText = ComponentUtil.translatable(effect.getDescriptionId());
		if (amplifier > 0) effectText = ComponentUtil.translatable("potion.withAmplifier", effectText, ComponentUtil.translatable("potion.potency." + amplifier));
		if (duration > 20) effectText = ComponentUtil.translatable("potion.withDuration", effectText, StringUtil.formatTickDuration(duration));
		tooltips.add(effectText.withStyle(effect.getCategory().getTooltipFormatting()));

		Map<Attribute, AttributeModifier> effectModifiers = effect.getAttributeModifiers();
		if (!effectModifiers.isEmpty()) {
			tooltips.add(ComponentUtil.emptyLine());

			for (Map.Entry<Attribute, AttributeModifier> entry : effectModifiers.entrySet()) {
				AttributeModifier modifier = entry.getValue();
				AttributeModifier.Operation operation = modifier.getOperation();
				double value = effect.getAttributeModifierValue(amplifier, modifier);
				double amount = operation != AttributeModifier.Operation.MULTIPLY_BASE && operation != AttributeModifier.Operation.MULTIPLY_TOTAL ? value : value * 100d;

				MutableComponent attributeText = ComponentUtil.translatable(entry.getKey().getDescriptionId());
				if (value > 0) {
					tooltips.add((ComponentUtil.translatable("attribute.modifier.plus." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount), attributeText)).withStyle(ChatFormatting.BLUE));
				}
				else if (value < 0) {
					amount = amount * -1d;
					tooltips.add((ComponentUtil.translatable("attribute.modifier.take." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount), attributeText)).withStyle(ChatFormatting.RED));
				}
			}
		}
	}

	public static void injectAIBehavior(Mob mob) {
		if (!(mob instanceof PathfinderMob) && !(mob instanceof RangedAttackMob)) return;

		if (!hasFrenzyTargetGoal(mob)) {
			mob.targetSelector.addGoal(1, new FrenzyAttackableTargetGoal<>(mob, LivingEntity.class));
		}

		if (!(mob instanceof RangedAttackMob) && !(mob instanceof Enemy) && mob instanceof PathfinderMob pathfinderMob) {
			if (!hasMeleeAttackGoal(mob)) {
				mob.goalSelector.addGoal(4, new FrenzyMeleeAttackGoal(pathfinderMob, 1d, false));
			}
		}
	}

	private static boolean hasMeleeAttackGoal(Mob mob) {
		for (WrappedGoal availableGoal : mob.goalSelector.getAvailableGoals()) {
			if (availableGoal.getGoal() instanceof MeleeAttackGoal) return true;
		}
		return false;
	}

	private static boolean hasFrenzyTargetGoal(Mob mob) {
		for (WrappedGoal availableGoal : mob.targetSelector.getAvailableGoals()) {
			if (availableGoal.getGoal() instanceof FrenzyAttackableTargetGoal) return true;
		}
		return false;
	}

}
