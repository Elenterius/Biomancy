package com.github.elenterius.biomancy.mixin.client;

import net.minecraft.client.renderer.entity.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

	//TODO: this will probably also break with optifine
//	@Inject(method = "func_241741_a_", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel$ArmPose;ITEM:Lnet/minecraft/client/renderer/entity/model/BipedModel$ArmPose;"))
//	private static void biomancy_onGetArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedModel.ArmPose> cir, ItemStack itemstack) {
//		if (!player.isSwingInProgress && itemstack.getItem() instanceof ProjectileWeaponItem && ((ProjectileWeaponItem) itemstack.getItem()).hasAmmo(itemstack)) {
//			cir.setReturnValue(BipedModel.ArmPose.CROSSBOW_HOLD);
//		}
//	}

}
