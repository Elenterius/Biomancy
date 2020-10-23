package com.creativechasm.blightlings.entity;

import com.creativechasm.blightlings.entity.ai.goal.FollowSwarmLeaderGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SwarmGroupMemberEntity extends SpiderEntity implements ISwarmGroupMember<SwarmGroupMemberEntity>
{
    private ISwarmGroupMember<?> leader;
    private int groupSize = 1;

    public SwarmGroupMemberEntity(EntityType<? extends SpiderEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new FollowSwarmLeaderGoal(this));
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ModifiableAttributeInstance attribute = getAttribute(Attributes.field_233819_b_);
        if (attribute != null) {
            attribute.func_233769_c_(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
        }

        if (spawnDataIn == null) {
            spawnDataIn = new SwarmGroupData(this);
        }
        else {
            joinGroup(((SwarmGroupData) spawnDataIn).leader);
        }

        return spawnDataIn;
    }

    @Override
    public SwarmGroupMemberEntity asMobEntity() {
        return this;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return getMaxGroupSize();
    }

    @Override
    public int getGroupSize() {
        return groupSize;
    }

    @Override
    public int getMaxGroupSize() {
        return 8;
    }

    @Override
    public ISwarmGroupMember<?> getLeader() {
        return leader;
    }

    @Override
    public void setLeader(@Nullable ISwarmGroupMember<?> groupLeader) {
        leader = groupLeader;
    }

    @Override
    public void increaseGroupSize() {
        ++groupSize;
    }

    @Override
    public void decreaseGroupSize() {
        --groupSize;
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entityIn) {
        super.setAttackTarget(entityIn);
        if (entityIn != null && entityIn.isAlive() && hasLeader() && getLeader().asMobEntity().getAttackTarget() == null) {
            getLeader().asMobEntity().setAttackTarget(entityIn);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isLeader() && world.rand.nextInt(200) == 1) {
            List<SwarmGroupMemberEntity> list = world.getEntitiesWithinAABB(getClass(), getBoundingBox().grow(8.0D, 8.0D, 8.0D));
            if (list.size() <= 1) groupSize = 1;
        }
    }
}