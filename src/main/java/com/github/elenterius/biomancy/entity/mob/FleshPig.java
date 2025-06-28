package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.entity.mob.ai.goal.FindItemGoal;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.mixin.accessor.AnimalAccessor;
import com.github.elenterius.biomancy.util.animation.MobAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class FleshPig extends Animal implements Enemy, GeoEntity {

	protected static final Predicate<LivingEntity> PREY_SELECTOR = livingEntity -> !(livingEntity instanceof Pig) && !(livingEntity instanceof Hoglin) && !livingEntity.getType().is(ModEntityTags.FLESHKIN_IGNORES);

	protected static final Ingredient FOOD_ITEMS = Ingredient.of(ModItemTags.FRESH_RAW_MEATS);
	protected static final Predicate<ItemEntity> FOOD_ITEM_ENTITIES = itemEntity -> FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity) && itemEntity.getItem().is(ModItemTags.FRESH_RAW_MEATS);

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FleshPig(EntityType<? extends FleshPig> entityType, Level level) {
		super(entityType, level);
		setCanPickUpLoot(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10d)
				.add(Attributes.MOVEMENT_SPEED, 0.25d)
				.add(Attributes.ATTACK_DAMAGE, 0.8d);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));

		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1d, true));

		goalSelector.addGoal(3, new BreedGoal(this, 1d, Animal.class));
		goalSelector.addGoal(4, new TemptGoal(this, 1.2d, FOOD_ITEMS, false));
		goalSelector.addGoal(5, new FindItemGoal(this, 8d, 1.2d, FOOD_ITEM_ENTITIES));
		goalSelector.addGoal(5, new FollowParentGoal(this, 1.1d));

		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1d));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6f));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, PREY_SELECTOR));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, PREY_SELECTOR));
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ModItemTags.FRESH_RAW_MEATS);
	}

	@Override
	public boolean canPickUpLoot() {
		return !isInLove() && !hasEffect(ModMobEffects.FRENZY.get()) && super.canPickUpLoot();
	}

	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		return !isInLove() && super.wantsToPickUp(stack);
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		return isFood(stack);
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			onItemPickup(itemEntity);
			take(itemEntity, 1);

			int foodNutrition = getFoodNutrition(stack);
			boolean isPork = stack.is(ModItemTags.C_RAW_PORK);

			stack.shrink(1);
			if (stack.isEmpty()) {
				itemEntity.discard();
			}

			if (isPork) {
				addEffect(new MobEffectInstance(ModMobEffects.FRENZY.get(), 20 * 60, 0));
			}
			else if (getHealth() < getMaxHealth()) {
				heal(foodNutrition * 0.5f);
			}
			else if (isBaby()) {
				ageUp(getSpeedUpSecondsWhenFeeding(getAge()) + foodNutrition, true);
			}
			else {
				setInLoveTime(600 * Mth.clamp(foodNutrition, 4, 20));
				level().broadcastEntityEvent(this, (byte) 18);
			}
		}
	}

	protected int getFoodNutrition(ItemStack stack) {
		if (!stack.isEdible()) return 0;

		FoodProperties foodProperties = stack.getFoodProperties(this);
		return foodProperties == null ? 0 : foodProperties.getNutrition();
	}

	public void setInLove(@Nullable Player player) {
		setInLoveTime(600 * 4);
		if (player != null) {
			((AnimalAccessor) this).biomancy$setLoveCause(player.getUUID());
		}
		level().broadcastEntityEvent(this, (byte) 18);
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) return false;

		boolean isValidMate = otherAnimal instanceof FleshPig || otherAnimal instanceof Pig || otherAnimal instanceof Hoglin;
		return isValidMate && isInLove() && otherAnimal.isInLove();
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		if (otherParent.getClass() != getClass() && random.nextFloat() <= 0.15f) {
			return otherParent.getBreedOffspring(level, this);
		}

		return ModEntityTypes.FLESH_PIG.get().create(level);
	}

	@Override
	public Vec3 getLeashOffset() {
		return new Vec3(0d, 0.6d * getEyeHeight(), getBbWidth() * 0.4d);
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
	protected void playStepSound(BlockPos pos, BlockState block) {
		playSound(SoundEvents.PIG_STEP, 0.15f, 1f);
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
