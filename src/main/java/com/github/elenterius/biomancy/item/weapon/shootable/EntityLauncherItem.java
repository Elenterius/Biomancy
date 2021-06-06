package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class EntityLauncherItem extends ProjectileWeaponItem {

	public EntityLauncherItem(Properties builder) {
		super(builder, 0.75f, 1f, 0f, 1, 4 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon) {
		BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(worldIn);
		if (entity != null) {
			entity.enablePersistence();
			if (projectileWeapon.hasDisplayName()) {
				entity.setCustomName(projectileWeapon.getDisplayName());
				entity.setCustomNameVisible(true);
			}

			EntitySize size = entity.getSize(Pose.FALL_FLYING);

			Vector3d posVec = shooter.getEyePosition(1f).add(0d, -0.1d, 0d).add(shooter.getLookVec().rotateYaw(-15f).normalize().add(size.width * 0.5f, 0, size.width * 0.5f));
			entity.setPosition(posVec.x, posVec.y, posVec.z);
			Vector3d direction = shooter.getLookVec().normalize().scale(2.55f);
			entity.lookAt(EntityAnchorArgument.Type.FEET, direction);
			entity.setMotion(direction);
			entity.isAirBorne = true;
			Vector3d playerMotion = shooter.getMotion();
			entity.setMotion(entity.getMotion().add(playerMotion.x, shooter.isOnGround() ? 0d : playerMotion.y, playerMotion.z));

			projectileWeapon.damageItem(1, shooter, (livingEntity) -> livingEntity.sendBreakAnimation(hand));
			if (worldIn.addEntity(entity)) {
				entity.playAmbientSound();
			}
		}
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon);
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
