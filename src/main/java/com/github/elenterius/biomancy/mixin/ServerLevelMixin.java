package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.util.random.CellularNoise;
import com.github.elenterius.biomancy.util.random.CellularNoiseProvider;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements CellularNoiseProvider {

	@Shadow
	public abstract long getSeed();

	protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
		super(levelData, dimension, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
	}

	@Unique
	private CellularNoise biomancy$cellularNoise = null;

	@Override
	public CellularNoise biomancy$getCellularNoise() {
		if (biomancy$cellularNoise == null) {
			int seed = (int) (getSeed() + 42 + dimension().location().hashCode());
			biomancy$cellularNoise = PrimordialEcosystem.createPreconfiguredCellularNoise(seed);
		}

		return biomancy$cellularNoise;
	}

}
