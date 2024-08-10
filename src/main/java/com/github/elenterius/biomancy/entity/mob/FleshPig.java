package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.util.animation.MobAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class FleshPig extends Pig implements Enemy, GeoEntity {

	public static final Predicate<LivingEntity> PREY_SELECTOR = livingEntity -> !(livingEntity instanceof FleshPig) && !livingEntity.getType().is(ModEntityTags.FLESHKIN);

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FleshPig(EntityType<? extends Pig> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Pig.createAttributes().add(Attributes.ATTACK_DAMAGE, 0.8d);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1d, true));

		targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) return false;
		return otherAnimal instanceof Pig && isInLove() && otherAnimal.isInLove();
	}

	@Nullable
	@Override
	public Pig getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		if (otherParent.getClass() != getClass() && random.nextFloat() < 0.15f) {
			return (Pig) otherParent.getBreedOffspring(level, this);
		}

		return ModEntityTypes.FLESH_PIG.get().create(level);
	}

	@Override
	public boolean isSaddleable() {
		return false;
	}

	@Override
	public boolean isSaddled() {
		return false;
	}

	@Override
	public void equipSaddle(@Nullable SoundSource source) {
		//do nothing
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSoundEvents.FLESH_PIG_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModSoundEvents.FLESH_PIG_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.FLESH_PIG_DEATH.get();
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

}
