package com.github.elenterius.biomancy.util.random;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

public final class RejectionSampling {
	private RejectionSampling() {}

	private static final float EPS = Float.MIN_NORMAL;
	private static final float BIAS = 0x1.0p-36f;

	/// @return x² + y²
	public static float uniformDisc(Random random, Vector2f dest) {
		float d = 1f;
		float x = 0f;
		float y = 0f;

		while (d >= 1f) {
			x = 2f * random.nextFloat() - 1f;
			y = 2f * random.nextFloat() - 1f;
			d = x * x + y * y;
		}

		dest.x = x;
		dest.y = y;

		return d;
	}

	public static float uniformCircle(Random random, Vector2f dest) {
		float d = uniformDisc(random, dest);
		float s = Math.invsqrt(d + BIAS * BIAS);
		dest.x += BIAS;
		dest.x *= s;
		dest.y *= s;
		return d;
	}

	public static void uniformSphericalCapViaRejection(Random random, float h, Vector3f dest) {
		Vector2f v = new Vector2f();
		float k = h * uniformDisc(random, v); // h * (x² + y²)
		float s = Math.sqrt(h * (2f - k));
		dest.set(s * v.x, s * v.y, 1f - k);
	}

	/// @param u unit bi-vector
	public static void uniformQuaternionAboutFixedAxis(Random random, Vector3f u, Quaternionf dest) {
		Vector2f p = new Vector2f();
		uniformCircle(random, p);
		dest.set(p.y * u.x, p.y * u.y, p.y * u.z, p.x);
	}

	public static void uniformQuaternionFromZDirection(Random random, Quaternionf dest) {
		Vector2f v = new Vector2f();
		float d = uniformDisc(random, v); // x² + y²
		float s = Math.sqrt(1f - d);
		dest.set(v.x, v.y, 0f, s);
	}

	public static void uniformQuaternion(Random random, Quaternionf dest) {
		Vector2f p0 = new Vector2f();
		Vector2f p1 = new Vector2f();
		float d1 = uniformDisc(random, p1) + EPS;
		float s1 = Math.invsqrt(d1);
		float d0 = uniformDisc(random, p0);
		float s0 = Math.sqrt(1f - d0);
		float s = s0 * s1;

		dest.set(p0.y, s * p1.x, s * p1.y, p0.x);
	}

}
