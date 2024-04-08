package com.github.elenterius.biomancy.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ShieldBlockingListener {
	void onShieldBlocking(ItemStack shield, LivingEntity user, LivingEntity attacker);
}
