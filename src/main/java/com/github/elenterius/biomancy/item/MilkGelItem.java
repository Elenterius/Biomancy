package com.github.elenterius.biomancy.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MilkGelItem extends Item {

	public MilkGelItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity entity) {
		if (!level.isClientSide) entity.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
		return entity.eat(level, stack);
	}

}
