package com.github.elenterius.biomancy.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CriticalHitListener {

	void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target);

}
