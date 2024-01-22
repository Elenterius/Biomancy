package com.github.elenterius.biomancy.entity.mob.fleshblob;

import net.minecraft.world.entity.SpawnGroupData;

public interface FleshBlobSpawnData extends SpawnGroupData {

	byte tumorFlags();

	record Tumors(byte tumorFlags) implements FleshBlobSpawnData {}

}
