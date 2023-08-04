package com.github.elenterius.biomancy.util.colors;

public final class ColorHarmony {
	private ColorHarmony() {}

	private static double adjustHue(double hue, double angleDegrees) {
		return hue + angleDegrees;
	}

	public static double[][] analogousOkLCh(double[] okLCh) {
		double lightness = okLCh[0];
		double chroma = okLCh[1];
		double hue = okLCh[2];

		double[] colorA = {lightness, chroma, adjustHue(hue, 30)};
		double[] colorB = {lightness, chroma, adjustHue(hue, -30)};

		return new double[][]{okLCh, colorA, colorB};
	}

	public static double[][] complementaryOkLCh(double[] okLCh) {
		return geometricOkLCh(okLCh, 2);
	}

	public static double[][] splitComplementaryOkLCh(double[] okLCh) {
		double lightness = okLCh[0];
		double chroma = okLCh[1];
		double hue = okLCh[2];

		double[] colorA = {lightness, chroma, adjustHue(hue, 210)};
		double[] colorB = {lightness, chroma, adjustHue(hue, -210)};

		return new double[][]{okLCh, colorA, colorB};
	}

	public static double[][] triadicOkLCh(double[] okLCh) {
		return geometricOkLCh(okLCh, 3);
	}

	public static double[][] tetradicSquareOkLCh(double[] okLCh) {
		return geometricOkLCh(okLCh, 4);
	}

	public static double[][] tetradicRectOkLCh(double[] okLCh) {
		double lightness = okLCh[0];
		double chroma = okLCh[1];
		double hue = okLCh[2];

		double[] colorA = {lightness, chroma, adjustHue(hue, 30)};
		double[] colorB = {lightness, chroma, adjustHue(hue, 180)};
		double[] colorC = {lightness, chroma, adjustHue(hue, 210)};

		return new double[][]{okLCh, colorA, colorB, colorC};
	}

	public static double[][] wheelOkLCh(double[] okLCh) {
		return geometricOkLCh(okLCh, 12);
	}

	public static double[][] geometricOkLCh(double[] okLCh, int count) {
		double lightness = okLCh[0];
		double chroma = okLCh[1];
		double hue = okLCh[2];

		double[][] colors = new double[count][3];
		colors[0] = okLCh;

		double stepSize = 360.0 / count;
		double angleDegrees = stepSize;
		for (int i = 1; i < count; i++) {
			colors[i] = new double[]{lightness, chroma, adjustHue(hue, angleDegrees)};
			angleDegrees += stepSize;
		}

		return colors;
	}
}
