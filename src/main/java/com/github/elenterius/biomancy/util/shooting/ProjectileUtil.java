package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.github.elenterius.biomancy.util.function.IntOperator;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ProjectileUtil {

	private ProjectileUtil() {}

	/// @param localSpawnOffset spawn offset in local space (x=RIGHT, y=UP, z=FORWARD)
	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float accuracy, float spreadBias, Vector3fc localSpawnOffset, ProjectileEntityType<T> entityType, int projectileCount, Consumer<T> modifier) {
		Vec3 eyePosition = shooter.getEyePosition();
		Quaternionfc viewRotation = new Quaternionf().rotationYXZ(
				-shooter.getViewYRot(1f) * Mth.DEG_TO_RAD,
				shooter.getViewXRot(1f) * Mth.DEG_TO_RAD,
				0f
		);

		Vector3f offset = viewRotation.transform(localSpawnOffset.mul(-1f, 1f, 1f, new Vector3f()));
		Vec3 spawnPos = eyePosition.add(offset.x, offset.y, offset.z);

		RandomSource rand = shooter.getRandom();

		float coneAngle = (1f - Mth.abs(accuracy)) * Mth.PI;
		float distance = 1024f; //arbitrary far away aim distance

		Vector3f localDirection = new Vector3f();
		Vector3f aimDirection = new Vector3f();

		for (int i = 0; i < projectileCount; i++) {

			float u = (float) Math.pow((i + rand.nextFloat()) / projectileCount, spreadBias); //stratified sample
			float v = projectileCount < 5 ? rand.nextFloat() : (i * 0.61803398875f) % 1f; // golden ratio
			uniformSphericalCap(coneAngle, u, v, localDirection);

			if (accuracy < 0f) localDirection.z *= -1f;

			viewRotation.transform(localDirection, aimDirection).normalize();

			//compensate for offset spawn position to prevent projectile aiming parallel to "players aim"
			Vec3 targetPos = eyePosition.add(aimDirection.x * distance, aimDirection.y * distance, aimDirection.z * distance); // "player aim"
			Vec3 shootDirection = targetPos.subtract(spawnPos).normalize(); //aim projectile towards "player aim"

			T projectile = entityType.create(level);
			if (projectile == null) return false;

			projectile.setPos(spawnPos);
			setMovementAndRotation(projectile, shootDirection, velocity);

			projectile.setOwner(shooter);
			projectile.setDamage(damage);
			projectile.setKnockback(knockback);
			modifier.accept(projectile);

			if (eyePosition.distanceTo(spawnPos) > 0.2d) {
				//prevent shooting through walls due to any offset
				BlockHitResult hitResult = level.clip(new ClipContext(eyePosition, spawnPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, projectile));
				if (hitResult.getType() != HitResult.Type.MISS) {
					projectile.onHit(hitResult);
				}
			}

			if (projectile.isAlive()) {
				level.addFreshEntity(projectile);
			}
		}

		return true;
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable Entity owner, Vec3 origin, Vec3 direction, float velocity, float damage, int knockback, float accuracy, float spreadBias, ProjectileEntityType<T> entityType, int projectileCount, Consumer<T> modify) {
		Quaternionfc aimRotation = new Quaternionf().lookAlong((float) direction.x, (float) direction.y, (float) direction.z, 0f, 1f, 0f);
		return shoot(level, owner, origin, aimRotation, velocity, damage, knockback, accuracy, spreadBias, entityType, projectileCount, modify);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable Entity owner, Vec3 origin, Quaternionfc aimRotation, float velocity, float damage, int knockback, float accuracy, float spreadBias, ProjectileEntityType<T> entityType, int projectileCount, Consumer<T> modify) {
		RandomSource rand = level.getRandom();

		float coneAngle = (1f - Mth.abs(accuracy)) * Mth.PI;

		Vector3f localDirection = new Vector3f();
		Vector3f shootDirection = new Vector3f();

		for (int i = 0; i < projectileCount; i++) {

			float u = (float) Math.pow((i + rand.nextFloat()) / projectileCount, spreadBias); //stratified sample
			float v = projectileCount < 5 ? rand.nextFloat() : (i * 0.61803398875f) % 1f; // golden ratio
			uniformSphericalCap(coneAngle, u, v, localDirection);

			if (accuracy < 0f) localDirection.z *= -1f;

			aimRotation.transform(localDirection, shootDirection).normalize();

			T projectile = entityType.create(level);
			if (projectile == null) return false;

			projectile.setPos(origin);
			setMovementAndRotation(projectile, shootDirection, velocity);

			projectile.setOwner(owner);
			projectile.setDamage(damage);
			projectile.setKnockback(knockback);
			modify.accept(projectile);

			level.addFreshEntity(projectile);
		}

		return true;
	}

	private static void uniformSphericalCap(float angle, float u, float v, Vector3f dest) {
		float cosTheta = 1f - (1f - Mth.cos(angle)) * u; // uniform sampling
		float sinTheta = Mth.sqrt(1f - cosTheta * cosTheta); //inverse sampling
		float phi = 2f * Mth.PI * v; // uniform sampling
		dest.set(
				Mth.cos(phi) * sinTheta,
				Mth.sin(phi) * sinTheta,
				cosTheta
		);
	}

	private static <T extends BaseProjectile> void setMovementAndRotation(T projectile, Vec3 direction, float velocity) {
		Vec3 deltaMovement = direction.scale(velocity);
		setMovementAndRotation(projectile, deltaMovement);
	}

	private static <T extends BaseProjectile> void setMovementAndRotation(T projectile, Vector3fc direction, float velocity) {
		Vec3 deltaMovement = new Vec3(
				direction.x() * velocity,
				direction.y() * velocity,
				direction.z() * velocity
		);
		setMovementAndRotation(projectile, deltaMovement);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private static <T extends BaseProjectile> void setMovementAndRotation(T projectile, Vec3 deltaMovement) {
		projectile.setDeltaMovement(deltaMovement);

		double xzLength = deltaMovement.horizontalDistance();
		projectile.setXRot((float) (Mth.atan2(deltaMovement.y, xzLength) * Mth.RAD_TO_DEG));
		projectile.setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * Mth.RAD_TO_DEG));
		projectile.yRotO = projectile.getYRot();
		projectile.xRotO = projectile.getXRot();
	}

	//	private static <T extends BaseProjectile> void setMovementAndRotation(T projectile, Quaternionfc rotation, float velocity) {
	//		Vector3f direction = rotation.transform(new Vector3f(0f, 0f, 1f)).normalize();
	//		direction.mul(velocity);
	//		projectile.setDeltaMovement(direction.x, direction.y, direction.z);
	//
	//		Vector3f euler = rotation.getEulerAnglesYXZ(new Vector3f()).mul(Mth.RAD_TO_DEG, -Mth.RAD_TO_DEG, 0f);
	//
	//		projectile.setYRot(euler.y); //yaw
	//		projectile.setXRot(euler.x); //pitch
	//		projectile.yRotO = projectile.getYRot();
	//		projectile.xRotO = projectile.getXRot();
	//	}

	public static <T extends BaseProjectile> boolean shoot(Level level, ProjectileShootContext<T> context, Vec3 origin, Vec3 target) {
		return shoot(level, null, context, origin, target, false);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable Entity owner, ProjectileShootContext<T> context, Vec3 origin, Vec3 target, boolean aimAssist) {
		Quaternionfc aimRotation;

		if (aimAssist) {
			float airDrag = context.entityType().getAirDrag();
			float gravity = context.entityType().getGravity();
			aimRotation = computeLaunchDirection(origin, target, context.velocity(), airDrag, gravity, false);
		}
		else {
			Vec3 direction = target.subtract(origin).normalize();
			aimRotation = new Quaternionf().lookAlong((float) direction.x, (float) direction.y, (float) direction.z, 0f, 1f, 0f);
		}

		return shoot(
				level, owner, origin, aimRotation,
				context.velocity(),
				context.damage(),
				context.knockback(),
				context.accuracy(),
				context.spreadBias(),
				context.entityType(), context.projectileCount(), projectile -> {}
		);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, ProjectileShootContext<T> context, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier) {
		return shoot(level, null, context, origin, target, velocityModifier, damageModifier, knockbackModifier, accuracyModifier);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable Entity owner, ProjectileShootContext<T> context, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier) {
		Vec3 direction = target.subtract(origin).normalize();
		return shoot(
				level, owner, origin, direction,
				velocityModifier.apply(context.velocity()),
				damageModifier.apply(context.damage()),
				knockbackModifier.apply(context.knockback()),
				accuracyModifier.apply(context.accuracy()),
				context.spreadBias(),
				context.entityType(),
				context.projectileCount(),
				projectile -> {}
		);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context) {
		return shoot(level, shooter, context, projectile -> {});
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context, Consumer<T> modify) {
		return shoot(level, shooter, context.velocity(), context.damage(), context.knockback(), context.accuracy(), context.spreadBias(), context.localOffset(), context.entityType(), context.projectileCount(), modify);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier) {
		return shoot(level, shooter, context, velocityModifier, damageModifier, knockbackModifier, accuracyModifier, context.localOffset(), projectile -> {});
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier, Vector3fc localSpawnOffset) {
		return shoot(level, shooter, context, velocityModifier, damageModifier, knockbackModifier, accuracyModifier, localSpawnOffset, projectile -> {});
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier, Vector3fc localSpawnOffset, Consumer<T> modify) {
		return shoot(
				level, shooter,
				velocityModifier.apply(context.velocity()),
				damageModifier.apply(context.damage()),
				knockbackModifier.apply(context.knockback()),
				accuracyModifier.apply(context.accuracy()),
				context.spreadBias(),
				localSpawnOffset,
				context.entityType(), context.projectileCount(), modify
		);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ItemStack stack, Gun<T> gun) {
		return shoot(level, shooter, stack, gun, projectile -> {});
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ItemStack stack, Gun<T> gun, Consumer<T> projectileModifier) {
		GunProperties<T> properties = gun.getGunProperties();
		return shoot(
				level, shooter,
				gun.getProjectileVelocity(stack),
				gun.getProjectileDamage(stack),
				gun.getProjectileKnockBack(stack),
				gun.getAccuracy(stack),
				properties.spreadBias(),
				properties.localOffset(),
				properties.projectileType().get(),
				gun.getProjectileCount(stack),
				projectileModifier
		);
	}

	/// @param launchAngle Launch angle in radians from -90deg to 90deg
	/// @return Horizontal range
	public static double simulateRange(double initialHeight, double launchAngle, double initialVelocity, double drag, double gravity) {
		double maxAngle = Math.PI / 2d;
		if (launchAngle > maxAngle || launchAngle < -maxAngle) throw new IllegalArgumentException("launch angle is outside of valid range");

		double vx = initialVelocity * Math.cos(launchAngle);
		double vy = initialVelocity * Math.sin(launchAngle);

		if (vy >= 0d && drag == 0d && gravity == 0d) return Double.POSITIVE_INFINITY;

		double x = 0, y = initialHeight;
		double prevX = x, prevY = y;

		while (y > 0d) {
			vx = vx + -drag * vx;
			vy = vy + -drag * vy - gravity;

			prevX = x;
			prevY = y;

			x += vx;
			y += vy;
		}

		double slope = (y - prevY) / (x - prevX);
		double xIntercept = prevX - (prevY / slope);

		if (xIntercept < 1e-3) {
			return 0d;
		}

		return xIntercept;
	}

	public static double computeRange(double initialHeight, float launchAngle, double initialVelocity, double drag, double gravity) {
		double vx = initialVelocity * Mth.cos(launchAngle);
		double vy = initialVelocity * Mth.sin(launchAngle);
		double t = computeFlightTime(vy, initialHeight, drag, gravity);
		return (vx / drag) * (1d - Math.exp(-drag * t));
	}

	public static double computeFlightTime(double vy, double initialHeight, double drag, double gravity) {
		double gd = gravity / drag;
		double vg = vy + gd;
		double vgd = vg / drag;

		double t = (vy + Math.sqrt(vy * vy + 2d * gravity * initialHeight)) / gravity;

		for (int i = 0; i < 4; i++) {
			double e = Math.exp(-drag * t);
			double y = initialHeight + vgd * (1d - e) - gd * t;
			double dy = vg * e - gd;
			t -= y / dy;
		}

		return t;
	}

	public static double computeDerivativeRange(double initialHeight, float launchAngle, double initialVelocity, double drag, double gravity) {
		float thetaCos = Mth.cos(launchAngle);
		float thetaSin = Mth.sin(launchAngle);

		double vx_0 = initialVelocity * thetaCos;
		double vy_0 = initialVelocity * thetaSin;
		double t = computeFlightTime(vy_0, initialHeight, drag, gravity);
		double e = Math.exp(-drag * t);

		double dt_dvy_0 = (1d - e) / ((vy_0 + gravity / drag) * e - gravity / drag);
		double dvx_0_dAngle = initialVelocity * -thetaSin;
		double dvy_0_dAngle = initialVelocity * thetaCos;

		// dR/dAngle = dR/dvx_0 * dvx_0/dAngle + dR/dt * dt/dvy_0 * dvy_0/dAngle
		double dR_dvx_0 = (1d - e) / drag;
		double dR_dt = vx_0 * e;
		return dR_dvx_0 * dvx_0_dAngle + dR_dt * dt_dvy_0 * dvy_0_dAngle;
	}

	public static double computeLaunchAngle(double initialHeight, double initialVelocity, double drag, double gravity, double targetX, double targetY, boolean highArc) {
		double angle = (highArc ? 60 : 30) * Mth.DEG_TO_RAD;
		double dTheta = 1e-6d;

		for (int i = 0; i < 6; i++) {
			double vx_0 = initialVelocity * Mth.cos((float) angle);
			double t = -Math.log(1d - drag * targetX / vx_0) / drag;

			double y = computeY(initialHeight, initialVelocity, angle, drag, gravity, t);
			double error = y - targetY;

			double y_next = computeY(initialHeight, initialVelocity, angle + dTheta, drag, gravity, t);
			double dy_dTheta = (y_next - y) / dTheta;

			angle -= error / dy_dTheta;
		}

		return angle;
	}

	private static double computeY(double initialHeight, double initialVelocity, double angle, double drag, double gravity, double t) {
		double vy_0 = initialVelocity * Mth.sin((float) angle);
		return initialHeight + (vy_0 + gravity / drag) * (1d - Math.exp(-drag * t)) / drag - gravity * t / drag;
	}

	public static Quaternionfc computeLaunchDirection(Vec3 launchPos, Vec3 targetPos, double initialVelocity, double drag, double gravity, boolean highArc) {
		double dx = targetPos.x - launchPos.x;
		double dz = targetPos.z - launchPos.z;
		double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
		double verticalDistance = targetPos.y - launchPos.y;

		double yaw = Mth.atan2(dz, dx);
		double pitch = computeLaunchAngle(0, initialVelocity, drag, gravity, horizontalDistance, verticalDistance, highArc);

		return new Quaternionf().rotationYXZ(
				(float) -yaw,
				(float) pitch,
				0f
		);
	}

}
