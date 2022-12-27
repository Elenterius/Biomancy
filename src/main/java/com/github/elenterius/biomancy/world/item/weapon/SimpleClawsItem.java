package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleClawsItem extends ClawsItem implements ICustomTooltip {

	public SimpleClawsItem(Tier tier, int baseAttackDamage, float attackSpeedModifier, float attackRangeModifier, Properties properties) {
		super(tier, baseAttackDamage, attackSpeedModifier, attackRangeModifier, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(TooltipHacks.HR_COMPONENT);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
	}

}
