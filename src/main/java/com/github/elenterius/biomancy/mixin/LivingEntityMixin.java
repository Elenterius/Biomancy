package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	protected ItemStack useItem;

	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	@Inject(method = "isSensitiveToWater", at = @At(value = "HEAD"), cancellable = true)
	private void onIsSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
		if (hasEffect(ModMobEffects.CORROSIVE.get())) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "blockUsingShield", at = @At(value = "HEAD"))
	private void onBlockUsingShield(LivingEntity attacker, CallbackInfo ci) {
		if (useItem.getItem() instanceof ShieldBlockingListener listener) {
			listener.onShieldBlocking(useItem, (LivingEntity) (Object) this, attacker);
		}
	}

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "HEAD"), cancellable = true)
	private void onAddEffect(MobEffectInstance effectInstance, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
		if (source instanceof AreaEffectCloud || source instanceof ThrownPotion || source instanceof Arrow) {
			// Note: ThrownPotion or AbstractArrow will only be matched if they have no owner (owner == null)
			if (!StatusEffectHandler.canApplySplashEffectIfAllowed(effectInstance.getEffect(), biomancy$self())) {
				cir.setReturnValue(false);
			}
		}
	}

	@Unique
	private LivingEntity biomancy$self() {
		return (LivingEntity) (Object) this;
	}

}
