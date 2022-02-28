package com.github.elenterius.biomancy.world.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.weapon.ICriticalHitEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttackHandler {

	private AttackHandler() {}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onCriticalHit(final CriticalHitEvent event) {
		if (event.getDamageModifier() > 0 && event.getTarget() instanceof LivingEntity target && (event.getResult() == Event.Result.ALLOW || event.isVanillaCritical() && event.getResult() == Event.Result.DEFAULT)) {
			ItemStack heldStack = event.getEntityLiving().getMainHandItem();
			if (heldStack.getItem() instanceof ICriticalHitEntity listener) {
				listener.onCriticalHitEntity(heldStack, event.getPlayer(), target);
			}
		}
	}


}