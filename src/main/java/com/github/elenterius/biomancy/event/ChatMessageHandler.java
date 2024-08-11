package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChatMessageHandler {

	private ChatMessageHandler() {}

	@SubscribeEvent
	public static void onServerReceiveChatMessageFromClient(final ServerChatEvent event) {
		int level = event.getPlayer().getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(ModEnchantments.PRIMORDIAL_KNOWLEDGE.get());
		if (level > 1) {
			ComponentUtil.setStyles(event.getMessage(), TextStyles.PRIMORDIAL_RUNES);
		}
	}

}
