package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.ChrysalisBlockItem;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerInteractHandler {

	private PlayerInteractHandler() {}

	@SubscribeEvent
	public static void onPlayerInteractWithEntity(final PlayerInteractEvent.EntityInteract event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (item instanceof ExtractorItem || item instanceof InjectorItem || item instanceof ChrysalisBlockItem) {
			Entity target = event.getTarget();

			if (target instanceof PartEntity<?> partEntity) {
				interactWithParent(event, stack, item, partEntity);
			}
			else if (target instanceof LivingEntity livingEntity) {
				bypassLivingInteraction(event, stack, item, livingEntity);
			}
		}
	}

	private static void interactWithParent(PlayerInteractEvent.EntityInteract event, ItemStack stack, Item item, PartEntity<?> partEntity) {
		Entity parent = getParent(partEntity);
		if (parent instanceof LivingEntity livingEntity) {
			InteractionResult interactionResult = item.interactLivingEntity(stack, event.getEntity(), livingEntity, event.getHand());
			event.setCancellationResult(interactionResult);
			event.setCanceled(true);
		}
	}

	private static void bypassLivingInteraction(PlayerInteractEvent.EntityInteract event, ItemStack stack, Item item, LivingEntity livingEntity) {
		InteractionResult interactionResult = item.interactLivingEntity(stack, event.getEntity(), livingEntity, event.getHand());
		event.setCancellationResult(interactionResult);
		event.setCanceled(true);
	}

	private static Entity getParent(Entity entity) {
		if (entity instanceof PartEntity<?> partEntity) {
			return getParent(partEntity.getParent());
		}
		return entity;
	}

}
