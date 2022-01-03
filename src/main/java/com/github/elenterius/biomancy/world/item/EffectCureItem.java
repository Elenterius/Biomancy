package com.github.elenterius.biomancy.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EffectCureItem extends Item {

	public EffectCureItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide) entity.curePotionEffects(stack);
		return entity.eat(level, stack);
	}

}
