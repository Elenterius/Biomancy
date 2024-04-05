package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModDamageTypes;
import com.github.elenterius.biomancy.item.ItemAttackDamageSourceProvider;
import com.github.elenterius.biomancy.item.SweepAttackListener;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(method = "hurtArmor", at = @At(value = "HEAD"), argsOnly = true)
	private float modifyArmorDamage(float damage, DamageSource damageSource) {
		return damageSource.is(ModDamageTypes.CORROSIVE_ACID) ? damage * 1.2f : damage;
	}

	@Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
	private void onSweepAttack(CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (player.getMainHandItem().getItem() instanceof SweepAttackListener listener && listener.onSweepAttack(player.level(), player)) {
			ci.cancel();
		}
	}

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private Entity saveAttackTarget(Entity instance, DamageSource source, float damageAmount, @Share("target") LocalRef<Entity> argRef) {
		argRef.set(instance);
		return instance;
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
	private DamageSource adjustAttackDamageSource(DamageSource source, @Share("target") LocalRef<Entity> argRef) {
		ItemStack stack = getMainHandItem();
		if (stack.getItem() instanceof ItemAttackDamageSourceProvider damageSourceProvider) {
			Entity target = argRef.get();
			if (target != null) {
				DamageSource damageSource = damageSourceProvider.getDamageSource(stack, target, this);
				return damageSource != null ? damageSource : source;
			}
		}
		return source;
	}

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private LivingEntity saveSweepAttackTarget(LivingEntity instance, DamageSource damageSource, float damageAmount, @Share("sweepTarget") LocalRef<LivingEntity> argRef) {
		argRef.set(instance);
		return instance;
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
	private DamageSource adjustSweepAttackDamageSource(DamageSource source, @Share("sweepTarget") LocalRef<LivingEntity> argRef) {
		ItemStack stack = getMainHandItem();
		if (stack.getItem() instanceof ItemAttackDamageSourceProvider damageSourceProvider) {
			LivingEntity target = argRef.get();
			if (target != null) {
				DamageSource damageSource = damageSourceProvider.getDamageSource(stack, target, this);
				return damageSource != null ? damageSource : source;
			}
		}
		return source;
	}

	//	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	//	private Entity onAttackModifyHurtTarget(Entity instance, DamageSource source, float damageAmount) {
	//		if (instance instanceof LivingEntity && getMainHandItem().getItem() instanceof InvincibleFrameIgnoringAttackItem) {
	//			instance.invulnerableTime = 0;
	//		}
	//		return instance;
	//	}
	//
	//	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	//	private LivingEntity onAttackModifyHurtSweepTarget(LivingEntity instance, DamageSource damageSource, float damageAmount) {
	//		if (getMainHandItem().getItem() instanceof InvincibleFrameIgnoringAttackItem) {
	//			instance.invulnerableTime = 0;
	//		}
	//		return instance;
	//	}

}
