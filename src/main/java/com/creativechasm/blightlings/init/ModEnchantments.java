package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModEnchantments
{
    @ObjectHolder("climbing")
    public static ClimbingEnchantment CLIMBING;

    @ObjectHolder("bullet_jump")
    public static BulletJumpEnchantment BULLET_JUMP;
}
