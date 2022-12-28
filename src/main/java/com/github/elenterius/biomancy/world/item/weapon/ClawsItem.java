package com.github.elenterius.biomancy.world.item.weapon;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ClawsItem extends TieredItem implements Vanishable {

	protected static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("d76adb08-2bb3-4e88-997d-766a919f0f6b");
	protected static final Set<Material> MINEABLE_WITH_CLAWS = Set.of(Material.PLANT, Material.REPLACEABLE_PLANT, Material.VEGETABLE);
	protected final Lazy<Multimap<Attribute, AttributeModifier>> defaultAttributeModifiers;

	public ClawsItem(Tier tier, int baseAttackDamage, float attackSpeedModifier, float attackRangeModifier, Properties properties) {
		super(tier, properties);
		float attackDamageModifier = baseAttackDamage + tier.getAttackDamageBonus();
		defaultAttributeModifiers = Lazy.of(() -> createDefaultAttributeModifiers(attackDamageModifier, attackSpeedModifier, attackRangeModifier));
	}

	protected Multimap<Attribute, AttributeModifier> createDefaultAttributeModifiers(float attackDamageModifier, float attackSpeedModifier, float attackRangeModifier) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamageModifier, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeedModifier, AttributeModifier.Operation.ADDITION));
		builder.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(BASE_ATTACK_RANGE_UUID, "Weapon modifier", attackRangeModifier, AttributeModifier.Operation.ADDITION));
		return builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? defaultAttributeModifiers.get() : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction); //use sword actions
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.category.canEnchant(stack.getItem()) || enchantment.category == EnchantmentCategory.WEAPON; //use sword enchantments
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return getDestroySpeed(state);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (state.getDestroySpeed(level, pos) != 0f) {
			stack.hurtAndBreak(2, miningEntity, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState block) {
		return block.is(Blocks.COBWEB) || block.is(BlockTags.LEAVES);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		if (player.level.isClientSide()) return InteractionResult.PASS;
		if (shearInteractionTarget(stack, player, interactionTarget, usedHand)) return InteractionResult.SUCCESS;
		return InteractionResult.PASS;
	}

	protected float getDestroySpeed(BlockState state) {
		if (state.is(Blocks.COBWEB)) return 15f;
		if (state.is(BlockTags.LEAVES)) return 15f;
		if (state.is(BlockTags.WOOL)) return 5f;

		Material material = state.getMaterial();
		return MINEABLE_WITH_CLAWS.contains(material) ? 1.5F : 1f;
	}

	protected boolean shearInteractionTarget(ItemStack stack, Player player, LivingEntity targetEntity, InteractionHand usedHand) {
		if (targetEntity instanceof IForgeShearable shearingTarget) {
			BlockPos pos = targetEntity.blockPosition();

			if (shearingTarget.isShearable(stack, targetEntity.level, pos)) {
				List<ItemStack> drops = shearingTarget.onSheared(player, stack, targetEntity.level, pos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
				Random rand = player.getRandom();
				drops.forEach(lootStack -> {
					ItemEntity itemEntity = targetEntity.spawnAtLocation(lootStack, 1f);
					if (itemEntity != null) {
						itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1f, rand.nextFloat() * 0.05f, (rand.nextFloat() - rand.nextFloat()) * 0.1f));
					}
				});
				stack.hurtAndBreak(1, targetEntity, entity -> entity.broadcastBreakEvent(usedHand));
			}
			return true;
		}

		return false;
	}

}
