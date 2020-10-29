package com.creativechasm.blightlings.entity;

import net.minecraft.entity.MobEntity;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public interface ISwarmGroupMember<T extends MobEntity>
{
    T asMobEntity();

    int getGroupSize();

    int getMaxGroupSize();

    default boolean hasNoLeader() {
        return !hasLeader();
    }

    default boolean hasLeader() {
        return getLeader() != null && getLeader().asMobEntity().isAlive();
    }

    @Nullable
    ISwarmGroupMember<?> getLeader();

    default boolean isLeader() {
        return getGroupSize() > 1;
    }

    void setLeader(@Nullable T groupLeader);

    default T joinGroup(T groupLeader) {
        setLeader(groupLeader);
        ((ISwarmGroupMember<?>) groupLeader).increaseGroupSize();
        return groupLeader;
    }

    default void leaveGroup() {
        getLeader().decreaseGroupSize();
        setLeader(null);
    }

    void increaseGroupSize();

    void decreaseGroupSize();

    default boolean canGroupGrow() {
        return isLeader() && getGroupSize() < getMaxGroupSize();
    }

    default boolean inRangeOfLeader(T leader) {
        return asMobEntity().getDistanceSq(leader) <= 128D;
    }

    default void moveToLeader() {
        if (hasLeader()) asMobEntity().getNavigator().tryMoveToEntityLiving(getLeader().asMobEntity(), 1.0D);
    }

    default void addGroupMembers(Stream<T> entityStream) {
        entityStream
                .limit(getMaxGroupSize() - getGroupSize())
                .filter((entity) -> entity != this.asMobEntity())
                .forEach((entity) -> ((ISwarmGroupMember<T>) entity).joinGroup((T) this));
    }
}
