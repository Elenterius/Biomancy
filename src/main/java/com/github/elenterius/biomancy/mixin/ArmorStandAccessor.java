package com.github.elenterius.biomancy.mixin;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandAccessor {

	@Invoker("setSmall")
	void biomancy_setSmall(boolean flag);

}
