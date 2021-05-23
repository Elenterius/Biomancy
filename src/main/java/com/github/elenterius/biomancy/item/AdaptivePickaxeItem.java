package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import com.github.elenterius.biomancy.util.TooltipUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AdaptivePickaxeItem extends PickaxeItem implements IAdaptiveEfficiencyItem, IAreaHarvestingItem {

	public AdaptivePickaxeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
		super(tier, attackDamageIn, attackSpeedIn, builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getTooltip(this).setStyle(TooltipUtil.LORE_STYLE));
		IAdaptiveEfficiencyItem.addAdaptiveEfficiencyTooltip(stack, tooltip);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float currEfficiency = super.getDestroySpeed(stack, state);
		if (currEfficiency >= this.efficiency) {
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
	public byte getBlockHarvestRange(ItemStack stack) {
		return 1;
	}
}
