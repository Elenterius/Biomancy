package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.WaterGelBlock;
import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
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
		LivingEntity livingEntity = event.getEntity();

		if (livingEntity instanceof LivingEntityData.TransientDataProvider provider) {
			provider.biomancy$getData().tick(livingEntity);
		}

		AcidInteractions.handleEntityInsideAcidFluid(livingEntity);
	}

	@SubscribeEvent
	public static void onLivingBreath(final LivingBreatheEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Block blockAtEyePos = livingEntity.level().getBlockState(BlockPos.containing(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ())).getBlock();

		if (blockAtEyePos instanceof WaterGelBlock) {
			boolean canBreathe = !livingEntity.canDrownInFluidType(Fluids.WATER.getFluidType()) || MobEffectUtil.hasWaterBreathing(livingEntity) || (livingEntity instanceof Player && ((Player) livingEntity).getAbilities().invulnerable);
			if (!canBreathe) {
				event.setCanBreathe(false);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}

		if (event.side == LogicalSide.CLIENT) return;

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
