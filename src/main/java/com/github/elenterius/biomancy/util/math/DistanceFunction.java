package com.github.elenterius.biomancy.util.math;

public interface DistanceFunction {
	double SQRT_2 = Math.sqrt(2);
	double SQRT_3 = Math.sqrt(3);

	DistanceFunction IDENTITY = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return 1d;
		}

		@Override
		public double apply(double x, double y, double z) {
			return 1d;
		}
	};
	DistanceFunction LENGTH = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return Math.sqrt(x * x + z * z) / SQRT_2;
		}

		@Override
		public double apply(double x, double y, double z) {
			return Math.sqrt(x * x + y * y + z * z) / SQRT_3;
		}
	};
	DistanceFunction LENGTH_SQUARED = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return x * x + z * z;
		}

		@Override
		public double apply(double x, double y, double z) {
			return x * x + y * y + z * z;
		}
	};
	DistanceFunction MANHATTAN = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return x + z;
		}

		@Override
		public double apply(double x, double y, double z) {
			return x + y + z;
		}
	};
	DistanceFunction QUADRATIC = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return x * x + z * z + x * z;
		}

		@Override
		public double apply(double x, double y, double z) {
			return x * x + y * y + z * z + x * y + x * z + y * z;
		}
	};
	DistanceFunction SPECIAL = new DistanceFunction() {
		@Override
		public double apply(double x, double z) {
			return Math.pow(Math.E, Math.sqrt(x * x + z * z) / SQRT_2) / Math.E;
		}

		@Override
		public double apply(double x, double y, double z) {
			return 1;
		}
	};

	double apply(double xDistance, double zDistance);

	double apply(double xDistance, double yDistance, double zDistance);
}
