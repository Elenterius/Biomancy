package com.github.elenterius.biomancy.util.colors;

import net.minecraft.util.FastColor;

public final class ColorSpace {

	private ColorSpace() {}

	private static double cube(double x) {
		return x * x * x;
	}

	public static final class SRGB {
		private SRGB() {}

		/**
		 * @return linear sRGB
		 */
		public static double toLinear(double colorChannel) {
			if (colorChannel < 0.04045) {
				return colorChannel / 12.92;
			}
			return Math.pow((colorChannel + 0.055) / 1.055, 2.4);
		}

		/**
		 * @return linear sRGB
		 */
		public static double[] toLinear(double[] rgb) {
			return new double[]{
					toLinear(rgb[0]),
					toLinear(rgb[1]),
					toLinear(rgb[2]),
			};
		}

		/**
		 * @return sRGB
		 */
		public static double fromLinear(double colorChannel) {
			if (colorChannel < 0.0031308) {
				return 12.92 * colorChannel;
			}
			return 1.055 * Math.pow(colorChannel, 1 / 2.4) - 0.055;
		}

		/**
		 * @return sRGB
		 */
		public static double[] fromLinear(double[] linearRGB) {
			return new double[]{
					fromLinear(linearRGB[0]),
					fromLinear(linearRGB[1]),
					fromLinear(linearRGB[2]),
			};
		}

		/**
		 * @return sRGB
		 */
		public static double[] fromARGB32(int argb) {
			return new double[]{
					FastColor.ARGB32.red(argb) / 255d,
					FastColor.ARGB32.green(argb) / 255d,
					FastColor.ARGB32.blue(argb) / 255d
			};
		}

		/**
		 * @return aRGB 32
		 */
		public static int toARGB32(double[] rgb) {
			return FastColor.ARGB32.color(
					1,
					(int) (rgb[0] * 255),
					(int) (rgb[1] * 255),
					(int) (rgb[2] * 255)
			);
		}
	}

	/**
	 * @see <a href="https://bottosson.github.io/posts/oklab/#oklab-implementations">Oklab</a>
	 */
	public static final class OkLab {
		private OkLab() {}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromLinearSRGB(double rLinear, double gLinear, double bLinear) {
			double cubeRootL = Math.cbrt(0.4122214708 * rLinear + 0.5363325363 * gLinear + 0.0514459929 * bLinear);
			double cubeRootM = Math.cbrt(0.2119034982 * rLinear + 0.6806995451 * gLinear + 0.1073969566 * bLinear);
			double cubeRootS = Math.cbrt(0.0883024619 * rLinear + 0.2817188376 * gLinear + 0.6299787005 * bLinear);

			double L = 0.2104542553 * cubeRootL + 0.7936177850 * cubeRootM - 0.0040720468 * cubeRootS;
			double a = 1.9779984951 * cubeRootL - 2.4285922050 * cubeRootM + 0.4505937099 * cubeRootS;
			double b = 0.0259040371 * cubeRootL + 0.7827717662 * cubeRootM - 0.8086757660 * cubeRootS;

			return new double[]{L, a, b};
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromSRGB(double r, double g, double b) {
			return fromLinearSRGB(SRGB.toLinear(r), SRGB.toLinear(g), SRGB.toLinear(b));
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromSRGB(double[] rgb) {
			return fromSRGB(rgb[0], rgb[1], rgb[2]);
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromARGB32(int rgb) {
			return fromSRGB(SRGB.fromARGB32(rgb));
		}

		/**
		 * @return linear sRGB (r, g, b)
		 */
		public static double[] toLinearSRGB(double[] Lab) {
			double Lightness = Lab[0];
			double a = Lab[1];
			double b = Lab[2];

			double l = cube(Lightness + 0.3963377774 * a + 0.2158037573 * b);
			double m = cube(Lightness - 0.1055613458 * a - 0.0638541728 * b);
			double s = cube(Lightness - 0.0894841775 * a - 1.2914855480 * b);

			return new double[]{
					4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
					-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
					-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
			};
		}

		/**
		 * @return sRGB (r, g, b)
		 */
		public static double[] toSRGB(double[] Lab) {
			double[] linearRGB = toLinearSRGB(Lab);
			return SRGB.fromLinear(linearRGB);
		}

		/**
		 * @return aRGB (r, g, b)
		 */
		public static int toARGB32(double[] LCh) {
			return SRGB.toARGB32(toSRGB(LCh));
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] toOkLCh(double[] Lab) {
			double Lightness = Lab[0]; // 0.0 - 100
			double a = Lab[1];
			double b = Lab[2];
			double Chroma = Math.sqrt(a * a + b * b);  //0 - 0.37
			double hue = Math.atan2(b, a);  // 0 - 360 angle

			return new double[]{Lightness, Chroma, hue};
		}
	}

	/**
	 * Oklch is the cylindrical representation of Oklab
	 */
	public static final class OkLCh {
		private OkLCh() {}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromLinearSRGB(double rLinear, double gLinear, double bLinear) {
			double[] Lab = OkLab.fromLinearSRGB(rLinear, gLinear, bLinear);
			double Lightness = Lab[0]; // 0.0 - 100
			double a = Lab[1];
			double b = Lab[2];
			double Chroma = Math.sqrt(a * a + b * b);  //0 - 0.37
			double hue = Math.atan2(b, a);  // 0 - 360 angle

			return new double[]{Lightness, Chroma, hue};
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromSRGB(double r, double g, double b) {
			return fromLinearSRGB(SRGB.toLinear(r), SRGB.toLinear(g), SRGB.toLinear(b));
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromSRGB(double[] rgb) {
			return fromSRGB(rgb[0], rgb[1], rgb[2]);
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromARGB32(int rgb) {
			return fromSRGB(SRGB.fromARGB32(rgb));
		}

		/**
		 * @return linear sRGB (r, g, b)
		 */
		public static double[] toLinearSRGB(double[] LCh) {
			return OkLab.toLinearSRGB(toOkLab(LCh));
		}

		/**
		 * @return sRGB (r, g, b)
		 */
		public static double[] toSRGB(double[] LCh) {
			double[] linearRGB = toLinearSRGB(LCh);
			return SRGB.fromLinear(linearRGB);
		}

		/**
		 * @return aRGB (r, g, b)
		 */
		public static int toARGB32(double[] LCh) {
			return SRGB.toARGB32(toSRGB(LCh));
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] toOkLab(double[] LCh) {
			double Lightness = LCh[0];
			double Chroma = LCh[1];
			double hue = Math.toRadians(LCh[2]);
			double a = Chroma * Math.cos(hue);
			double b = Chroma * Math.sin(hue);

			return new double[]{Lightness, a, b};
		}

	}

}
