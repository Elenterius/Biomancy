package com.creativechasm.blightlings.entity;

import com.creativechasm.blightlings.entity.ai.goal.LookAtAndMakeNoiseGoal;
import com.creativechasm.blightlings.init.CommonRegistry;
import com.creativechasm.blightlings.init.ModSoundEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BroodmotherEntity extends SwarmGroupMemberEntity
{
    private byte timer;

    public BroodmotherEntity(EntityType<? extends BroodmotherEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233818_a_, 16.0D) //max health
                .func_233815_a_(Attributes.field_233821_d_, 0.25D) //movement speed
                .func_233815_a_(Attributes.field_233823_f_, 3.0D); //attack damage
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(6, new LookAtAndMakeNoiseGoal(this, PlayerEntity.class, 8.0F, ModSoundEvents.WAH_WAH));
    }

    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public void livingTick() {
        if (!world.isRemote() && ticksExisted % 20 == 0) {
            if (canGroupGrow() && getHealth() < getMaxHealth() * 0.75f) {
                if (++timer > 7 && getAttackTarget() != null) {
                    timer = 0;
                    spawnBlobling();
                }
            }
        }
        super.livingTick();
    }

    private void spawnBlobling() {
        BloblingEntity entity = CommonRegistry.EntityTypes.BLOBLING.create(world);
        if (entity != null) {
            Vector3d pos = getPositionVec().add(4D * rand.nextDouble() - 2D, 0.5D, 4D * rand.nextDouble() - 2D);
            entity.setLocationAndAngles(pos.x, pos.y, pos.z, rand.nextFloat() * 360.0F, 0.0F);
            entity.onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(new BlockPos(pos)), SpawnReason.REINFORCEMENT, new SwarmGroupData(this), null);

            if (isNoDespawnRequired()) {
                entity.enablePersistence();
            }
            entity.setNoAI(isAIDisabled());
            entity.setInvulnerable(isInvulnerable());
            if (hasCustomName()) {
                entity.setCustomName(new StringTextComponent("Little ").func_230529_a_(getCustomName()));
                entity.setCustomNameVisible(isCustomNameVisible());
            }

            world.addEntity(entity);
            entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH_SMALL, entity.getSoundVolume(), 1f);
        }
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return 0.28F;
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundevent = getAmbientSound();
        if (soundevent != null) {
            playSound(soundevent, getSoundVolume(), getSoundPitch() * 0.5f);
        }
    }
}
