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

public class HangingCombsDecorator implements ChamberDecorator {

	private final FastNoiseLite simplexNoise;
	private final BlockState material;

	public HangingCombsDecorator(BlockState material) {
		this.material = material;
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
		return PartOfDecorationResult.of(isPosInsideAnyComb(chamber, pos), state == material);
	}

	@Override
	public boolean canPlace(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
		return isPosInsideAnyComb(chamber, pos);
	}

	@Override
	public boolean place(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
		return level.setBlock(pos, material, Block.UPDATE_CLIENTS);
	}

	protected float combThreshold(float y) {
		return 0.5f + easeInExpo(1 - y) * 10;
	}

	protected boolean isPosInsideAnyComb(Chamber chamber, BlockPos pos) {
		Vec3 center = chamber.center();
		float x = (float) (center.x - (pos.getX() + 0.5d));
		float z = (float) (center.z - (pos.getZ() + 0.5d));

		AABB aabb = chamber.getAABB();
		float yNormalized = normalize(pos.getY(), aabb.minY, aabb.maxY);
		float threshold = combThreshold(yNormalized);

		simplexNoise.SetSeed(chamber.seed());

		return simplexNoise.GetNoise(x, 0, z) >= threshold;
	}

	private static float normalize(double value, double min, double max) {
		float rescaled = (float) ((value - min) / (max - min)); //min-max rescale
		return Mth.clamp(rescaled, 0f, 1f);
	}

	private static float easeInExpo(float x) {
		if (x == 0) return 0;
		return (float) Math.pow(2, 10 * x - 10);
	}
}
