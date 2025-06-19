package com.github.elenterius.biomancy.entity.misc;

public interface BiomancyPlayer {

	/**
	 * This data is transient and not synced between server and client
	 */
	boolean biomancy$isEyeInsideMembrane();

	/**
	 * This data is transient and not synced between server and client
	 */
	void biomancy$setIsInsideMembrane(boolean isInside);

}
