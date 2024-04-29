package com.github.elenterius.biomancy.api.tribute;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface Tribute {

	Tribute EMPTY = createEmpty();

	int biomass();

	int lifeEnergy();

	int successModifier();

	int diseaseModifier();

	int hostileModifier();

	int anomalyModifier();

	default boolean isEmpty() {
		return biomass() == 0 && lifeEnergy() == 0
				&& successModifier() == 0
				&& diseaseModifier() == 0
				&& hostileModifier() == 0
				&& anomalyModifier() == 0;
	}

	private static Tribute createEmpty() {
		return new Tribute() {
			@Override
			public int biomass() {
				return 0;
			}

			@Override
			public int lifeEnergy() {
				return 0;
			}

			@Override
			public int successModifier() {
				return 0;
			}

			@Override
			public int diseaseModifier() {
				return 0;
			}

			@Override
			public int hostileModifier() {
				return 0;
			}

			@Override
			public int anomalyModifier() {
				return 0;
			}

			@Override
			public boolean isEmpty() {
				return true;
			}
		};
	}

}
