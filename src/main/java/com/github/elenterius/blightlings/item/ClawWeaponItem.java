package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.mixin.SwordItemMixinAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.util.Lazy;

public class ClawWeaponItem extends SwordItem {

	final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiers;

	public ClawWeaponItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
		lazyAttributeModifiers = Lazy.of(this::createAttributeModifiers);
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiers() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> swordAttributes = ((SwordItemMixinAccessor) this).getAttributeModifiers();
		swordAttributes.forEach(builder::put);
		addAdditionalAttributeModifiers(builder);
		return builder.build();
	}

	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return equipmentSlot == EquipmentSlotType.MAINHAND ? lazyAttributeModifiers.get() : super.getAttributeModifiers(equipmentSlot);
	}

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {

	}

	public void onDamageEntity(ItemStack stack, LivingEntity attacker, LivingEntity target, float amount) {

	}
}
