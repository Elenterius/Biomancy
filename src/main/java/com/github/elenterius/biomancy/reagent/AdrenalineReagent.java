package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AdrenalineReagent extends Reagent {

	public static final int DURATION = 20 * (2 * 60 + 30); // 2 min : 30 sec
	public static final int AMPLIFIER = 1;

	public AdrenalineReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		return addStatusEffect(target);
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return addStatusEffect(targetSelf);
	}

	public boolean addStatusEffect(LivingEntity target) {
		if (!target.level.isClientSide) {
			int duration = DURATION;
			EffectInstance effectInstance = target.getEffect(ModEffects.ADRENAL_FATIGUE.get());
			if (effectInstance != null) {
				duration -= Math.min(effectInstance.getDuration() / 2, 20 * 30);
				target.removeEffect(ModEffects.ADRENAL_FATIGUE.get());
			}
			target.addEffect(new EffectInstance(ModEffects.ADRENALINE_RUSH.get(), duration, AMPLIFIER));
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInfoToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getItem() != ModItems.REAGENT.get()) {
			tooltip.add(ClientTextUtil.getTooltipText("contains", new TranslationTextComponent(getTranslationKey())).withStyle(TextFormatting.GRAY));
		}

		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(new TranslationTextComponent(getTranslationKey().replace("reagent", "tooltip")).withStyle(ClientTextUtil.LORE_STYLE));
		}

		addEffectToTooltip(tooltip, ModEffects.ADRENALINE_RUSH.get(), AMPLIFIER, DURATION);
	}

	@OnlyIn(Dist.CLIENT)
	public void addEffectToTooltip(List<ITextComponent> tooltips, Effect effect, int amplifier, int duration) {
		TranslationTextComponent effectText = new TranslationTextComponent(effect.getDescriptionId());
		if (amplifier > 0)
			effectText = new TranslationTextComponent("potion.withAmplifier", effectText, new TranslationTextComponent("potion.potency." + amplifier));
		if (duration > 20) effectText = new TranslationTextComponent("potion.withDuration", effectText, StringUtils.formatTickDuration(duration));
		tooltips.add(effectText.withStyle(effect.getCategory().getTooltipFormatting()));

		Map<Attribute, AttributeModifier> effectModifiers = effect.getAttributeModifiers();
		if (!effectModifiers.isEmpty()) {
			tooltips.add(ClientTextUtil.EMPTY_LINE_HACK());

			for (Map.Entry<Attribute, AttributeModifier> entry : effectModifiers.entrySet()) {
				AttributeModifier modifier = entry.getValue();
				AttributeModifier.Operation operation = modifier.getOperation();
				double value = effect.getAttributeModifierValue(amplifier, modifier);
				double amount = operation != AttributeModifier.Operation.MULTIPLY_BASE && operation != AttributeModifier.Operation.MULTIPLY_TOTAL ? value : value * 100d;

				TranslationTextComponent attributeText = new TranslationTextComponent(entry.getKey().getDescriptionId());
				if (value > 0) {
					tooltips.add((new TranslationTextComponent("attribute.modifier.plus." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount), attributeText)).withStyle(TextFormatting.BLUE));
				}
				else if (value < 0) {
					amount = amount * -1d;
					tooltips.add((new TranslationTextComponent("attribute.modifier.take." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount), attributeText)).withStyle(TextFormatting.RED));
				}
			}
		}
	}

}
