package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.mixin.SwordItemMixinAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;

public class ClawsItem extends SimpleSwordItem implements IClaws {

	final Lazy<Multimap<Attribute, AttributeModifier>> defaultAttributeModifiers;

	public ClawsItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
		super(tier, attackDamage, attackSpeed, properties);
		defaultAttributeModifiers = Lazy.of(this::createAttributeModifiers);
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiers() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> swordAttributes = ((SwordItemMixinAccessor) this).biomancy_getDefaultModifiers();
		builder.putAll(swordAttributes);
		addClawAttributeModifiers(builder);
		return builder.build();
	}

	protected void addClawAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		builder.put(ForgeMod.ATTACK_RANGE.get(), CLAWS_ATTACK_RANGE_MODIFIER);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? defaultAttributeModifiers.get() : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return getDestroySpeed(state);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		if (player.level.isClientSide()) return InteractionResult.PASS;
		if (shearTarget(stack, player, interactionTarget, usedHand)) return InteractionResult.SUCCESS;
		return InteractionResult.PASS;
	}

}
