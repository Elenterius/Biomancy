package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.world.item.IArmPoseProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

	@Inject(method = "getArmPose", at = @At(value = "HEAD"), cancellable = true)
	private static void onGetArmPose(AbstractClientPlayer player, InteractionHand usedHand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (stack.getItem() instanceof IArmPoseProvider armPoseProvider) {
			cir.setReturnValue(armPoseProvider.getArmPose(player, usedHand, stack));
		}
	}

}
