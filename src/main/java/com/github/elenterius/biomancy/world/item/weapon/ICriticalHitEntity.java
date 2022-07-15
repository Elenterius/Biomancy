package com.github.elenterius.biomancy.world.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICriticalHitEntity {

	void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target);

}
