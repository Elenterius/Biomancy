package com.github.elenterius.blightlings.entity;

import com.github.elenterius.blightlings.entity.ai.goal.PlaceBlockAtPositionGoal;
import com.github.elenterius.blightlings.entity.ai.goal.ReturnToOwnerGoal;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.util.BlockPlacementTarget;
import com.github.elenterius.blightlings.util.PlayerInteractionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class MasonBeetleEntity extends AbstractUtilityEntity implements IPlaceBlockAtPositionMob {

	private BlockPlacementTarget blockPlacementTarget = null;

	public MasonBeetleEntity(EntityType<? extends AbstractUtilityEntity> type, World worldIn) {
		super(type, worldIn);
		moveController = new FlyingMovementController(this, 20, true);
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 12d)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.4d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2d)
				.createMutableAttribute(Attributes.ARMOR, 2 + 5 + 6 + 2) //equal to full iron armor
				.createMutableAttribute(Attributes.ARMOR_TOUGHNESS, 0.5d);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new PlaceBlockAtPositionGoal(this, 1d));
		goalSelector.addGoal(3, new ReturnToOwnerGoal(this, 1d));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	@Override
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(true);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	protected void dropInventory() {
		ItemStack stack = getPlacementBlock();
		if (!stack.isEmpty()) {
			setPlacementBlock(ItemStack.EMPTY);
			entityDropItem(stack);
		}
	}

	@Override
	public boolean tryToPlaceBlockAtPosition(BlockRayTraceResult rayTraceResult, Direction horizontalFacing) {
		if (world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) world;
			ServerPlayerEntity player = (ServerPlayerEntity) getOwner().orElse(null);
			if (player == null) return false;

			BlockPos blockPos = rayTraceResult.getPos();
			if (blockPos.getY() < serverWorld.getServer().getBuildLimit()) {
				ItemStack stack = getPlacementBlock();
				if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && !stack.getItem().isFood()) {
					if (serverWorld.isBlockModifiable(player, blockPos)) {
						String blockTranslation = stack.getTranslationKey();
						Direction direction = rayTraceResult.getFace();
						PlayerInteractionUtil.PlayerSurrogate surrogate = PlayerInteractionUtil.PlayerSurrogate.of(player, this);
						ActionResultType actionResultType = PlayerInteractionUtil.tryToPlaceBlock(surrogate, stack, Hand.MAIN_HAND, rayTraceResult, horizontalFacing);
						if (direction == Direction.UP && !actionResultType.isSuccessOrConsume() && blockPos.getY() >= serverWorld.getServer().getBuildLimit() - 1 && isValidItem(surrogate, stack)) {
							ITextComponent textComponent = new TranslationTextComponent("build.tooHigh", serverWorld.getServer().getBuildLimit()).mergeStyle(TextFormatting.RED);
							player.sendStatusMessage(textComponent, true);
						} else if (actionResultType.isSuccessOrConsume()) {
							ITextComponent blockName = new TranslationTextComponent(blockTranslation);
							ITextComponent beetle_name = hasCustomName() ? getCustomName() : new TranslationTextComponent("msg.blightlings.your_beetle");
							ITextComponent textComponent = new TranslationTextComponent("msg.blightlings.beetle_block_place_success", beetle_name, blockName).mergeStyle(TextFormatting.GREEN);
							player.sendStatusMessage(textComponent, true);
							return true;
						}
					}
				}
			} else {
				ITextComponent textComponent = (new TranslationTextComponent("build.tooHigh", serverWorld.getServer().getBuildLimit())).mergeStyle(TextFormatting.RED);
				player.sendStatusMessage(textComponent, true);
			}
		}
		return false;
	}

	private static boolean isValidItem(ServerPlayerEntity player, ItemStack stack) {
		if (stack.isEmpty()) return false;
		Item item = stack.getItem();
		return (item instanceof BlockItem || item instanceof BucketItem) && !player.getCooldownTracker().hasCooldown(item);
	}

	@Override
	public boolean hasPlaceableBlock() {
		ItemStack stack = getPlacementBlock();
		return !stack.isEmpty() && stack.getItem() instanceof BlockItem;
	}

	@Override
	public void setPlacementBlock(ItemStack stack) {
		setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
	}

	@Override
	public ItemStack getPlacementBlock() {
		return getItemStackFromSlot(EquipmentSlotType.MAINHAND);
	}

	@Nullable
	@Override
	public BlockPlacementTarget getBlockPlacementTarget() {
		return blockPlacementTarget;
	}

	@Override
	public void setBlockPlacementTarget(@Nullable BlockPlacementTarget placementTarget) {
		blockPlacementTarget = placementTarget;
		setTargetBlockPos(placementTarget != null ? new BlockPos(placementTarget.targetPos) : null);
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		if (world instanceof ServerWorld) {
			ServerPlayerEntity player = (ServerPlayerEntity) getOwner().orElse(null);
			if (player == null) return false;

			ItemStack beetleStack = ModItems.MASON_BEETLE.get().setBlockItemStack(new ItemStack(ModItems.MASON_BEETLE.get()), getPlacementBlock().copy());
			if (hasCustomName()) beetleStack.setDisplayName(getCustomName());
			if (player.addItemStackToInventory(beetleStack)) {
				setPlacementBlock(ItemStack.EMPTY);
				setDead();
				return true;
			}
		}
		return false;
	}

	@Override
	protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof LeadItem) return ActionResultType.PASS;

		if (!player.world.isRemote()) {
			setDead();
			ItemStack beetleStack = ModItems.MASON_BEETLE.get().setBlockItemStack(new ItemStack(ModItems.MASON_BEETLE.get()), getPlacementBlock().copy());
			if (hasCustomName()) beetleStack.setDisplayName(getCustomName());
			if (player.addItemStackToInventory(beetleStack)) {
				setPlacementBlock(ItemStack.EMPTY);
			} else {
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
