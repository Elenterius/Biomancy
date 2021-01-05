package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.init.ModAttributes;
import com.github.elenterius.blightlings.mixin.SwordItemMixinAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.util.Lazy;

public class PoleWeaponItem extends SwordItem {

	public static AttributeModifier ATTACK_DISTANCE_MODIFIER = new AttributeModifier(ModAttributes.UUIDS.ATTACK_DISTANCE, "attack_distance_modifier", 1.8f, AttributeModifier.Operation.ADDITION);
	public static AttributeModifier BLOCK_REACH_DISTANCE_MODIFIER = new AttributeModifier(ModAttributes.UUIDS.BLOCK_REACH_DISTANCE, "Tool modifier", 2f, AttributeModifier.Operation.ADDITION);

	final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiers; //is needed if we want to add forge block reach distance

	public PoleWeaponItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
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

	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		builder.put(ModAttributes.getBlockReachDistance(), BLOCK_REACH_DISTANCE_MODIFIER);
		builder.put(ModAttributes.getAttackDistance(), ATTACK_DISTANCE_MODIFIER);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return equipmentSlot == EquipmentSlotType.MAINHAND ? lazyAttributeModifiers.get() : super.getAttributeModifiers(equipmentSlot);
	}
}
