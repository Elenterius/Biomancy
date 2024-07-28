package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.util.animation.MobAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FleshChicken extends Chicken implements RangedAttackMob, GeoEntity {

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	boolean hasAttacked;

	public FleshChicken(EntityType<? extends Chicken> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25d, 40, 20f));
		targetSelector.addGoal(1, new FleshChickenHurtByTargetGoal(this).setAlertOthers());
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return super.isInvulnerableTo(source) || source.is(ModDamageTypes.CORROSIVE_ACID);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float velocity) {

		double x = getX() - getBbWidth() * Mth.sin(yBodyRot * Mth.DEG_TO_RAD);
		double y = getEyeY() + 0.25d;
		double z = getZ() + getBbWidth() * Mth.cos(yBodyRot * Mth.DEG_TO_RAD);

		ModProjectiles.GASTRIC_SPIT.shoot(level(), new Vec3(x, y, z), target.getEyePosition());
		hasAttacked = true;
	}

	@Nullable
	@Override
	public ItemEntity spawnAtLocation(ItemLike item) {
		if (item == Items.EGG) item = ModItems.BILE.get();
		return super.spawnAtLocation(item);
	}

	@Nullable
	@Override
	public Chicken getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		if (otherParent.getClass() != getClass() && random.nextFloat() < 0.15f) {
			return (Chicken) otherParent.getBreedOffspring(level, this);
		}

		return ModEntityTypes.FLESH_CHICKEN.get().create(level);
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) return false;
		return otherAnimal instanceof Chicken && isInLove() && otherAnimal.isInLove();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSoundEvents.FLESH_CHICKEN_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModSoundEvents.FLESH_CHICKEN_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.FLESH_CHICKEN_DEATH.get();
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(MobAnimations.walkController(this));
		controllers.add(MobAnimations.babyTransformController(this));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	static class FleshChickenHurtByTargetGoal extends HurtByTargetGoal {

		public FleshChickenHurtByTargetGoal(FleshChicken fleshChicken) {
			super(fleshChicken);
		}

		public boolean canContinueToUse() {
			if (mob instanceof FleshChicken chicken) {
				if (chicken.hasAttacked) {
					chicken.hasAttacked = false;
					return false;
				}
			}

			return super.canContinueToUse();
		}

	}

}
