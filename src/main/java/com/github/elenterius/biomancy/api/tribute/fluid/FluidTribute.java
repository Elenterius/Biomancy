package com.github.elenterius.biomancy.api.tribute.fluid;

import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.util.SaturatedMath;

/**
 * Values are measured in milli units.<br>
 * E.g. 1000 Fluid-Biomass = 1 Biomass
 */
public record FluidTribute(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements Tribute {

	public static final long UNIT_SCALE = 1000;

	public static FluidTribute of(Tribute tribute) {
		return of(tribute, 1);
	}

	public static FluidTribute of(Tribute tribute, int ratio) {
		if (tribute instanceof FluidTribute fluidTribute) {
			return fluidTribute;
		}

		return new FluidTribute(
				SaturatedMath.castToInteger(tribute.biomass() * FluidTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.lifeEnergy() * FluidTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.successModifier() * FluidTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.diseaseModifier() * FluidTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.hostileModifier() * FluidTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.anomalyModifier() * FluidTribute.UNIT_SCALE / ratio)
		);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private int biomass = 0;
		private int lifeEnergy = 0;

		private int successModifier = 0;

		private int diseaseModifier = 0;
		private int hostileModifier = 0;
		private int anomalyModifier = 0;

		private Builder() {}

		public Builder successModifier(int successModifier) {
			this.successModifier = successModifier;
			return this;
		}

		public Builder diseaseModifier(int diseaseModifier) {
			this.diseaseModifier = diseaseModifier;
			return this;
		}

		public Builder hostileModifier(int hostileModifier) {
			this.hostileModifier = hostileModifier;
			return this;
		}

		public Builder biomass(int biomass) {
			this.biomass = biomass;
			return this;
		}

		public Builder lifeEnergy(int lifeEnergy) {
			this.lifeEnergy = lifeEnergy;
			return this;
		}

		public Builder anomalyModifier(int anomalyModifier) {
			this.anomalyModifier = anomalyModifier;
			return this;
		}

		public FluidTribute build() {
			return new FluidTribute(biomass, lifeEnergy, successModifier, diseaseModifier, hostileModifier, anomalyModifier);
		}

	}

}