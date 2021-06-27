package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.monster.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SlimeEntity.class)
public interface SlimeEntityAccessor {

	@Invoker("setSlimeSize")
	void biomancy_setSlimeSize(int size, boolean resetHealth);

}
