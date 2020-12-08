package com.github.elenterius.blightlings.entity;

import com.github.elenterius.blightlings.entity.ai.goal.RangedAttackWithMaxDurationGoal;
import com.github.elenterius.blightlings.entity.ai.goal.ReturnToOwnerGoal;
import com.github.elenterius.blightlings.entity.ai.goal.ThrowPotionAtPositionGoal;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PotionBeetleEntity extends CreatureEntity implements IRangedAttackMob, IThrowPotionAtPositionMob
{
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private BlockPos targetPos;

    public PotionBeetleEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 6d)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3d)
                .createMutableAttribute(Attributes.ARMOR, 2 + 5 + 6 + 2); //equal to full iron armor
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
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
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (getOwnerUUID().isPresent()) compound.putUniqueId("OwnerUUID", getOwnerUUID().get());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("OwnerUUID")) setOwnerUUID(compound.getUniqueId("OwnerUUID"));
    }

    public Optional<UUID> getOwnerUUID() {
        return dataManager.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity entity) {
        setOwnerUUID(entity.getUniqueID());
    }

    public Optional<PlayerEntity> getOwner() {
        return getOwnerUUID().map(value -> world.getPlayerByUuid(value));
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return !getLeashed() && player == getOwner().orElse(null);
    }

    @Nullable
    public BlockPos getTargetBlockPos() {
        return targetPos;
    }

    public void setTargetBlockPos(@Nullable BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    @Override
    public void setTargetPos(@Nullable IPosition position) {
        targetPos = position != null ? new BlockPos(position) : null;
    }

    @Nullable
    @Override
    public Vector3d getTargetPos() {
        return targetPos != null ? Vector3d.copyCentered(targetPos) : null;
    }

    @Override
    public boolean hasThrowablePotion() {
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        return !stack.isEmpty() && stack.getItem() instanceof ThrowablePotionItem;
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
            potionEntity.func_234612_a_(this, rotationPitch, rotationYaw, -20.0F, 0.75F, 1.0F);
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
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (player.getHeldItemMainhand().isEmpty()) {
            setDead();
            if (!player.world.isRemote()) {
                ItemStack beetleStack = new ItemStack(ModItems.POTION_BEETLE.get());
                ItemStack potionStack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                if (!potionStack.isEmpty() && potionStack.getItem() instanceof ThrowablePotionItem) {
                    Potion potion = PotionUtils.getPotionFromItem(potionStack);
                    List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(potionStack);
                    PotionUtils.addPotionToItemStack(beetleStack, potion);
                    PotionUtils.appendEffects(beetleStack, effects);
                    ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(potionStack.getItem());
                    if (registryKey != null) beetleStack.getOrCreateTag().putString("PotionItem", registryKey.toString());
                    beetleStack.getOrCreateTag().putString("PotionName", potionStack.getTranslationKey());
                }

                entityDropItem(beetleStack);
//                player.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.POTION_BEETLE.get()));
            }
            return ActionResultType.func_233537_a_(world.isRemote());
        }

        return super.func_230254_b_(player, hand);
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
