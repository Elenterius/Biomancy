package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.enchantment.BulletJumpEnchantment;
import com.creativechasm.blightlings.enchantment.ClimbingEnchantment;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModEnchantments
{
    @ObjectHolder("climbing")
    public static ClimbingEnchantment CLIMBING;

    @ObjectHolder("bullet_jump")
    public static BulletJumpEnchantment BULLET_JUMP;
}
