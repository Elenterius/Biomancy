package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.TooltipUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AdaptiveAxeItem extends AxeItem implements IAdaptiveEfficiencyItem {

	public AdaptiveAxeItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {
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

}
