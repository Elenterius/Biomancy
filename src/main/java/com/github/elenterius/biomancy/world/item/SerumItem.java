package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SerumItem extends Item {

	public SerumItem(Properties properties) {
		super(properties);
	}

	public static ItemStack getReagentItemStack(Serum reagent) {
		ItemStack stack = new ItemStack(ModItems.SERUM.get());
		Serum.serialize(reagent, stack.getOrCreateTag());
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		Serum reagent = Serum.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			reagent.addInfoToTooltip(stack, level, tooltip, isAdvanced);
		}
		else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing"));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (allowdedIn(group)) {
			for (Serum reagent : ModSerums.REGISTRY.get()) {
				items.add(SerumItem.getReagentItemStack(reagent));
			}
		}
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		Serum reagent = Serum.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			return reagent.getTranslationKey();
		}
		return super.getDescriptionId(stack);
	}

	public int getSerumColor(ItemStack stack) {
		return Serum.getColor(stack.getOrCreateTag());
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stackIn) {
		return new ItemStack(ModItems.GLASS_VIAL.get());
	}

}
