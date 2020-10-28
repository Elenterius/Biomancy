package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModItems
{
    @ObjectHolder("true_sight_goggles")
    public static GogglesArmorItem TRUE_SIGHT_GOGGLES;

    @ObjectHolder("blightbringer_axe")
    public static AxeItem BLIGHTBRINGER_AXE;
}
