package com.github.elenterius.biomancy.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface SweepAttackListener {

	/**
	 * return true if you want to cancel the sweep vfx particles
	 */
	boolean onSweepAttack(Level level, Player attacker);

}
