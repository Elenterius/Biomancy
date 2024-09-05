package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.UnaryOperator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

	@Shadow
	public ServerPlayer player;

	@Inject(method = "handleRenameItem(Lnet/minecraft/network/protocol/game/ServerboundRenameItemPacket;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/inventory/AnvilMenu.setItemName(Ljava/lang/String;)Z", shift = At.Shift.AFTER))
	private void onHandleRenameItem(ServerboundRenameItemPacket packet, CallbackInfo ci) {
		if (player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			if (player.containerMenu instanceof AnvilMenu menu) {
				Slot slot = menu.getSlot(2);
				if (!slot.hasItem()) return;

				ItemStack stack = slot.getItem();
				if (!stack.hasCustomHoverName()) return;

				stack.setHoverName(biomancy$transform(stack.getHoverName()));
			}
		}
	}

	@Unique
	private static Component biomancy$transform(Component component) {
		Component transliterated = TransliterationUtil.transliterate(component, UnaryOperator.identity());
		return ComponentUtil.setStyles(transliterated, TextStyles.PRIMORDIAL_RUNES);
	}

}
