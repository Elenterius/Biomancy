package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.util.TooltipUtil;
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

import static net.minecraft.item.ItemStack.DECIMALFORMAT;

public class KhopeshItem extends AxeItem {

	public static AttributeModifier ATTACK_DAMAGE_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("CBD1DE77-3F1D-4E8B-839A-AA471A93D424"), "riding_attack_modifier", 4f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier ATTACK_DIST_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("e488293e-0160-4be3-b7b2-35def9b8ab7e"), "riding_attack_distance_modifier", 1f, AttributeModifier.Operation.ADDITION);

	public KhopeshItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {
		super(tier, attackDamageIn, attackSpeedIn, builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getTooltip(this).setStyle(TooltipUtil.LORE_STYLE));

		if (stack.isEnchanted()) {
			if (TooltipUtil.isToolTipVisible(stack, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
				stack.func_242395_a(ItemStack.TooltipDisplayFlags.ENCHANTMENTS); //hide enchantment tooltip
			}
			tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
			ItemStack.addEnchantmentTooltips(tooltip, stack.getEnchantmentTagList()); //add enchantments before custom modifiers
		}

		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		tooltip.add(BiomancyMod.getTranslationText("tooltip", "riding_bonus").setStyle(Style.EMPTY.applyFormatting(TextFormatting.GRAY)));
		tooltip.add((new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DAMAGE_RIDING_MODIFIER.getOperation().getId(), DECIMALFORMAT.format(ATTACK_DAMAGE_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.name.generic.attack_damage"))).mergeStyle(TextFormatting.BLUE));
		tooltip.add((new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DIST_RIDING_MODIFIER.getOperation().getId(), DECIMALFORMAT.format(ATTACK_DIST_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.generic.attack_distance"))).mergeStyle(TextFormatting.BLUE));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return state.matchesBlock(Blocks.COBWEB) ? 25f : super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return blockIn.matchesBlock(Blocks.COBWEB) || super.canHarvestBlock(blockIn);
	}

	public static void removeSpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
		modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(ModAttributes.getAttackDistance());
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DIST_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DIST_RIDING_MODIFIER);
		}
	}

	public static void applySpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.applyNonPersistentModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
		modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(ModAttributes.getAttackDistance());
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DIST_RIDING_MODIFIER)) {
			modifiableAttributeInstance.applyNonPersistentModifier(ATTACK_DIST_RIDING_MODIFIER);
		}
	}
}
