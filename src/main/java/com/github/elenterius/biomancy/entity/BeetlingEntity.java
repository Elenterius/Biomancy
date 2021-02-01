package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class BeetlingEntity extends AnimalEntity {
	public BeetlingEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 5d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2d)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 1d)
				.createMutableAttribute(Attributes.ARMOR, 2 + 5 + 6 + 2); //equal to full iron armor
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new PanicGoal(this, 1.9D));
		goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
		goalSelector.addGoal(3, new TemptGoal(this, 1.15D, Ingredient.fromItems(ModItems.ERODING_BILE.get()), true));
		goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 5.0F));
		goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	@Nullable
	@Override
	public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity ageableEntity) {
		return ModEntityTypes.BEETLING.get().create(world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == ModItems.ERODING_BILE.get();
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4f;
	}

	@Override
	public void playAmbientSound() {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getSoundPitch() * 1.2f);
		}
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		livingSoundTime = -getTalkInterval();
		SoundEvent soundevent = getHurtSound(source);
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getSoundPitch() * 1.2f);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SPIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SPIDER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, getSoundPitch());
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return isChild() ? sizeIn.height * 0.16F : 0.16f;
	}
}
