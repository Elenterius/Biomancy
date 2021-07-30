package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.util.RayTraceUtil;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

//Prototype (Terraria)
public class BeeHiveGunItem extends ProjectileWeaponItem {

	public final float maxDistance;

	public BeeHiveGunItem(Properties builder) {
		super(builder, 0.75f, 1f, 0f, 1, 5 * 20);
		maxDistance = 20f;
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, double maxDistance) {
		RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(shooter, target -> !target.isSpectator() && target.isAlive() && target.canBeCollidedWith() && target instanceof LivingEntity && !shooter.isRidingSameEntity(target), maxDistance);
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return;

		BlockPos targetBlockPos = null;
		LivingEntity targetEntity = null;
		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
			BlockRayTraceResult rayTrace = (BlockRayTraceResult) rayTraceResult;
			targetBlockPos = rayTrace.getPos().offset(rayTrace.getFace());
			if (!worldIn.getFluidState(targetBlockPos).isEmpty()) return;
		}
		else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
			EntityRayTraceResult rayTrace = (EntityRayTraceResult) rayTraceResult;
			if (rayTrace.getEntity() instanceof LivingEntity) {
				targetEntity = (LivingEntity) rayTrace.getEntity();
			}
			else return;
		}

		//TODO: implement our own light weight bee entity/projectile that behaves more like homing missiles
		BeeEntity entity = EntityType.BEE.create(worldIn);
		if (entity != null) {
			entity.enablePersistence();
			if (targetEntity != null) {
				entity.setRevengeTarget(targetEntity);
				entity.setAttackTarget(targetEntity);
			}
			else if (targetBlockPos != null) {
				entity.getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1.25d);
			}

			//force move the bee towards the target
			EntitySize size = entity.getSize(Pose.FALL_FLYING);
			Vector3d posVec = shooter.getEyePosition(1f).add(0d, -0.1d, 0d).add(shooter.getLookVec().rotateYaw(-15f).normalize().add(size.width * 0.5f, 0, size.width * 0.5f));
			entity.setPosition(posVec.x, posVec.y, posVec.z);
			Vector3d direction = shooter.getLookVec().normalize().scale(2.55f);
			entity.lookAt(EntityAnchorArgument.Type.FEET, direction);
			entity.setMotion(direction);
			Vector3d playerMotion = shooter.getMotion();
			entity.setMotion(entity.getMotion().add(playerMotion.x, shooter.isOnGround() ? 0d : playerMotion.y, playerMotion.z));

			projectileWeapon.damageItem(1, shooter, livingEntity -> livingEntity.sendBreakAnimation(hand));
			if (worldIn.addEntity(entity)) {
				entity.playAmbientSound();
			}
		}
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, maxDistance);
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public Predicate<ItemStack> getInventoryAmmoPredicate() {
		return stack -> false;
	}

	@Override
	public int func_230305_d_() {
		return 20; //max range
	}

}
