package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.Fonts;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {

	@Shadow
	@Final
	private String[] messages;

	@Shadow
	@Final
	private SignBlockEntity sign;

	private AbstractSignEditScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "removed", at = @At(value = "HEAD"))
	private void onRemoved(CallbackInfo ci) {
		if (minecraft == null) return;
		if (minecraft.player == null) return;
		if (!minecraft.player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) return;

		int maxWidth = biomancy$approximateMaxLineWidth();
		for (int i = 0; i < messages.length; i++) {
			messages[i] = biomancy$transform(messages[i], maxWidth);
		}
	}

	@Unique
	private static String biomancy$transform(String text, int maxWidth) {
		String transliterated = TransliterationUtil.transliterate(text);
		return StringUtils.abbreviate(transliterated, maxWidth);
	}

	@Unique
	private int biomancy$approximateMaxLineWidth() {
		return Mth.floor((float) sign.getMaxTextLineWidth() / (Fonts.CARO_INVITICA_GLYPH_WIDTH - 1));
	}

}
