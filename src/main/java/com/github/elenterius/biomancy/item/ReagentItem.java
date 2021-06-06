package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModReagents;
import com.github.elenterius.biomancy.reagent.Reagent;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentItem extends Item {

	public ReagentItem(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			reagent.addInfoToTooltip(stack, worldIn, tooltip, flagIn);
		}
		else tooltip.add(TextUtil.getTranslationText("tooltip", "contains_nothing"));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (isInGroup(group)) {
			for (Reagent reagent : ModReagents.REGISTRY.get()) {
				items.add(ReagentItem.getReagentItemStack(reagent));
			}
		}
	}

	public static ItemStack getReagentItemStack(Reagent reagent) {
		ItemStack stack = new ItemStack(ModItems.REAGENT.get());
		Reagent.serialize(reagent, stack.getOrCreateTag());
		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			return reagent.getTranslationKey();
		}
		return super.getTranslationKey(stack);
	}

	public int getReagentColor(ItemStack stack) {
		return Reagent.getColor(stack.getOrCreateTag());
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
