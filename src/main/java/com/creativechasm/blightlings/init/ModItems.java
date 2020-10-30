package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModItems
{
    @ObjectHolder("true_sight_goggles")
    public static GogglesArmorItem TRUE_SIGHT_GOGGLES;

    @ObjectHolder("blightbringer_axe")
    public static AxeItem BLIGHTBRINGER_AXE;

    @ObjectHolder("blight_shard")
    public static Item BLIGHT_SHARD;

    @ObjectHolder("blight_sac")
    public static Item BLIGHT_SAC;

    @ObjectHolder("blight_goo")
    public static Item BLIGHT_GOO;

    @ObjectHolder("blight_string")
    public static Item BLIGHT_STRING;

    @ObjectHolder("blight_eye")
    public static Item BLIGHT_EYE;
}
