package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.Fonts;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.network.FilteredText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

	@Shadow
	public abstract int getMaxTextLineWidth();

	@Inject(
			method = "setMessages(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;Lnet/minecraft/world/level/block/entity/SignText;)Lnet/minecraft/world/level/block/entity/SignText;",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void onSetMessages(Player player, List<FilteredText> filteredText, SignText text, CallbackInfoReturnable<SignText> cir) {
		if (player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			int maxWidth = biomancy$approximateMaxLineWidth();

			for (int i = 0; i < filteredText.size(); i++) {
				FilteredText filteredtext = filteredText.get(i);
				Style style = text.getMessage(i, player.isTextFilteringEnabled()).getStyle();

				if (player.isTextFilteringEnabled()) {
					text = text.setMessage(i, biomancy$transform(filteredtext.filteredOrEmpty(), maxWidth, style));
				}
				else {
					text = text.setMessage(i, biomancy$transform(filteredtext.raw(), maxWidth, style), biomancy$transform(filteredtext.filteredOrEmpty(), maxWidth, style));
				}
			}

			cir.setReturnValue(text);
		}
	}

	@ModifyExpressionValue(
			method = "setMessages",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getStyle()Lnet/minecraft/network/chat/Style;")
	)
	private Style onSetMessagesModifyStyle(Style originalStyle, Player player, List<FilteredText> filteredText, SignText text) {
		if (originalStyle.getFont().equals(Fonts.CARO_INVITICA)) {
			return originalStyle.withFont(null);
		}
		return originalStyle;
	}

	@Unique
	private static Component biomancy$transform(String text, int maxWidth, Style style) {
		String transliterated = TransliterationUtil.transliterate(text);
		String abbreviated = StringUtils.abbreviate(transliterated, maxWidth);

		if (abbreviated.isBlank()) {
			return ComponentUtil.empty();
		}

		return ComponentUtil.literal(abbreviated).setStyle(style.withFont(Fonts.CARO_INVITICA));
	}

	@Unique
	private int biomancy$approximateMaxLineWidth() {
		return Mth.floor((float) getMaxTextLineWidth() / (Fonts.CARO_INVITICA_GLYPH_WIDTH - 1));
	}

}
