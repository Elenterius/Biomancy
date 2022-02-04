package com.github.elenterius.biomancy.mixin;

import net.minecraft.data.models.model.TextureSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nonnull;

@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {

	@Nonnull
	@Invoker
	static TextureSlot callCreate(String id) {return null;}

}
