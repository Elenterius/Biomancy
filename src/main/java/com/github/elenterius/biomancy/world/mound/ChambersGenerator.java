package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.shape.OctantEllipsoidShape;
import com.github.elenterius.biomancy.util.shape.SphereShape;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.function.Consumer;

public interface ChambersGenerator {
	void generate(double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer);

	ChambersGenerator ONE_SPHERE = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		consumer.accept(new MoundChamber(new SphereShape(x, y, z, chamberRadius)));
	};

	ChambersGenerator EIGHT_SMALL_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z + p, halfR, halfR, halfR, quarterR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z + p, quarterR, halfR, halfR, halfR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z - p, halfR, halfR, quarterR, quarterR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z - p, quarterR, halfR, quarterR, halfR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
	};

	ChambersGenerator SPECIAL_CRADLE = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p - 1, z, halfR, halfR, halfR, halfR, quarterR, halfR)));

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
	};

	ChambersGenerator ONE_BIG_FOUR_SMALL_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p, z, halfR, halfR, halfR, halfR, quarterR, halfR)));

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
	};

	ChambersGenerator FOUR_SMALL_ONE_BIG_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z + p, halfR, halfR, halfR, quarterR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z + p, quarterR, halfR, halfR, halfR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z - p, halfR, halfR, quarterR, quarterR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z - p, quarterR, halfR, quarterR, halfR, quarterR, halfR)));

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y - p, z, halfR, quarterR, halfR, halfR, halfR, halfR)));
	};

	ChambersGenerator TWO_BIG_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p, z, halfR, halfR, halfR, halfR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y - p, z, halfR, quarterR, halfR, halfR, halfR, halfR)));
	};

	SimpleWeightedRandomList<ChambersGenerator> RANDOM_DEFAULT = SimpleWeightedRandomList.<ChambersGenerator>builder()
			.add(ONE_SPHERE, 5)
			.add(ONE_BIG_FOUR_SMALL_ELLIPSOIDS, 20)
			.add(FOUR_SMALL_ONE_BIG_ELLIPSOIDS, 20)
			.add(TWO_BIG_ELLIPSOIDS, 10)
			.add(EIGHT_SMALL_ELLIPSOIDS, 60)
			.build();

}
