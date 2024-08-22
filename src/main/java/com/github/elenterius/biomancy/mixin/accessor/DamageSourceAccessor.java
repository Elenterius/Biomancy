package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSource.class)
public interface DamageSourceAccessor {

	@Mutable
	@Accessor("type")
	void biomancy$setDamageType(Holder<DamageType> type);

}
