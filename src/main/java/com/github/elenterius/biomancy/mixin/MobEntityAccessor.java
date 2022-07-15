package com.github.elenterius.biomancy.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(Mob.class)
public interface MobEntityAccessor {

	@Invoker("getAmbientSound")
	SoundEvent biomancy_getAmbientSound();

}
