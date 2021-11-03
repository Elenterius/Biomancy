package com.github.elenterius.biomancy.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {

	@Accessor(value = "capNBT", remap = false)
	void biomancy_setCapNbt(CompoundNBT nbt);

}
