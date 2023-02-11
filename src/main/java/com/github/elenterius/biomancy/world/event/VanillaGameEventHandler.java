package com.github.elenterius.biomancy.world.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.UnstableCompoundItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VanillaGameEventHandler {

	private VanillaGameEventHandler() {}

	@SubscribeEvent
	public static void onEvent(final VanillaGameEvent event) {
		if (event.getVanillaEvent() != GameEvent.HIT_GROUND) return;

		Entity cause = event.getCause();
		if (cause instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof UnstableCompoundItem) {
			UnstableCompoundItem.explode(itemEntity, false);
		}
	}

}
