package com.github.elenterius.biomancy.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CriticalHitEntityAction {

	void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target);

}
