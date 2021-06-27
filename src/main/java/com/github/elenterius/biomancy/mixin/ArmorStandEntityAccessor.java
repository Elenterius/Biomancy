package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.item.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {

	@Invoker("setSmall")
	void biomancy_setSmall(boolean flag);

}
