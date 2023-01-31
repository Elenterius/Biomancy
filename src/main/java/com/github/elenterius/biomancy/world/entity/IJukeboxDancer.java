package com.github.elenterius.biomancy.world.entity;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface IJukeboxDancer {

	boolean isDancing();

	void setDancing(boolean flag);

	@Nullable BlockPos getJukeboxPos();

}
