package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(MobEntity.class)
public interface MobEntityAccessor {

	@Invoker("getAmbientSound")
	SoundEvent biomancy_getAmbientSound();

}
