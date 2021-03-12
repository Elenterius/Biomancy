package com.github.elenterius.biomancy.entity.golem;

import com.github.elenterius.biomancy.entity.IThrowPotionAtPositionMob;
import com.github.elenterius.biomancy.entity.ai.goal.RangedAttackWithMaxDurationGoal;
import com.github.elenterius.biomancy.entity.ai.goal.ThrowPotionAtPositionGoal;
import com.github.elenterius.biomancy.entity.ai.goal.golem.ReturnToOwnerGoal;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class PotionBeetleEntity extends OwnableCreatureEntity implements IRangedAttackMob, IThrowPotionAtPositionMob {
	public PotionBeetleEntity(EntityType<? extends OwnableCreatureEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 6.5d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3d)
				.createMutableAttribute(Attributes.ARMOR, 2 + 5 + 6 + 2) //equal to full iron armor
				.createMutableAttribute(Attributes.ARMOR_TOUGHNESS, 1.5d);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new ThrowPotionAtPositionGoal(this, 1d));
		goalSelector.addGoal(2, new RangedAttackWithMaxDurationGoal(this, 1d, 20, 3.5F, 20 * 30));
		goalSelector.addGoal(3, new ReturnToOwnerGoal(this, 1d));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	@Override
	public void setTargetPos(@Nullable IPosition position) {
		setTargetBlockPos(position != null ? new BlockPos(position) : null);
	}

	@Nullable
	@Override
	public Vector3d getTargetPos() {
		return getTargetBlockPos() != null ? Vector3d.copyCentered(getTargetBlockPos()) : null;
	}

	@Override
	public boolean tryToThrowPotionAtPosition(Vector3d targetPos) {
		ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty() && stack.getItem() instanceof ThrowablePotionItem) {
			double x = targetPos.x - getPosX();
			double y = targetPos.y - 1.1d - getPosY();
			double z = targetPos.z - getPosZ();
			float magnitude = MathHelper.sqrt(x * x + z * z);

			PotionEntity potionEntity = new PotionEntity(world, this);
			potionEntity.setItem(stack);
			potionEntity.rotationPitch -= 20.0F;
			potionEntity.shoot(x, y + magnitude * 0.25F, z, 0.9F, 1.0F);
			if (!isSilent()) {
				world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_WITCH_THROW, getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
			}
			world.addEntity(potionEntity);

			stack.shrink(1);
			return true;
		}
		return false;
	}

	public boolean tryToThrowPotionInLookDirection() {
		ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty() && stack.getItem() instanceof ThrowablePotionItem) {
			PotionEntity potionEntity = new PotionEntity(world, this);
			potionEntity.setItem(stack);
			potionEntity.setDirectionAndMovement(this, rotationPitch, rotationYaw, -20.0F, 0.75F, 1.0F);
			if (!isSilent()) {
				world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_WITCH_THROW, getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
			}
			world.addEntity(potionEntity);
			stack.shrink(1);
			return true;
		}
		return false;
	}

	@Override
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty() && stack.getItem() instanceof ThrowablePotionItem) {
			Vector3d targetMotion = target.getMotion();
			double x = target.getPosX() + targetMotion.x - getPosX();
			double y = target.getPosYEye() - 1.1d - getPosY();
			double z = target.getPosZ() + targetMotion.z - getPosZ();
			float magnitude = MathHelper.sqrt(x * x + z * z);

			PotionEntity potionEntity = new PotionEntity(world, this);
			potionEntity.setItem(stack);
			potionEntity.rotationPitch -= 20.0F;
			potionEntity.shoot(x, y + magnitude * 0.25F, z, 0.9F, 1.0F);
			if (!isSilent()) {
				world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_WITCH_THROW, getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
			}
			world.addEntity(potionEntity);

			stack.shrink(1);
			setAttackTarget(null);
			getNavigator().clearPath();
		}
	}

	@Override
	protected void dropInventory() {
		ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty()) {
			setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
			entityDropItem(stack);
		}
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		if (world instanceof ServerWorld) {
			ServerPlayerEntity player = (ServerPlayerEntity) getOwner().orElse(null);
			if (player == null) return false;

			ItemStack beetleStack = ModItems.POTION_BEETLE.get().setPotionItemStack(new ItemStack(ModItems.POTION_BEETLE.get()), getPotionItemStack().copy());
			if (hasCustomName()) beetleStack.setDisplayName(getCustomName());
			if (player.addItemStackToInventory(beetleStack)) {
				setPotionItemStack(ItemStack.EMPTY);
				setDead();
				return true;
			}
		}
		return false;
	}

	@Override
	public void setPotionItemStack(ItemStack stack) {
		setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
	}

	@Override
	public ItemStack getPotionItemStack() {
		return getItemStackFromSlot(EquipmentSlotType.MAINHAND);
	}

	@Override
	protected ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
		if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof LeadItem) return ActionResultType.PASS;

		if (!player.world.isRemote()) {
			setDead();
			ItemStack beetleStack = ModItems.POTION_BEETLE.get().setPotionItemStack(new ItemStack(ModItems.POTION_BEETLE.get()), getPotionItemStack().copy());
			if (hasCustomName()) beetleStack.setDisplayName(getCustomName());
			if (player.addItemStackToInventory(beetleStack)) {
				setPotionItemStack(ItemStack.EMPTY);
			}
			else {
				entityDropItem(beetleStack);
			}
		}
		return ActionResultType.func_233537_a_(world.isRemote());
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
