package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.world.mound.decorator.ChamberDecorators;
import com.github.elenterius.biomancy.world.spatial.geometry.OctantEllipsoidShape;
import com.github.elenterius.biomancy.world.spatial.geometry.SphereShape;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.function.Consumer;

public interface ChamberFactory {
	void create(double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer);

	ChamberFactory ONE_SPHERE = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> consumer.accept(new MoundChamber(new SphereShape(x, y, z, chamberRadius)));

	ChamberFactory EIGHT_SMALL_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
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

	ChamberFactory ONE_BIG_FOUR_SMALL_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p, z, halfR, halfR, halfR, halfR, quarterR, halfR)));

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
	};

	ChamberFactory FOUR_SMALL_ONE_BIG_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z + p, halfR, halfR, halfR, quarterR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z + p, quarterR, halfR, halfR, halfR, quarterR, quarterR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y + p, z - p, halfR, halfR, quarterR, quarterR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y + p, z - p, quarterR, halfR, quarterR, halfR, quarterR, halfR)));

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y - p, z, halfR, quarterR, halfR, halfR, halfR, halfR)));
	};

	ChamberFactory TWO_BIG_ELLIPSOIDS = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p, z, halfR, halfR, halfR, halfR, quarterR, halfR)));
		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y - p, z, halfR, quarterR, halfR, halfR, halfR, halfR)));
	};

	ChamberFactory SPECIAL_CRADLE = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
		float halfR = chamberRadius / 2;
		float quarterR = halfR / 2;
		float p = chamberRadius / 3.8f; // radius / 4.25f

		consumer.accept(new MoundChamber(new OctantEllipsoidShape(x, y + p - 1, z, halfR, halfR, halfR, halfR, quarterR, halfR)));

		Consumer<MoundChamber> wrappedConsumer = chamber -> {
			chamber.setDecorator(ChamberDecorators.PRIMAL_ORIFICE_COMBS, random.nextInt());
			consumer.accept(chamber);
		};

		float offset = 0.5f;
		wrappedConsumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p - offset, z + p, halfR, quarterR, halfR, quarterR, halfR, quarterR)));
		wrappedConsumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p - offset, z + p, quarterR, quarterR, halfR, halfR, halfR, quarterR)));
		wrappedConsumer.accept(new MoundChamber(new OctantEllipsoidShape(x + p, y - p - offset, z - p, halfR, quarterR, quarterR, quarterR, halfR, halfR)));
		wrappedConsumer.accept(new MoundChamber(new OctantEllipsoidShape(x - p, y - p - offset, z - p, quarterR, quarterR, quarterR, halfR, halfR, halfR)));
	};

	SimpleWeightedRandomList<ChamberFactory> RANDOM_DEFAULTS_LIST = SimpleWeightedRandomList.<ChamberFactory>builder()
			.add(ONE_SPHERE, 5)
			.add(ONE_BIG_FOUR_SMALL_ELLIPSOIDS, 20)
			.add(FOUR_SMALL_ONE_BIG_ELLIPSOIDS, 20)
			.add(TWO_BIG_ELLIPSOIDS, 10)
			.add(EIGHT_SMALL_ELLIPSOIDS, 60)
			.build();

	ChamberFactory RANDOM_DEFAULT = (double x, double y, double z, float chamberRadius, RandomSource random, Consumer<MoundChamber> consumer) -> {
		ChamberFactory factory = RANDOM_DEFAULTS_LIST.getRandomValue(random).orElse(ChamberFactory.EIGHT_SMALL_ELLIPSOIDS);

		Consumer<MoundChamber> wrappedConsumer = chamber -> {
			chamber.setDecorator(ChamberDecorators.getRandomDefault(random), random.nextInt());
			consumer.accept(chamber);
		};

		factory.create(x, y, z, chamberRadius, random, wrappedConsumer);
	};

}
