package com.github.elenterius.biomancy.entity.fleshblob;

import net.minecraft.world.entity.SpawnGroupData;

public interface IFleshBlobSpawnData extends SpawnGroupData {

	byte tumorFlags();

	record Tumors(byte tumorFlags) implements IFleshBlobSpawnData {}

}
