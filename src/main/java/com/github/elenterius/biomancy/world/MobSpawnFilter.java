package com.github.elenterius.biomancy.world;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

import java.util.Set;

public interface MobSpawnFilter {

	Set<MobSpawnType> NATURAL_SPAWN_REASON = Set.of(
			MobSpawnType.EVENT,
			MobSpawnType.NATURAL,
			MobSpawnType.CHUNK_GENERATION,
			MobSpawnType.PATROL
	);

	static boolean isNaturalSpawn(MobSpawnType spawnReason) {
		return NATURAL_SPAWN_REASON.contains(spawnReason);
	}

	/**
	 * At the moment only checks natural mob spawns
	 */
	boolean isMobAllowedToSpawn(Mob mob, MobSpawnType spawnReason, LevelAccessor level, double x, double y, double z);

}
