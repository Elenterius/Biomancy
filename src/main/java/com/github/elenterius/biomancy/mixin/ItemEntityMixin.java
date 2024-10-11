package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.AcidInteractions;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Inject(at = @At("TAIL"), method = {"tick()V"})
	private void onTick(CallbackInfo ci) {
		ItemEntity itemEntity = (ItemEntity) (Object) this; //I hate casting like this on so many levels -Kd
		if (itemEntity.isRemoved()) return;
		AcidInteractions.InWorldItemDigesting.tryDigestSubmergedItem(itemEntity);
	}

}