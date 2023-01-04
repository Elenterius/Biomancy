package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AdrenalineSerum extends Serum {

	public static final int DURATION = 20 * (2 * 60 + 30); // 2 min : 30 sec
	public static final int AMPLIFIER = 1;

	public AdrenalineSerum(int colorIn) {
		super(colorIn);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		addStatusEffect(target);
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		addStatusEffect(targetSelf);
	}

	private void addStatusEffect(LivingEntity target) {
		int duration = DURATION;
		MobEffectInstance effectInstance = target.getEffect(ModMobEffects.ADRENAL_FATIGUE.get());
		if (effectInstance != null) {
			duration -= Math.min(effectInstance.getDuration() / 2, 20 * 30);
			target.removeEffect(ModMobEffects.ADRENAL_FATIGUE.get());
		}
		target.addEffect(new MobEffectInstance(ModMobEffects.ADRENALINE_RUSH.get(), duration, AMPLIFIER));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(ComponentUtil.translatable(getTooltipKey()).withStyle(TextStyles.LORE));
		}

		addEffectToClientTooltip(tooltip, ModMobEffects.ADRENALINE_RUSH.get(), AMPLIFIER, DURATION);
	}

	public void addEffectToClientTooltip(List<Component> tooltips, MobEffect effect, int amplifier, int duration) {
		MutableComponent effectText = ComponentUtil.translatable(effect.getDescriptionId());
		if (amplifier > 0)
			effectText = ComponentUtil.translatable("potion.withAmplifier", effectText, ComponentUtil.translatable("potion.potency." + amplifier));
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

}
