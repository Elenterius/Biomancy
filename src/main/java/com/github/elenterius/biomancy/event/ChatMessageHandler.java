package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

import java.util.function.UnaryOperator;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChatMessageHandler {

	private static final UnaryOperator<String> ABBREVIATE_OPERATOR = s -> StringUtils.abbreviate(s, SharedConstants.MAX_CHAT_LENGTH);

	private ChatMessageHandler() {}

	@SubscribeEvent
	public static void onServerReceiveChatMessageFromClient(final ServerChatEvent event) {
		if (event.getPlayer().hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			event.setMessage(transform(event.getMessage()));
		}
	}

	private static Component transform(Component component) {
		Component transliterated = TransliterationUtil.transliterate(component, ABBREVIATE_OPERATOR);
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, component.copy());
		return ComponentUtil.setStyles(transliterated, TextStyles.PRIMORDIAL_RUNES.withHoverEvent(hoverEvent));
	}

}
