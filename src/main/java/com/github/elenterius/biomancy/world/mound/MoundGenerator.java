package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.TemperatureUtil;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.github.elenterius.biomancy.world.spatial.geometry.SphereShape;
import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class MoundGenerator {
	private MoundGenerator() {}

	public static MoundShape constructShape(BlockPos blockOrigin, MoundShape.ProcGenValues procGenValues) {
		return genShape(blockOrigin, procGenValues);
	}

	public static MoundShape constructShape(Level level, BlockPos blockOrigin, long seed) {
		Biome biome = level.getBiome(blockOrigin).get();
		MoundShape.ProcGenValues procGenValues = new MoundShape.ProcGenValues(
				seed,
				(byte) -21,
				(byte) 5,
				(byte) 2,
				level.getMaxBuildHeight(),
				level.getSeaLevel(),
				TemperatureUtil.getTemperature(biome, blockOrigin),
				biome.getDownfall()
		);

		return genShape(blockOrigin, procGenValues);
	}

	private static MoundShape genShape(BlockPos blockOrigin, MoundShape.ProcGenValues procGenValues) {
		Context ctx = new Context();
		ctx.random = RandomSource.create(procGenValues.seed());
		Vec3 origin = ctx.origin = Vec3.atCenterOf(blockOrigin);

		float radius = 8f * (1 + Mth.clamp(procGenValues.radiusMultiplier(), -0.5f, 1.5f));

		float biomeTemperature = procGenValues.biomeTemperature();
		float biomeHumidity = procGenValues.biomeHumidity();

		float heatMultiplier = TemperatureUtil.rescale(biomeTemperature) * 0.5f + biomeTemperature / TemperatureUtil.MAX_TEMP * 0.5f;
		float coldMultiplier = TemperatureUtil.isFreezing(biomeTemperature) ? 0.1f : 1;
		float erosionMultiplier = 0.1f + biomeHumidity * coldMultiplier;
		float erosionMultiplierInv = 1 - erosionMultiplier;

		ctx.spikiness = Mth.clamp(procGenValues.heightMultiplier() + heatMultiplier, 0, 1);
		ctx.slantMultiplier = 0.1f + ctx.random.nextFloat() + heatMultiplier * 2f;
		ctx.relativeWallThickness = Mth.clamp((1 - heatMultiplier) * 8, 2.25f, 8);

		ctx.minMoundRadius = 3 + erosionMultiplier * 3f;
		ctx.baseMoundRadius = radius + (radius / 2) * erosionMultiplierInv;
		ctx.maxMoundRadius = ctx.minMoundRadius + ctx.baseMoundRadius;

		float subSpireRadius = ctx.maxMoundRadius / 2;
		float subSpires = Mth.clamp(procGenValues.subSpires(), 0, countCirclesOnCircumference(ctx.maxMoundRadius, subSpireRadius));

		int maxBuildHeight = procGenValues.maxBuildHeight();
		int seaLevel = procGenValues.seaLevel();
		float maxMoundHeight = Mth.clamp(maxBuildHeight * ctx.spikiness, 0, (maxBuildHeight - seaLevel));

		ctx.dirLean = new Vec3(ctx.random.nextFloat() - ctx.random.nextFloat(), 0, ctx.random.nextFloat() - ctx.random.nextFloat()).normalize();
		ctx.maxLean = ctx.dirLean.scale(2);
		genSpire(origin.x, origin.y, origin.z, maxMoundHeight, ctx.baseMoundRadius, ctx, true);

		float subRadius = (ctx.baseMoundRadius + ctx.relativeWallThickness) * Mth.sin(Mth.PI / subSpires);
		float r = Mth.lerp(ctx.spikiness, subSpireRadius, ctx.baseMoundRadius) + ctx.relativeWallThickness;
		float startAngle = ctx.random.nextFloat() * (Mth.PI * 2);
		float angle = (Mth.PI * 2) / subSpires;

		for (int n = 0; n < subSpires; n++) {
			float arc = startAngle + angle * n;
			double xn = origin.x + Mth.sin(arc) * r;
			double zn = origin.z + Mth.cos(arc) * r;
			genSpire(xn, origin.y, zn, maxMoundHeight / (1.5f + ctx.random.nextFloat() * 1.5f), subRadius, ctx);
		}

		return new MoundShape(blockOrigin, ctx.boundingShapes, ctx.chambers, procGenValues);
	}

	private static void genSpire(double x, double y, double z, float maxHeight, float baseRadius, Context context) {
		genSpire(x, y, z, maxHeight, baseRadius, context, false);
	}

	private static void genSpire(double x, double y, double z, float maxHeight, float baseRadius, Context context, boolean isMainSpire) {

		Vector3d prevLean = new Vector3d(0, 0, 0);
		Vector3d leanOffset = new Vector3d(0, 0, 0);
		float prevRadius = baseRadius + context.relativeWallThickness;
		float totalHeight = 0;

		genSphereWithChambers(x, y, z, prevRadius, prevRadius - context.relativeWallThickness / 2f, context, isMainSpire ? ChamberFactoryType.CRADLE : ChamberFactoryType.DEFAULT);

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

			genSphereWithChambers(x + leanX, y + height, z + leanZ, radius, radius - context.relativeWallThickness / 2f, context, ChamberFactoryType.DEFAULT);

			prevLean.set(leanX, 0, leanZ);
			prevRadius = radius;
			totalHeight = height;
		}

		//end cap shape
		ChamberFactoryType chamberType = isMainSpire ? ChamberFactoryType.END_CAP_MAIN_SPIRE : ChamberFactoryType.END_CAP_SUB_SPIRE;
		genSphereWithChambers(x + prevLean.x, y + totalHeight + (prevRadius / 2) * 1.5f, z + prevLean.z, prevRadius / 2f, prevRadius / 2f, context, chamberType);
	}

	private static void genSphereWithChambers(double x, double y, double z, float radius, float chamberRadius, Context context, ChamberFactoryType type) {
		Vec3 pos = new Vec3(x, y, z);
		context.boundingShapes.add(new SphereShape(pos, radius));

		if (type == ChamberFactoryType.CRADLE) {
			context.mainChamberRadius = chamberRadius * 0.9f;

			Consumer<MoundChamber> consumer = chamber -> {
				context.cradleChambers.add(chamber);
				context.chambers.add(chamber);
			};

			ChamberFactory.SPECIAL_CRADLE.create(x, y, z, chamberRadius, context.random, consumer);
			return;
		}

		Consumer<MoundChamber> filteredConsumer = chamber -> {
			//			Vec3 closestPoint = GeometryUtil.closestPointOnAABB(chamber.getAABB(), context.origin);
			//			double distSqr = closestPoint.distanceToSqr(context.origin);
			//			boolean noIntersectionWithCradleChambers = distSqr > context.mainChamberRadius * context.mainChamberRadius;

			boolean intersectingWithCradleChamber = false;
			for (MoundChamber cradleChamber : context.cradleChambers) {
				if (cradleChamber.intersectsCuboid(chamber.getAABB())) {
					intersectingWithCradleChamber = true;
					break;
				}
			}

			if (!intersectingWithCradleChamber) {
				context.chambers.add(chamber);
			}
		};

		if (chamberRadius < 8) {
			filteredConsumer.accept(new MoundChamber(new SphereShape(pos, chamberRadius)));
			return;
		}

		switch (type) {
			case DEFAULT -> ChamberFactory.RANDOM_DEFAULT.create(x, y, z, chamberRadius, context.random, filteredConsumer);
			case END_CAP_MAIN_SPIRE -> {
				ChamberFactory generator = context.random.nextFloat() < 0.8f ? ChamberFactory.ONE_SPHERE : ChamberFactory.ONE_BIG_FOUR_SMALL_ELLIPSOIDS;
				generator.create(x, y, z, chamberRadius, context.random, filteredConsumer);
			}
			case END_CAP_SUB_SPIRE -> {
				ChamberFactory generator = context.random.nextFloat() < 0.8f ? ChamberFactory.ONE_BIG_FOUR_SMALL_ELLIPSOIDS : ChamberFactory.ONE_SPHERE;
				generator.create(x, y, z, chamberRadius, context.random, filteredConsumer);
			}
			default -> { /* do nothing */ }
		}
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
		List<Shape> boundingShapes = new ArrayList<>();
		List<MoundChamber> chambers = new ArrayList<>();
		List<MoundChamber> cradleChambers = new ArrayList<>();
		RandomSource random;
		Vec3 origin;
		float spikiness;
		float slantMultiplier;
		float minMoundRadius;
		float baseMoundRadius;
		float maxMoundRadius;
		float mainChamberRadius;
		float relativeWallThickness;
		Vec3 dirLean;
		Vec3 maxLean;
	}
}
