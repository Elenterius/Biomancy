package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingEventHandler {

	private LivingEventHandler() {}

	@SubscribeEvent
	public static void onLivingTick(final LivingEvent.LivingTickEvent event) {
		AcidInteractions.handleEntityInsideAcidFluid(event.getEntity());
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDeath(final LivingDeathEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity.level() instanceof ServerLevel serverLevel && livingEntity.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			if (livingEntity.isFreezing() || livingEntity.isOnFire()) return;
			PrimordialEcosystem.placeMalignantBlocksOnLivingDeath(serverLevel, livingEntity);
		}
	}

	@SubscribeEvent
	public static void onLivingJoinLevel(final EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof Mob mob && mob.hasEffect(ModMobEffects.FRENZY.get())) {
			FrenzySerum.injectAIBehavior(mob);
		}
	}

}
