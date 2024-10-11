package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	protected ItemStack useItem;

	private LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	@Shadow
	public abstract AttributeMap getAttributes();

	@Shadow
	@Final
	private Map<MobEffect, MobEffectInstance> activeEffects;

	@Shadow
	public abstract Collection<MobEffectInstance> getActiveEffects();

	@Inject(method = "getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", at = @At("HEAD"), cancellable = true)
	protected void onGetAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
		if (attribute == Attributes.ATTACK_DAMAGE && !getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) && hasEffect(ModMobEffects.FRENZY.get())) {
			cir.setReturnValue(FrenzySerum.ATTACK_DAMAGE_FALLBACK);
		}
	}

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

	@Inject(method = "addEatEffect", at = @At(value = "TAIL"))
	private void onAddEatEffect(ItemStack food, Level level, LivingEntity livingEntity, CallbackInfo ci) {
		if (!level.isClientSide && biomancy$getRawMeatNutrition(food) > 2 && livingEntity.getRandom().nextFloat() < 0.2f) {
			livingEntity.addEffect(new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION.get(), 20 * 8, 0));
		}
	}

	@ModifyArg(
			method = "curePotionEffects",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V"),
			remap = false
	)
	private MobEffectInstance onCurePotionEffects(MobEffectInstance effectInstance, @Share("removedFrenzy") LocalRef<MobEffectInstance> removedFrenzyRef) {
		if (effectInstance.getEffect() == ModMobEffects.FRENZY.get()) {
			removedFrenzyRef.set(effectInstance);
		}
		return effectInstance;
	}

	@Inject(method = "curePotionEffects", at = @At(value = "TAIL"), remap = false)
	private void onPostCurePotionEffects(ItemStack curativeItem, CallbackInfoReturnable<Boolean> cir, @Share("removedFrenzy") LocalRef<MobEffectInstance> removedFrenzyRef) {
		if (level().isClientSide) return;

		MobEffectInstance removedFrenzyEffect = removedFrenzyRef.get();
		if (removedFrenzyEffect == null) return;

		StatusEffectHandler.addWithdrawalAfterFrenzy(biomancy$self(), removedFrenzyEffect);
	}

	@ModifyArg(
			method = "tickEffects",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
	)
	private MobEffectInstance onTickEffects(MobEffectInstance effectInstance, @Share("expiredFrenzy") LocalRef<MobEffectInstance> expiredFrenzyRef) {
		if (effectInstance.getEffect() == ModMobEffects.FRENZY.get()) {
			expiredFrenzyRef.set(effectInstance);
		}
		return effectInstance;
	}

	@Inject(method = "tickEffects", at = @At(value = "TAIL"))
	private void onPostTickEffects(CallbackInfo ci, @Share("expiredFrenzy") LocalRef<MobEffectInstance> expiredFrenzyRef) {
		if (level().isClientSide) return;

		MobEffectInstance removedFrenzyEffect = expiredFrenzyRef.get();
		if (removedFrenzyEffect == null) return;

		StatusEffectHandler.addWithdrawalAfterFrenzy(biomancy$self(), removedFrenzyEffect);
	}

	@Unique
	private static int biomancy$getRawMeatNutrition(ItemStack itemStack) {
		if (!itemStack.isEdible()) return 0;
		FoodProperties food = itemStack.getFoodProperties(null);
		return food != null && food.isMeat() && itemStack.is(ModItemTags.RAW_MEATS) ? food.getNutrition() : 0;
	}

	@Unique
	private LivingEntity biomancy$self() {
		return (LivingEntity) (Object) this;
	}

}
