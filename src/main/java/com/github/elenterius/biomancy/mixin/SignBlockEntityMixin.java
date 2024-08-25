package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

	@Inject(at = @At(value = "HEAD"), method = "setMessages(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;Lnet/minecraft/world/level/block/entity/SignText;)Lnet/minecraft/world/level/block/entity/SignText;")
	private void onSetMessages(Player player, List<FilteredText> filteredText, SignText text, CallbackInfoReturnable<SignText> cir) {
		if (player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			for (Component message : text.getMessages(player.isTextFilteringEnabled())) {
				ComponentUtil.setStyles(message, TextStyles.PRIMORDIAL_RUNES);
			}
		}
	}

}
