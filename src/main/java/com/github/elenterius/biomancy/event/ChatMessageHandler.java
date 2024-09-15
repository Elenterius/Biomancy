package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChatMessageHandler {

	private ChatMessageHandler() {}

	@SubscribeEvent
	public static void onServerReceiveChatMessageFromClient(final ServerChatEvent event) {
		if (event.getPlayer().hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, event.getMessage().copy());
			event.setMessage(ComponentUtil.setStyles(event.getMessage(), TextStyles.PRIMORDIAL_RUNES.withHoverEvent(hoverEvent)));
		}
	}

	@SubscribeEvent
	public static void onClientSendChatMessageToServer(final ClientChatEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			String transliterated = TransliterationUtil.transliterate(event.getMessage());
			String abbreviated = StringUtils.abbreviate(transliterated, SharedConstants.MAX_CHAT_LENGTH);
			event.setMessage(abbreviated);
		}
	}

}
