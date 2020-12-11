package com.github.elenterius.blightlings.entity;

import com.github.elenterius.blightlings.entity.ai.goal.PlaceBlockAtPositionGoal;
import com.github.elenterius.blightlings.entity.ai.goal.ReturnToOwnerGoal;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public boolean tryToPlaceBlockAtPosition(BlockRayTraceResult rayTraceResult) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            ServerPlayerEntity player = (ServerPlayerEntity) getOwner().orElse(null);
            if (player == null) return false;

            BlockPos blockPos = rayTraceResult.getPos();
            if (blockPos.getY() < serverWorld.getServer().getBuildLimit()) {
                ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && !stack.getItem().isFood()) {
                    if (serverWorld.isBlockModifiable(player, blockPos)) {
                        Direction direction = rayTraceResult.getFace();
                        PlayerInteractionUtil.PlayerSurrogate.of(player, this);
                        ActionResultType actionResultType = PlayerInteractionUtil.tryToPlaceBlock(player, stack, Hand.MAIN_HAND, rayTraceResult);
                        if (direction == Direction.UP && !actionResultType.isSuccessOrConsume() && blockPos.getY() >= serverWorld.getServer().getBuildLimit() - 1 && isValidItem(player, stack)) {
                            ITextComponent textComponent = (new TranslationTextComponent("build.tooHigh", serverWorld.getServer().getBuildLimit())).mergeStyle(TextFormatting.RED);
                            player.sendStatusMessage(textComponent, true);
                        } else if (actionResultType.isSuccess()) {
                            //do something like swing an arm
                            return true;
                        }
                        System.out.println(actionResultType);
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
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem;
    }

    @Override
    public void setPlacementBlock(ItemStack stack) {
        setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
    }

    @Nullable
    @Override
    public BlockPos getBlockPlacementPos() {
        return getTargetBlockPos();
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
