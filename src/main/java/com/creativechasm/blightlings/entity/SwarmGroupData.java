package com.creativechasm.blightlings.entity;

import net.minecraft.entity.ILivingEntityData;

public class SwarmGroupData implements ILivingEntityData
{
    public final ISwarmGroupMember<?> leader;

    public SwarmGroupData(ISwarmGroupMember<?> leader) {
        this.leader = leader;
    }
}
