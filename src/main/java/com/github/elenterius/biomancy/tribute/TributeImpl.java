package com.github.elenterius.biomancy.tribute;

public record TributeImpl(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements Tribute {
	public TributeImpl(Tribute a, Tribute b) {
		this(
				a.biomass() + b.biomass(),
				a.lifeEnergy() + b.lifeEnergy(),
				a.successModifier() + b.successModifier(),
				a.diseaseModifier() + b.diseaseModifier(),
				a.hostileModifier() + b.hostileModifier(),
				a.anomalyModifier() + b.anomalyModifier()
		);
	}

	public static TributeImpl.Builder builder() {
		return new TributeImpl.Builder();
	}

	public static class Builder {
		private int successModifier = 0;
		private int diseaseModifier = 0;
		private int hostileModifier = 0;
		private int biomass = 0;
		private int lifeEnergy = 0;
		private int anomalyModifier = 0;

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

		public Tribute create() {
			return new TributeImpl(biomass, lifeEnergy, successModifier, diseaseModifier, hostileModifier, anomalyModifier);
		}
	}
}
