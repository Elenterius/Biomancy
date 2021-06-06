package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.capabilities.IItemDecayTracker;
import com.github.elenterius.biomancy.init.ModCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CapabilityProvider.class)
public abstract class CapabilityProviderMixin {

	/**
	 * hack forge to allow stacking of Items that have a decay capability even though their decay nbt-cap data differs
	 */
	@Inject(method = "areCapsCompatible(Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z", at = @At("HEAD"), remap = false, cancellable = true)
	protected void biomancy_onAreCapsCompatible(CapabilityProvider<?> other, CallbackInfoReturnable<Boolean> cir) {
		Object _this = this;
		//noinspection ConstantConditions
		if (_this instanceof ItemStack && other instanceof ItemStack) {
			if (((ItemStack) _this).getItem() == ((ItemStack) other).getItem()) {
				LazyOptional<IItemDecayTracker> capA = ((ItemStack) _this).getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
				LazyOptional<IItemDecayTracker> capB = other.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
				if (capA.isPresent() && capB.isPresent()) {
					cir.setReturnValue(true);
				}
			}
		}
	}

}
