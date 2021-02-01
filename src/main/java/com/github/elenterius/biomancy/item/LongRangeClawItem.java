package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.util.TooltipUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LongRangeClawItem extends ClawWeaponItem {

	public static AttributeModifier RETRACTED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("d76adb08-2bb3-4e88-997d-766a919f0f6b"), "attack_distance_modifier", 0.5f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier EXTENDED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("29ace568-4e32-4809-840c-3c9a0e1ebcd4"), "attack_distance_modifier", 1.5f, AttributeModifier.Operation.ADDITION);

	private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiersV2;

	private final int abilityDuration; // in "seconds"

	public LongRangeClawItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, int abilityDuration, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
		lazyAttributeModifiersV2 = Lazy.of(this::createAttributeModifiersV2);
		this.abilityDuration = abilityDuration;
	}

	public static boolean isClawExtended(ItemStack stack) {
		return stack.getOrCreateTag().getInt("LongClawTimeLeft") > 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getTooltip(this).setStyle(TooltipUtil.LORE_STYLE));
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());

		int timeLeft = stack.getOrCreateTag().getInt("LongClawTimeLeft");
		if (timeLeft > 0) {
			tooltip.add(new TranslationTextComponent("tooltip.biomancy.item_is_excited").appendString(" (" + timeLeft + ")").mergeStyle(TextFormatting.GRAY));
		}
		else {
			tooltip.add(new TranslationTextComponent("tooltip.biomancy.item_is_dormant").mergeStyle(TextFormatting.GRAY));
		}
		if (stack.isEnchanted()) tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			String key = stack.getOrCreateTag().getInt("LongClawTimeLeft") > 0 ? "tooltip.biomancy.excited" : "tooltip.biomancy.dormant";
			return ((IFormattableTextComponent) displayName).appendString(" (").append(new TranslationTextComponent(key)).appendString(")");
		}
		return displayName;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (isInGroup(group)) {
			ItemStack stack = new ItemStack(this);
			stack.addEnchantment(Enchantments.SWEEPING, 3);
			items.add(stack);
		}
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiersV2() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> clawAttributes = lazyAttributeModifiers.get();
		clawAttributes.forEach((attribute, attributeModifier) -> {
			if (attributeModifier != RETRACTED_CLAW_REACH_MODIFIER) {
				builder.put(attribute, attributeModifier);
			}
		});
		builder.put(ModAttributes.getAttackDistance(), EXTENDED_CLAW_REACH_MODIFIER);
		return builder.build();
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ModAttributes.getAttackDistance(), RETRACTED_CLAW_REACH_MODIFIER);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return slot == EquipmentSlotType.MAINHAND && isClawExtended(stack) ? lazyAttributeModifiersV2.get() : super.getAttributeModifiers(slot, stack);
	}

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		super.onCriticalHitEntity(stack, attacker, target);
		if (!attacker.world.isRemote()) {
			stack.getOrCreateTag().putInt("LongClawTimeLeft", abilityDuration);
		}
		else {
			attacker.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote() && worldIn.getGameTime() % 20L == 0L) {
			CompoundNBT nbt = stack.getOrCreateTag();
			int timeLeft = nbt.getInt("LongClawTimeLeft");
			if (timeLeft > 0) {
				nbt.putInt("LongClawTimeLeft", timeLeft - 1);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
}
