package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.Fonts;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.chat.Style;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

	@ModifyExpressionValue(
			method = "setMessages",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getStyle()Lnet/minecraft/network/chat/Style;")
	)
	private Style onSetMessagesModifyStyle(Style originalStyle, Player player, List<FilteredText> filteredText, SignText text) {
		if (player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			return originalStyle.withFont(Fonts.CARO_INVITICA);
		}

		if (originalStyle.getFont().equals(Fonts.CARO_INVITICA)) {
			return originalStyle.withFont(null);
		}

		return originalStyle;
	}

}
