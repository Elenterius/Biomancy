package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModDamageTypes;
import com.github.elenterius.biomancy.item.SweepAttackListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

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
}
