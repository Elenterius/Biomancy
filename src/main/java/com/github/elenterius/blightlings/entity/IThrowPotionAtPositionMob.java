package com.github.elenterius.blightlings.entity;

import net.minecraft.dispenser.IPosition;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface IThrowPotionAtPositionMob
{
    boolean tryToThrowPotionAtPosition(Vector3d targetPos);

    boolean hasThrowablePotion();

    @Nullable
    Vector3d getTargetPos();

    void setTargetPos(@Nullable IPosition position);
}
