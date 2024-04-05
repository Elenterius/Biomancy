package com.github.elenterius.biomancy.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemAttackDamageSourceProvider {
	@Nullable DamageSource getDamageSource(ItemStack stack, Entity target, LivingEntity attacker);
}
