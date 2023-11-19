package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.util.TemperatureUtil;
import com.github.elenterius.biomancy.util.shape.OctantEllipsoidShape;
import com.github.elenterius.biomancy.util.shape.Shape;
import com.github.elenterius.biomancy.util.shape.ShapeMap;
import com.github.elenterius.biomancy.util.shape.SphereShape;
import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoundShape implements Shape {
	BlockPos origin;
	ShapeMap<Shape> solidShapes;
	ShapeMap<MoundChamber> chamberShapes;

	private MoundShape(BlockPos origin, List<Shape> solidShapes, List<MoundChamber> chamberShapes) {
		this.solidShapes = new ShapeMap<>(solidShapes);
		this.chamberShapes = new ShapeMap<>(chamberShapes);
		this.origin = origin;
	}

	public BlockPos getOrigin() {
		return origin;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return solidShapes.contains(x, y, z);
	}

	@Override
	public Vec3 getCenter() {
		return solidShapes.getCenter();
	}

	@Override
	public double distanceToSqr(double x, double y, double z) {
		return solidShapes.distanceToSqr(x, y, z);
	}

	@Override
	public AABB getAABB() {
		return solidShapes.getAABB();
	}

	@Nullable
	public MoundChamber getChamberAt(int x, int y, int z) {
		return chamberShapes.getClosestShapeContaining(x, y, z);
	}

	@Nullable
	public Shape getChamberConfiningShapeAt(int x, int y, int z) {
		return solidShapes.getClosestShapeContaining(x, y, z);
	}

	public final class Generator {
		private Generator() {}

		public static MoundShape constructShape(Level level, BlockPos blockOrigin, long seed) {
			Context ctx = new Context();
			ctx.random = RandomSource.create(seed);

			final int maxBuildHeight = level.getMaxBuildHeight();
			final int seaLevel = level.getSeaLevel();

			Vec3 origin = Vec3.atCenterOf(blockOrigin);

			//TODO: add these to the cradle?
			float heightMultiplier = 0;
			float spireCountModifier = 6;
			float roomSizeModifier = 4; //clamp between 0 and 4? The more spires the smaller the roomSizeModifier?

			Biome biome = level.getBiome(blockOrigin).get();
			float biomeTemperature = TemperatureUtil.getTemperature(biome, blockOrigin);
			float biomeHumidity = biome.getDownfall();

			float heatMultiplier = TemperatureUtil.rescale(biomeTemperature) * 0.5f + biomeTemperature / TemperatureUtil.MAX_TEMP * 0.5f;
			float coldMultiplier = TemperatureUtil.isFreezing(biomeTemperature) ? 0.1f : 1;
			float erosionMultiplier = 0.1f + biomeHumidity * coldMultiplier;
			float erosionMultiplierInv = 1 - erosionMultiplier;

			ctx.spikiness = Mth.clamp(heightMultiplier + heatMultiplier, 0, 1);
			ctx.slantMultiplier = 0.1f + ctx.random.nextFloat() + heatMultiplier * 2f;
			ctx.relativeWallThickness = Mth.clamp((1 - heatMultiplier) * 32, 2.25f, 32);

			ctx.minMoundRadius = 3 + erosionMultiplier * 3;
			ctx.baseMoundRadius = 8 + 4 * erosionMultiplierInv;

			ctx.maxMoundRadius = ctx.baseMoundRadius + Mth.clamp(roomSizeModifier, ctx.minMoundRadius, ctx.baseMoundRadius);
			float subSpireRadius = ctx.maxMoundRadius / 2;
			float extraSpires = Mth.clamp(spireCountModifier, 0, countCirclesOnCircumference(ctx.maxMoundRadius, subSpireRadius));

			float maxMoundHeight = Mth.clamp(maxBuildHeight * ctx.spikiness, 0, (maxBuildHeight - seaLevel));

			ctx.dirLean = new Vec3(ctx.random.nextFloat() - ctx.random.nextFloat(), 0, ctx.random.nextFloat() - ctx.random.nextFloat()).normalize();
			ctx.maxLean = ctx.dirLean.scale(2);
			genSpire(origin.x, origin.y, origin.z, maxMoundHeight, ctx.baseMoundRadius, ctx, true);

			float subRadius = (ctx.baseMoundRadius + ctx.relativeWallThickness) * Mth.sin(Mth.PI / extraSpires);
			float r = Mth.lerp(ctx.spikiness, subSpireRadius, ctx.baseMoundRadius) + ctx.relativeWallThickness;
			float startAngle = ctx.random.nextFloat() * (Mth.PI * 2);
			float angle = (Mth.PI * 2) / extraSpires;

			for (int n = 0; n < extraSpires; n++) {
				float arc = startAngle + angle * n;
				double xn = origin.x + Mth.sin(arc) * r;
				double zn = origin.z + Mth.cos(arc) * r;
				genSpire(xn, origin.y, zn, maxMoundHeight / (1.5f + ctx.random.nextFloat() * 1.5f), subRadius, ctx);
			}

			return new MoundShape(blockOrigin, ctx.solids, ctx.chambers);
		}

		private static void genSpire(double x, double y, double z, float maxHeight, float baseRadius, Context context) {
			genSpire(x, y, z, maxHeight, baseRadius, context, false);
		}

		private static void genSpire(double x, double y, double z, float maxHeight, float baseRadius, Context context, boolean isMainSpire) {

			Vector3d prevLean = new Vector3d(0, 0, 0);
			Vector3d leanOffset = new Vector3d(0, 0, 0);
			float prevRadius = baseRadius + context.relativeWallThickness;
			float totalHeight = 0;

			genSphereWithChambers(x, y, z, prevRadius, prevRadius - context.relativeWallThickness / 2f, context, isMainSpire ? ChamberType.CRADLE : ChamberType.DEFAULT);

			while (totalHeight < maxHeight) {
				float t = totalHeight / maxHeight;

				float coldRadius = Mth.lerp(easeInQuad(t), baseRadius, context.minMoundRadius);
				float warmRadius = Mth.lerp(easeOutQuad(t), baseRadius, context.minMoundRadius);
				float radius = Mth.clamp(Mth.lerp(context.spikiness, coldRadius, warmRadius), context.minMoundRadius, context.maxMoundRadius) + context.relativeWallThickness;
				float height = totalHeight + radius / 2 + Mth.lerp(t, 0, radius / 2.5f);

				if (height >= maxHeight) break;

				leanOffset.set(context.dirLean.x, context.dirLean.y, context.dirLean.z);
				leanOffset.scale((context.random.nextFloat() - 1) * context.slantMultiplier);
				double leanX = prevLean.x + leanOffset.x;
				if (Math.abs(leanX) >= context.maxLean.x) {
					leanX = prevLean.x - leanOffset.z;
				}
				double leanZ = prevLean.z + leanOffset.z;
				if (Math.abs(leanZ) >= context.maxLean.z) {
					leanZ = prevLean.z - leanOffset.z;
				}

				genSphereWithChambers(x + leanX, y + height, z + leanZ, radius, radius - context.relativeWallThickness / 2f, context, ChamberType.DEFAULT);

				prevLean.set(leanX, 0, leanZ);
				prevRadius = radius;
				totalHeight = height;
			}

			//end cap shape
			ChamberType chamberType = isMainSpire ? ChamberType.END_CAP_MAIN_SPIRE : ChamberType.END_CAP_SUB_SPIRE;
			genSphereWithChambers(x + prevLean.x, y + totalHeight + (prevRadius / 2) * 1.5f, z + prevLean.z, prevRadius / 2f, prevRadius / 2f, context, chamberType);
		}

		private static void genSphereWithChambers(double x, double y, double z, float radius, float chamberRadius, Context context, ChamberType type) {
			Vec3 pos = new Vec3(x, y, z);
			context.solids.add(new SphereShape(pos, radius));

			if (chamberRadius < 8) {
				context.chambers.add(new MoundChamber(new SphereShape(pos, chamberRadius)));
				return;
			}

			switch (type) {
				case DEFAULT -> RANDOM_DEFAULT_CHAMBER_GENERATORS.getRandomValue(context.random).orElse(EllipsoidChambers8).generate(x, y, z, chamberRadius, context);
				case CRADLE -> EllipsoidChambers1big4small.generate(x, y - 1, z, chamberRadius, context);
				case END_CAP_MAIN_SPIRE -> {
					ChambersGenerator generator = context.random.nextFloat() < 0.8f ? SphereChamber : EllipsoidChambers1big4small;
					generator.generate(x, y, z, chamberRadius, context);
				}
				case END_CAP_SUB_SPIRE -> {
					ChambersGenerator generator = context.random.nextFloat() < 0.8f ? EllipsoidChambers1big4small : SphereChamber;
					generator.generate(x, y, z, chamberRadius, context);
				}
			}
		}

		private static final ChambersGenerator SphereChamber = (double x, double y, double z, float chamberRadius, Context context) -> {
			context.chambers.add(new MoundChamber(new SphereShape(x, y, z, chamberRadius)));
		};

		private static final ChambersGenerator EllipsoidChambers8 = (double x, double y, double z, float chamberRadius, Context context) -> {
			float halfR = chamberRadius / 2;
			float quarterR = halfR / 2;
			float p = chamberRadius / 3.8f; // radius / 4.25f

			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z + p, halfR, halfR, halfR, quarterR, quarterR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z + p, quarterR, halfR, halfR, halfR, quarterR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z - p, halfR, halfR, quarterR, quarterR, quarterR, halfR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z - p, quarterR, halfR, quarterR, halfR, quarterR, halfR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
		};

		private static final ChambersGenerator EllipsoidChambers1big4small = (double x, double y, double z, float chamberRadius, Context context) -> {
			float halfR = chamberRadius / 2;
			float quarterR = halfR / 2;
			float p = chamberRadius / 3.8f; // radius / 4.25f

			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x, y + p, z, halfR, halfR, halfR, halfR, quarterR, halfR)));

			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
		};

		private static final ChambersGenerator EllipsoidChambers4small1big = (double x, double y, double z, float chamberRadius, Context context) -> {
			float halfR = chamberRadius / 2;
			float quarterR = halfR / 2;
			float p = chamberRadius / 3.8f; // radius / 4.25f

			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z + p, halfR, halfR, halfR, quarterR, quarterR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z + p, quarterR, halfR, halfR, halfR, quarterR, quarterR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z - p, halfR, halfR, quarterR, quarterR, quarterR, halfR)));
			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z - p, quarterR, halfR, quarterR, halfR, quarterR, halfR)));

			context.chambers.add(new MoundChamber(new OctantEllipsoidShape(x, y - p, z, halfR, quarterR, halfR, halfR, halfR, halfR)));
		};

		private static final SimpleWeightedRandomList<ChambersGenerator> RANDOM_DEFAULT_CHAMBER_GENERATORS = SimpleWeightedRandomList.<ChambersGenerator>builder()
				.add(SphereChamber, 5)
				.add(EllipsoidChambers1big4small, 20)
				.add(EllipsoidChambers4small1big, 20)
				.add(EllipsoidChambers8, 60)
				.build();

		private interface ChambersGenerator {
			void generate(double x, double y, double z, float chamberRadius, Context context);
		}

		private static float countCirclesOnCircumference(float radius, float subCircleRadius) {
			//https://stackoverflow.com/questions/56004326/calculate-the-number-of-circles-that-fit-on-the-circumference-of-another-circle
			if (subCircleRadius > radius) return 0;
			return Mth.PI / (float) Math.asin(subCircleRadius / radius);
		}

		private static float easeInQuad(float x) {
			return x * x;
		}

		private static float easeOutQuad(float x) {
			return 1f - (1f - x) * (1f - x);
		}

		private static class Context {
			List<Shape> solids = new ArrayList<>();
			List<MoundChamber> chambers = new ArrayList<>();
			RandomSource random;
			float spikiness;
			float slantMultiplier;
			float minMoundRadius;
			float baseMoundRadius;
			float maxMoundRadius;
			float relativeWallThickness;
			Vec3 dirLean;
			Vec3 maxLean;
		}

		private enum ChamberType {
			DEFAULT, CRADLE, END_CAP_MAIN_SPIRE, END_CAP_SUB_SPIRE
		}
	}
}


