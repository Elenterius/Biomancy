package com.creativechasm.blightlings.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BloblingEntity extends SwarmGroupMemberEntity
{

    public BloblingEntity(EntityType<? extends BloblingEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233818_a_, 5.0D) //max health
                .func_233815_a_(Attributes.field_233821_d_, 0.2D) //movement speed
                .func_233815_a_(Attributes.field_233823_f_, 1.0D); //attack damage
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    }

    @Override
    public boolean inRangeOfLeader(SwarmGroupMemberEntity leader) {
        double dist = getDistanceSq(leader);
        return dist >= 9D && dist <= 256D;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote && !cause.isUnblockable()) {
            Explosion.Mode mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 0.75f, mode);
        }
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return 0.16F;
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
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SLIME_HURT_SMALL;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SLIME_DEATH_SMALL;
    }
}
