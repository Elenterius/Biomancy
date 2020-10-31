package com.github.elenterius.blightlings.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BloblingEntity extends SwarmGroupMemberEntity
{

    public BloblingEntity(EntityType<? extends BloblingEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 5.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
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
        if (!world.isRemote && !cause.isMagicDamage() && !cause.isFireDamage() && !cause.isExplosion()) {
            Vector3d pos = getPositionVec();
            AreaEffectCloudEntity aoeCloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
            aoeCloud.setDuration(250);
            aoeCloud.setRadius(1.25F);
            aoeCloud.setRadiusOnUse(-0.5F);
            aoeCloud.setWaitTime(10);
            aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / (float) aoeCloud.getDuration());
            aoeCloud.setPotion(rand.nextBoolean() ? Potions.HARMING : Potions.WEAKNESS);
            aoeCloud.setColor(0xff5eeb);
            world.addEntity(aoeCloud);
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
