package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.entity.projectile.AcidSpitProjectile;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModDamageTypeTags;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.util.animation.MobAnimations;
import com.github.elenterius.biomancy.util.shooting.ConfiguredProjectile;
import com.github.elenterius.biomancy.util.shooting.ProjectileUtil;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class FleshChicken extends Chicken implements RangedAttackMob, GeoEntity {

	public static final Predicate<LivingEntity> TARGET_SELECTOR = livingEntity -> livingEntity.getMobType() == MobType.UNDEAD;
	public static final ConfiguredProjectile<AcidSpitProjectile> GASTRIC_SPIT = new ConfiguredProjectile<>(1.5f, 1, 0, 0.8f, ModSoundEvents.ACID_SPIT.get(), AcidSpitProjectile::new);

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
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, false, TARGET_SELECTOR));
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return super.isInvulnerableTo(source) || source.is(ModDamageTypeTags.FORGE_IS_ACID);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effectInstance) {
		if (ModMobEffectTags.forgeIsAcid(effectInstance.getEffect())) return false;
		return super.canBeAffected(effectInstance);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float velocity) {

		//TODO: add local offset handling for ProjectileUtil#shoot method variant with origin and target pos
		double x = getX() - getBbWidth() * Mth.sin(yBodyRot * Mth.DEG_TO_RAD);
		double y = getEyeY() + 0.25d;
		double z = getZ() + getBbWidth() * Mth.cos(yBodyRot * Mth.DEG_TO_RAD);

		Vec3 origin = new Vec3(x, y, z);
		if (ProjectileUtil.shoot(level(), this, GASTRIC_SPIT, origin, target.getEyePosition())) {
			level().playSound(null, origin.x, origin.y, origin.z, GASTRIC_SPIT.shootSound(), SoundUtil.soundSourceFor(this), 0.8f, 0.4f);
		}

		hasAttacked = true;
	}

	@Nullable
	@Override
	public ItemEntity spawnAtLocation(ItemLike item) {
		if (item == Items.EGG) {
			if (level() instanceof ServerLevel serverLevel) {
				if (random.nextFloat() <= 0.4f) {
					ModBlocks.ACID_SPLATTER.get().placeSmallSplatter(serverLevel, blockPosition(), Direction.UP, random);
					return null;
				}
				else {
					return super.spawnAtLocation(ModItems.ACIDIC_EGG.get());
				}
			}
			return null;
		}

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
