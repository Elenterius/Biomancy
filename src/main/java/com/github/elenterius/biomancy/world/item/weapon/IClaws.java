package com.github.elenterius.biomancy.world.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.IForgeShearable;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public interface IClaws {

	AttributeModifier CLAWS_ATTACK_RANGE_MODIFIER = new AttributeModifier(UUID.fromString("d76adb08-2bb3-4e88-997d-766a919f0f6b"), "Weapon modifier", 0.5f, AttributeModifier.Operation.ADDITION);

	Set<Material> MINEABLE_WITH_CLAWS = Set.of(Material.PLANT, Material.REPLACEABLE_PLANT, Material.VEGETABLE);

	default float getDestroySpeed(BlockState state) {
		if (state.is(Blocks.COBWEB)) return 15f;
		if (state.is(BlockTags.LEAVES)) return 15f;
		if (state.is(BlockTags.WOOL)) return 5f;

		Material material = state.getMaterial();
		return MINEABLE_WITH_CLAWS.contains(material) ? 1.5F : 1f;
	}

	default boolean shearTarget(ItemStack stack, Player player, LivingEntity targetEntity, InteractionHand usedHand) {
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
