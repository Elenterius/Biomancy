package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChatMessageHandler {

	private ChatMessageHandler() {}

	@SubscribeEvent
	public static void onServerReceiveChatMessageFromClient(final ServerChatEvent event) {
		if (event.getPlayer().hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, event.getMessage().copy());
			ComponentUtil.setStyles(event.getMessage(), TextStyles.PRIMORDIAL_RUNES.withHoverEvent(hoverEvent));
		}
	}

}
