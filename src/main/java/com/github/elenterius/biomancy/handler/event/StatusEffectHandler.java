package com.github.elenterius.biomancy.handler.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.ai.goal.DreadPanicGoal;
import com.github.elenterius.biomancy.entity.ai.goal.RavenousHungerTargetGoal;
import com.github.elenterius.biomancy.entity.ai.goal.RavenousMeleeAttackGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatusEffectHandler {
	private StatusEffectHandler() {}

//    @SubscribeEvent
//    public static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
//
//    }

	@SubscribeEvent
	public static void onLivingSpawn(final EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote()) return;

		Entity entity = event.getEntity();
		if (entity instanceof MobEntity) {
			((MobEntity) entity).targetSelector.addGoal(1, new RavenousHungerTargetGoal<>((MobEntity) entity, LivingEntity.class));

			if (!(entity instanceof IMob) && entity instanceof CreatureEntity) {
				((MobEntity) entity).goalSelector.addGoal(2, new RavenousMeleeAttackGoal((CreatureEntity) entity, 1.0D, false));
			}

			if (entity instanceof CreatureEntity) {
				((MobEntity) entity).goalSelector.addGoal(1, new DreadPanicGoal((CreatureEntity) entity, 2.0D));
			}
		}
	}
}
