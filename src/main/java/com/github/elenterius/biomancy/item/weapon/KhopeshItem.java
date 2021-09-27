package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class KhopeshItem extends AxeItem {

	public static AttributeModifier ATTACK_DAMAGE_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("CBD1DE77-3F1D-4E8B-839A-AA471A93D424"), "riding_attack_modifier", 4f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier ATTACK_DIST_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("e488293e-0160-4be3-b7b2-35def9b8ab7e"), "riding_attack_distance_modifier", 1f, AttributeModifier.Operation.ADDITION);

	public KhopeshItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {
		super(tier, attackDamageIn, attackSpeedIn, builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));

		if (stack.isEnchanted()) {
			if (ClientTextUtil.isToolTipVisible(stack, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
				stack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ENCHANTMENTS); //hide enchantment tooltip
			}
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			ItemStack.appendEnchantmentNames(tooltip, stack.getEnchantmentTags()); //add enchantments before custom modifiers
		}

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(TextUtil.getTranslationText("tooltip", "riding_bonus").setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
		tooltip.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DAMAGE_RIDING_MODIFIER.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(ATTACK_DAMAGE_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.name.generic.attack_damage"))).withStyle(TextFormatting.BLUE));
		tooltip.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DIST_RIDING_MODIFIER.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(ATTACK_DIST_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.generic.attack_distance"))).withStyle(TextFormatting.BLUE));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return state.is(Blocks.COBWEB) ? 25f : super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState blockIn) {
		return blockIn.is(Blocks.COBWEB) || super.isCorrectToolForDrops(blockIn);
	}

	public static void removeSpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
		modifiableAttributeInstance = livingEntity.getAttributes().getInstance(ModAttributes.getAttackDistanceModifier());
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DIST_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DIST_RIDING_MODIFIER);
		}
	}

	public static void applySpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.addTransientModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
		modifiableAttributeInstance = livingEntity.getAttributes().getInstance(ModAttributes.getAttackDistanceModifier());
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DIST_RIDING_MODIFIER)) {
			modifiableAttributeInstance.addTransientModifier(ATTACK_DIST_RIDING_MODIFIER);
		}
	}
}
