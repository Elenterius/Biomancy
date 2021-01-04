package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerInteractionHandler {
	private PlayerInteractionHandler() {}

	@SubscribeEvent
	public static void onPlayerInteraction(final PlayerInteractEvent.EntityInteract event) {
		if (event.getWorld().isRemote()) return;
		if (event.getPlayer().isSneaking() && event.getTarget() instanceof ItemFrameEntity && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == ModItems.LUMINESCENT_SPORES.get()) {
			event.setCanceled(true);
			event.getTarget().setInvisible(!event.getTarget().isInvisible());
			if (!event.getPlayer().isCreative()) {
				event.getItemStack().shrink(1);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteraction(final PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getWorld().isRemote()) return;
		if (event.getPlayer().isSneaking() && event.getTarget() instanceof ArmorStandEntity && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == ModItems.LUMINESCENT_SPORES.get()) {
			event.setCanceled(true);
			event.getTarget().setInvisible(!event.getTarget().isInvisible());
			if (!event.getPlayer().isCreative()) {
				event.getItemStack().shrink(1);
			}
		}
	}

}
