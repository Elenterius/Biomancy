package com.github.elenterius.biomancy.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ShowKnowledgeOverlay {

	boolean canShowKnowledgeOverlay(ItemStack stack, Player player);

}
