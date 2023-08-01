package com.github.elenterius.biomancy.mixin;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSource.class)
public interface DamageSourceAccessor {

	@Mutable
	@Accessor("msgId")
	void biomancy$setMsgId(String id);

}
