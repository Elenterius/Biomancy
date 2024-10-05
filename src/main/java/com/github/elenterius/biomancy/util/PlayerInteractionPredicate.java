package com.github.elenterius.biomancy.util;

import net.minecraft.world.entity.player.Player;

public interface PlayerInteractionPredicate {
	boolean canPlayerInteract(Player player);
}
