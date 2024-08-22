package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.data.models.model.TextureSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nonnull;

@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {

	@Nonnull
	@Invoker("create")
	static TextureSlot biomancy$create(String id) {return null;}

}
