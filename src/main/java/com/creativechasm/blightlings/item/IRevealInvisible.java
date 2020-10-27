package com.creativechasm.blightlings.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public interface IRevealInvisible<T extends ArmorItem>
{
    boolean canRevealInvisibleEntity(ItemStack stack, PlayerEntity player, Entity invisibleEntity);
}
