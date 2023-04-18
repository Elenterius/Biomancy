package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.item.ICustomTooltip;
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
		tooltip.add(ComponentUtil.horizontalLine());
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));
	}

}
