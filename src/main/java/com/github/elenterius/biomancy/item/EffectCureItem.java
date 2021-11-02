package com.github.elenterius.biomancy.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EffectCureItem extends Item {

	public EffectCureItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity entity) {
		if (!level.isClientSide) entity.curePotionEffects(stack);
		return entity.eat(level, stack);
	}

}
