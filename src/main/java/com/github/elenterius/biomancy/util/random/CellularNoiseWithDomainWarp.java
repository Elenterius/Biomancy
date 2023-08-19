package com.github.elenterius.biomancy.util.random;

public record CellularNoiseWithDomainWarp(FastNoiseLite cellularNoise, FastNoiseLite domainWarp) implements CellularNoise {
	@Override
	public float getValue(float x, float y) {
		FastNoiseLite.Vector2 vec = new FastNoiseLite.Vector2(x, y);
		domainWarp.DomainWarp(vec);
		return cellularNoise.GetNoise(vec.x, vec.y) + 1;
	}

	@Override
	public float getValue(float x, float y, float z) {
		FastNoiseLite.Vector3 vec = new FastNoiseLite.Vector3(x, y, z);
		domainWarp.DomainWarp(vec);
		return cellularNoise.GetNoise(vec.x, vec.y, vec.z) + 1;
	}
}
