package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.mixin.SwordItemMixinAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.Random;

public class ClawWeaponItem extends SwordItem {

	final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiers; //is needed if we want to add forge block reach distance

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

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {}

	public void onDamageEntity(ItemStack stack, LivingEntity attacker, LivingEntity target, float amount) {}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (!state.isIn(Blocks.COBWEB) && !state.isIn(BlockTags.LEAVES)) {
			return state.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(stack, state);
		}
		return 15.0F;
	}

	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity livingEntity, Hand hand) {
		if (livingEntity.world.isRemote()) return ActionResultType.PASS;
		if (livingEntity instanceof IForgeShearable) {
			BlockPos pos = new BlockPos(livingEntity.getPosX(), livingEntity.getPosY(), livingEntity.getPosZ());
			IForgeShearable iShearable = (IForgeShearable) livingEntity;
			if (iShearable.isShearable(stack, livingEntity.world, pos)) {
				List<ItemStack> drops = iShearable.onSheared(playerIn, stack, livingEntity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
				Random rand = new Random();
				drops.forEach(lootStack -> {
					ItemEntity itemEntity = livingEntity.entityDropItem(lootStack, 1.0F);
					if (itemEntity != null) {
						itemEntity.setMotion(itemEntity.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
					}
				});
				stack.damageItem(1, livingEntity, entity -> entity.sendBreakAnimation(hand));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}
