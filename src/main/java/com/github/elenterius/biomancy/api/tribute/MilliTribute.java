package com.github.elenterius.biomancy.api.tribute;

import com.github.elenterius.biomancy.util.SaturatedMath;

/**
 * Values are measured in milli units.<br>
 * E.g. 1000 MilliBiomass = 1 Biomass
 */
public record MilliTribute(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements Tribute {

	public static final long UNIT_SCALE = 1000;

	public static MilliTribute convertToMilliUnit(Tribute tribute) {
		return convertToMilliUnit(tribute, 1);
	}

	public static MilliTribute convertToMilliUnit(Tribute tribute, int ratio) {
		if (tribute instanceof MilliTribute milliTribute) {
			return milliTribute;
		}

		return new MilliTribute(
				SaturatedMath.castToInteger(tribute.biomass() * MilliTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.lifeEnergy() * MilliTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.successModifier() * MilliTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.diseaseModifier() * MilliTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.hostileModifier() * MilliTribute.UNIT_SCALE / ratio),
				SaturatedMath.castToInteger(tribute.anomalyModifier() * MilliTribute.UNIT_SCALE / ratio)
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

		public MilliTribute build() {
			return new MilliTribute(biomass, lifeEnergy, successModifier, diseaseModifier, hostileModifier, anomalyModifier);
		}

	}

}