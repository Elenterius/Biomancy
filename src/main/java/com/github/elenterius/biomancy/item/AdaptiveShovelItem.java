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
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		IAdaptiveEfficiencyItem.addAdaptiveEfficiencyTooltip(stack, tooltip);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float currEfficiency = super.getDestroySpeed(stack, state);
		if (currEfficiency >= speed) {
			return Math.min(currEfficiency + IAdaptiveEfficiencyItem.getEfficiencyModifier(stack, state), MAX_EFFICIENCY);
		}
		return currEfficiency;
	}

	@Override
	public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		if (!worldIn.isClientSide && state.getDestroySpeed(worldIn, pos) != 0.0F) {
			IAdaptiveEfficiencyItem.updateEfficiencyModifier(stack, state, speed, super.getDestroySpeed(stack, state));
		}
		return super.mineBlock(stack, worldIn, state, pos, livingEntity);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {

		if (!player.isShiftKeyDown() && getBlockHarvestRange(stack) > 0 && !player.level.isClientSide && player instanceof ServerPlayerEntity) {
			ServerWorld world = (ServerWorld) player.level;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			BlockState blockState = world.getBlockState(pos);
			BlockRayTraceResult rayTraceResult = Item.getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
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
	public ActionResultType useOn(ItemUseContext context) {
		if (context.getClickedFace() == Direction.DOWN) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState originalState = world.getBlockState(blockPos);

		BlockState modifiedState = originalState.getToolModifiedState(world, blockPos, player, context.getItemInHand(), ToolType.SHOVEL);
		if (modifiedState != null && world.isEmptyBlock(blockPos.above())) {
			world.playSound(player, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1f, 1f);
			if (!world.isClientSide) {
				int durabilityCost = 1;

				if (player != null && !player.isShiftKeyDown()) {
					List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(world, context.getClickedFace(), originalState, blockPos, getBlockHarvestRange(context.getItemInHand()));
					for (BlockPos neighborPos : blockNeighbors) {
						BlockState modifiedNeighbor = world.getBlockState(neighborPos).getToolModifiedState(world, neighborPos, player, context.getItemInHand(), ToolType.SHOVEL);
						if (modifiedNeighbor != null && world.isEmptyBlock(blockPos.above())) {
							world.setBlock(neighborPos, modifiedNeighbor, Constants.BlockFlags.DEFAULT_AND_RERENDER);
							durabilityCost++;
						}
					}
				}

				world.setBlock(blockPos, modifiedState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
				if (player != null)
					context.getItemInHand().hurtAndBreak(durabilityCost, player, (playerEntity) -> playerEntity.broadcastBreakEvent(context.getHand()));
			}
			return ActionResultType.sidedSuccess(world.isClientSide);
		}
		else if (originalState.getBlock() instanceof CampfireBlock && originalState.getValue(CampfireBlock.LIT)) {
			if (!world.isClientSide()) world.levelEvent(null, Constants.WorldEvents.FIRE_EXTINGUISH_SOUND, blockPos, 0);
			CampfireBlock.dowse(world, blockPos, originalState);
			BlockState newState = originalState.setValue(CampfireBlock.LIT, Boolean.FALSE);
			if (!world.isClientSide) {
				world.setBlock(blockPos, newState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
				if (player != null) context.getItemInHand().hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(context.getHand()));
			}
			return ActionResultType.sidedSuccess(world.isClientSide);
		}

		return ActionResultType.PASS;
	}

	@Override
	public byte getBlockHarvestRange(ItemStack stack) {
		return 1;
	}
}
