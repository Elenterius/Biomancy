package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface SerializableItemHandler extends IItemHandlerModifiable, INBTSerializable<CompoundTag> {

}
