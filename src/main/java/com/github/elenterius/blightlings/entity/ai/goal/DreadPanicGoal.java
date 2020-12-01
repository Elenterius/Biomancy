package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.init.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.PanicGoal;

public class DreadPanicGoal extends PanicGoal
{
    public DreadPanicGoal(CreatureEntity creature, double speedIn) {
        super(creature, speedIn);
    }

    @Override
    public boolean shouldExecute() {
        return creature.isPotionActive(ModEffects.DREAD.get()) && findRandomPosition();
    }
}
