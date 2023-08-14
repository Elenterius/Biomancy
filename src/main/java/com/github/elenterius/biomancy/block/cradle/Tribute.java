package com.github.elenterius.biomancy.block.cradle;

public record Tribute(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements ITribute {
	public Tribute(ITribute a, ITribute b) {
		this(
				a.biomass() + b.biomass(),
				a.lifeEnergy() + b.lifeEnergy(),
				a.successModifier() + b.successModifier(),
				a.diseaseModifier() + b.diseaseModifier(),
				a.hostileModifier() + b.hostileModifier(),
				a.anomalyModifier() + b.anomalyModifier()
		);
	}

	public static Tribute.Builder builder() {
		return new Tribute.Builder();
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

		public ITribute create() {
			return new Tribute(biomass, lifeEnergy, successModifier, diseaseModifier, hostileModifier, anomalyModifier);
		}
	}
}
