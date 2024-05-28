package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.AcidFluid;
import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingEventHandler {

	private LivingEventHandler() {}

	@SubscribeEvent
	public static void onLivingTick(final LivingEvent.LivingTickEvent event) {
		AcidFluid.onEntityInside(event.getEntity());
	}

	@SubscribeEvent
	public static void onLivingTick(final TickEvent.PlayerTickEvent event) {
		if (event.side == LogicalSide.CLIENT) return;
		if (event.phase == TickEvent.Phase.START) return;

		if (event.player.tickCount % 30 == 0) {
			ModEnchantments.SELF_FEEDING.get().repairLivingItems(event.player);
			ModEnchantments.PARASITIC_METABOLISM.get().repairLivingItems(event.player);
		}
	}

}
