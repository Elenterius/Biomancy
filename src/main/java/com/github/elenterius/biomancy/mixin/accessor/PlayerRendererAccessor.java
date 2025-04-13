package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRenderer.class)
public interface PlayerRendererAccessor {
	@Invoker("setModelProperties")
	void biomancy$SetModelProperties(AbstractClientPlayer player);
}
