package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
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

	@Shadow
	public abstract AttributeMap getAttributes();

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
