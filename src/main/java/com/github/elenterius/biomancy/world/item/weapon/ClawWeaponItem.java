package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.mixin.SwordItemMixinAccessor;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.Random;

public class ClawWeaponItem extends SwordItem implements IBiomancyItem {

	final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiers; //is needed if we want to add forge block reach distance

	public ClawWeaponItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
		super(tier, attackDamage, attackSpeed, properties);
		lazyAttributeModifiers = Lazy.of(this::createAttributeModifiers);
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiers() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> swordAttributes = ((SwordItemMixinAccessor) this).biomancy_getDefaultModifiers();
		swordAttributes.forEach(builder::put);
		addAdditionalAttributeModifiers(builder);
		return builder.build();
	}

	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? lazyAttributeModifiers.get() : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (!state.is(Blocks.COBWEB) && !state.is(BlockTags.LEAVES)) {
			return state.is(BlockTags.WOOL) ? 5f : super.getDestroySpeed(stack, state);
		}
		return 15f;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		if (interactionTarget.level.isClientSide()) return InteractionResult.PASS;
		if (interactionTarget instanceof IForgeShearable iShearable) {
			BlockPos pos = new BlockPos(interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ());
			if (iShearable.isShearable(stack, interactionTarget.level, pos)) {
				List<ItemStack> drops = iShearable.onSheared(player, stack, interactionTarget.level, pos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
				Random rand = player.getRandom();
				drops.forEach(lootStack -> {
					ItemEntity itemEntity = interactionTarget.spawnAtLocation(lootStack, 1f);
					if (itemEntity != null) {
						itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1f, rand.nextFloat() * 0.05f, (rand.nextFloat() - rand.nextFloat()) * 0.1f));
					}
				});
				stack.hurtAndBreak(1, interactionTarget, entity -> entity.broadcastBreakEvent(usedHand));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

}
