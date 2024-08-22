package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandAccessor {

	@Invoker("setSmall")
	void biomancy$setSmall(boolean flag);

}
