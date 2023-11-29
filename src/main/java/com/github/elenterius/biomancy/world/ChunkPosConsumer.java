package com.github.elenterius.biomancy.world;

@FunctionalInterface
public interface ChunkPosConsumer {
	void accept(int chunkX, int chunkZ);
}
