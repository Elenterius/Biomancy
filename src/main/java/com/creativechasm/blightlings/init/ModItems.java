package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModItems
{
    @ObjectHolder("true_sight_goggles")
    public static GogglesArmorItem TRUE_SIGHT_GOGGLES;

    @ObjectHolder("climbing_boots")
    public static ArmorItem CLIMBING_BOOTS;

    @ObjectHolder("jumping_pants")
    public static ArmorItem JUMPING_PANTS;

    @ObjectHolder("blightbringer_axe")
    public static AxeItem BLIGHTBRINGER_AXE;
}
