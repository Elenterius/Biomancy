package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(Mob.class)
public interface MobEntityAccessor {

	@Invoker("getAmbientSound")
	SoundEvent biomancy$getAmbientSound();

	@Accessor("jumpControl")
	void biomancy$setJumpControl(JumpControl jumpControl);

	@Accessor("moveControl")
	void biomancy$setMoveControl(MoveControl jumpControl);

}
