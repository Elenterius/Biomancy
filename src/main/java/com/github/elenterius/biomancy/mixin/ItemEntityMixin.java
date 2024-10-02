package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.AcidInteractions;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Unique
	@Inject(at=@At("TAIL"),method={"tick()V"})
	public void biomancy$tick(CallbackInfo ci) {
		ItemEntity self = (ItemEntity)((Object)this); //I hate casting like this on so many levels
		if (self.level().isClientSide()) return;
		AcidInteractions.tryDigest(self);
	}
}