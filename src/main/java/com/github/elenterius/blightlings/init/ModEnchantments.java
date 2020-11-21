package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.enchantment.BulletJumpEnchantment;
import com.github.elenterius.blightlings.enchantment.ClimbingEnchantment;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public abstract class ModEnchantments
{
    @ObjectHolder("climbing")
    public static ClimbingEnchantment CLIMBING;

    @ObjectHolder("bullet_jump")
    public static BulletJumpEnchantment BULLET_JUMP;
}
