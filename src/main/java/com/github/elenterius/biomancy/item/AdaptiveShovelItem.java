package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class AdaptiveShovelItem extends ShovelItem implements IAdaptiveEfficiencyItem, IAreaHarvestingItem {

	public AdaptiveShovelItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {
		super(tier, attackDamageIn, attackSpeedIn, builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		IAdaptiveEfficiencyItem.addAdaptiveEfficiencyTooltip(stack, tooltip);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float currEfficiency = super.getDestroySpeed(stack, state);
		if (currEfficiency >= efficiency) {
			return Math.min(currEfficiency + IAdaptiveEfficiencyItem.getEfficiencyModifier(stack, state), MAX_EFFICIENCY);
		}
		return currEfficiency;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
			IAdaptiveEfficiencyItem.updateEfficiencyModifier(stack, state, efficiency, super.getDestroySpeed(stack, state));
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, livingEntity);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {

		if (!player.isSneaking() && getBlockHarvestRange(stack) > 0 && !player.world.isRemote && player instanceof ServerPlayerEntity) {
			ServerWorld world = (ServerWorld) player.world;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			BlockState blockState = world.getBlockState(pos);
			BlockRayTraceResult rayTraceResult = Item.rayTrace(world, player, RayTraceContext.FluidMode.NONE);
			if (PlayerInteractionUtil.harvestBlock(world, serverPlayer, blockState, pos)) {
				List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(world, rayTraceResult, blockState, pos, getBlockHarvestRange(stack));
				for (BlockPos neighborPos : blockNeighbors) {
					PlayerInteractionUtil.harvestBlock(world, serverPlayer, blockState, neighborPos);
				}
			}
			return true;
		}

		//only called on client side
		return super.onBlockStartBreak(stack, pos, player);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getFace() == Direction.DOWN) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		BlockPos blockPos = context.getPos();
		BlockState originalState = world.getBlockState(blockPos);

		BlockState modifiedState = originalState.getToolModifiedState(world, blockPos, player, context.getItem(), ToolType.SHOVEL);
		if (modifiedState != null && world.isAirBlock(blockPos.up())) {
			world.playSound(player, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1f, 1f);
			if (!world.isRemote) {
				int durabilityCost = 1;

				if (player != null && !player.isSneaking()) {
					List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(world, context.getFace(), originalState, blockPos, getBlockHarvestRange(context.getItem()));
					for (BlockPos neighborPos : blockNeighbors) {
						BlockState modifiedNeighbor = world.getBlockState(neighborPos).getToolModifiedState(world, neighborPos, player, context.getItem(), ToolType.SHOVEL);
						if (modifiedNeighbor != null && world.isAirBlock(blockPos.up())) {
							world.setBlockState(neighborPos, modifiedNeighbor, Constants.BlockFlags.DEFAULT_AND_RERENDER);
							durabilityCost++;
						}
					}
				}

				world.setBlockState(blockPos, modifiedState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
				if (player != null) context.getItem().damageItem(durabilityCost, player, (playerEntity) -> playerEntity.sendBreakAnimation(context.getHand()));
			}
			return ActionResultType.func_233537_a_(world.isRemote);
		}
		else if (originalState.getBlock() instanceof CampfireBlock && originalState.get(CampfireBlock.LIT)) {
			if (!world.isRemote()) world.playEvent(null, Constants.WorldEvents.FIRE_EXTINGUISH_SOUND, blockPos, 0);
			CampfireBlock.extinguish(world, blockPos, originalState);
			BlockState newState = originalState.with(CampfireBlock.LIT, Boolean.FALSE);
			if (!world.isRemote) {
				world.setBlockState(blockPos, newState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
				if (player != null) context.getItem().damageItem(1, player, (playerEntity) -> playerEntity.sendBreakAnimation(context.getHand()));
			}
			return ActionResultType.func_233537_a_(world.isRemote);
		}

		return ActionResultType.PASS;
	}

	@Override
	public byte getBlockHarvestRange(ItemStack stack) {
		return 1;
	}
}
