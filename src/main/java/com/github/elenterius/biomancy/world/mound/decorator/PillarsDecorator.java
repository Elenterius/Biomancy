package com.github.elenterius.biomancy.world.mound.decorator;

import com.github.elenterius.biomancy.util.random.FastNoiseLite;
import com.github.elenterius.biomancy.world.mound.Chamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PillarsDecorator implements ChamberDecorator {

	private final FastNoiseLite simplexNoise;
	private final BlockState[] materials;
	private final Set<BlockState> materialsSet;

	public PillarsDecorator(BlockState... materials) {
		this.materials = materials;
		this.materialsSet = Arrays.stream(materials).collect(Collectors.toUnmodifiableSet());
		this.simplexNoise = initNoise();
	}

	protected FastNoiseLite initNoise() {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
		noise.SetFrequency(0.1f);
		return noise;
	}

	@Override
	public PartOfDecorationResult isBlockPartOfDecoration(Chamber chamber, Level level, BlockPos pos, BlockState state) {
		return PartOfDecorationResult.of(isPosInsideAnyPillar(chamber, pos), materialsSet.contains(state));
	}

	@Override
	public boolean canPlace(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
		return isPosInsideAnyPillar(chamber, pos);
	}

	@Override
	public boolean place(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
		int i = level.getRandom().nextInt(0, materials.length);
		return level.setBlock(pos, materials[i], Block.UPDATE_CLIENTS);
	}

	protected float pillarThreshold(float y) {
		float fx = (((y - 0.5f) * (y - 0.5f)) * 4f) * -1f + 1f;
		return 0.42f + fx * 0.2f;
	}

	protected boolean isPosInsideAnyPillar(Chamber chamber, BlockPos pos) {
		Vec3 center = chamber.center();
		float x = (float) (center.x - (pos.getX() + 0.5d));
		float z = (float) (center.z - (pos.getZ() + 0.5d));

		AABB aabb = chamber.getAABB();
		float yNormalized = normalize(pos.getY(), aabb.minY, aabb.maxY);
		float threshold = pillarThreshold(yNormalized);

		simplexNoise.SetSeed(chamber.seed());

		return simplexNoise.GetNoise(x, 0, z) >= threshold;
	}

	private static float normalize(double value, double min, double max) {
		float rescaled = (float) ((value - min) / (max - min)); //min-max rescale
		return Mth.clamp(rescaled, 0f, 1f);
	}
}
