package com.github.elenterius.biomancy.api.tribute;

public record SimpleTribute(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements Tribute {

	public static SimpleTribute.Builder builder() {
		return new SimpleTribute.Builder();
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

		public Tribute build() {
			return new SimpleTribute(biomass, lifeEnergy, successModifier, diseaseModifier, hostileModifier, anomalyModifier);
		}

	}

}
