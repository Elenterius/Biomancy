package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.github.elenterius.biomancy.util.function.IntOperator;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float accuracy, float spreadBias, Vector3fc localSpawnOffset, ProjectileFactory<T> factory, Consumer<T> modifier) {
		Vec3 eyePosition = shooter.getEyePosition();
		Quaternionfc viewRotation = new Quaternionf().rotationYXZ(
				-shooter.getViewYRot(1f) * Mth.DEG_TO_RAD,
				shooter.getViewXRot(1f) * Mth.DEG_TO_RAD,
				0f
		);

		Vector3f offset = viewRotation.transform(localSpawnOffset.mul(-1f, 0f, 0f, new Vector3f()));
		Vec3 spawnPos = eyePosition.add(offset.x, offset.y, offset.z);

		RandomSource rand = shooter.getRandom();

		//accuracy logic
		float u = (float) Math.pow(rand.nextFloat(), spreadBias);
		float v = rand.nextFloat();

		Vector3f localDirection = new Vector3f();
		uniformSphericalCap((1f - Mth.abs(accuracy)) * Mth.PI, u, v, localDirection);

		if (accuracy < 0f) localDirection.z *= -1f;

		Vector3f aimDirection = viewRotation.transform(localDirection, new Vector3f()).normalize();

		//compensate for offset spawn position to prevent projectile aiming parallel to "players aim"
		float distance = 1024f; //arbitrary far away aim distance
		Vec3 targetPos = eyePosition.add(aimDirection.x * distance, aimDirection.y * distance, aimDirection.z * distance); // "player aim"
		Vec3 shootDirection = targetPos.subtract(spawnPos).normalize(); //aim projectile towards "player aim"

		T projectile = factory.create(level, spawnPos.x, spawnPos.y, spawnPos.z);
		setMovementAndRotation(projectile, shootDirection, velocity);

		projectile.setOwner(shooter);
		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}
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

		return true;
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable LivingEntity shooter, Vec3 origin, Vec3 target, float velocity, float damage, int knockback, float accuracy, float spreadBias, ProjectileFactory<T> factory, Consumer<T> modify) {
		Vec3 viewDirection = target.subtract(origin).normalize();
		Quaternionfc viewRotation = new Quaternionf().lookAlong((float) viewDirection.x, (float) viewDirection.y, (float) viewDirection.z, 0f, 1f, 0f);

		RandomSource rand = level.getRandom();

		//accuracy logic
		float u = (float) Math.pow(rand.nextFloat(), spreadBias);
		float v = rand.nextFloat();

		Vector3f localDirection = new Vector3f();
		uniformSphericalCap((1f - Mth.abs(accuracy)) * Mth.PI, u, v, localDirection);

		if (accuracy < 0f) localDirection.z *= -1f;

		Vector3f shootDirection = viewRotation.transform(localDirection, new Vector3f()).normalize();

		T projectile = factory.create(level, origin.x, origin.y, origin.z);
		setMovementAndRotation(projectile, shootDirection, velocity);

		projectile.setOwner(shooter);
		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}
		modify.accept(projectile);

		return level.addFreshEntity(projectile);
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
		return shoot(level, null, context, origin, target);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable LivingEntity shooter, ProjectileShootContext<T> context, Vec3 origin, Vec3 target) {
		return shoot(
				level, shooter, origin, target,
				context.velocity(),
				context.damage(),
				context.knockback(),
				context.accuracy(),
				context.spreadBias(),
				context.factory(), projectile -> {}
		);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, ProjectileShootContext<T> context, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier) {
		return shoot(level, null, context, origin, target, velocityModifier, damageModifier, knockbackModifier, accuracyModifier);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, @Nullable LivingEntity shooter, ProjectileShootContext<T> context, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator accuracyModifier) {
		return shoot(
				level, shooter, origin, target,
				velocityModifier.apply(context.velocity()),
				damageModifier.apply(context.damage()),
				knockbackModifier.apply(context.knockback()),
				accuracyModifier.apply(context.accuracy()),
				context.spreadBias(),
				context.factory(),
				projectile -> {}
		);
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context) {
		return shoot(level, shooter, context, projectile -> {});
	}

	public static <T extends BaseProjectile> boolean shoot(Level level, LivingEntity shooter, ProjectileShootContext<T> context, Consumer<T> modify) {
		return shoot(level, shooter, context.velocity(), context.damage(), context.knockback(), context.accuracy(), context.spreadBias(), context.localOffset(), context.factory(), modify);
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
				context.factory(), modify
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
				properties.factory(),
				projectileModifier
		);
	}

}
