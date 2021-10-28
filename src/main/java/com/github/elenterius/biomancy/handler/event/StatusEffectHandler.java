package com.github.elenterius.biomancy.handler.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.ai.goal.CompelledWalkGoal;
import com.github.elenterius.biomancy.entity.ai.goal.RavenousHungerTargetGoal;
import com.github.elenterius.biomancy.entity.ai.goal.RavenousMeleeAttackGoal;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.reagent.AdrenalineReagent;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatusEffectHandler {
	private StatusEffectHandler() {}

	@SubscribeEvent
	public static void onLivingSpawn(final EntityJoinWorldEvent event) {
		if (event.getWorld().isClientSide()) return;

		//attach custom AI behavior to living entities
		Entity entity = event.getEntity();
		if (entity instanceof MobEntity) {
			((MobEntity) entity).targetSelector.addGoal(1, new RavenousHungerTargetGoal<>((MobEntity) entity, LivingEntity.class));

			if (entity instanceof CreatureEntity) {
				if (!(entity instanceof IMob)) {
					((MobEntity) entity).goalSelector.addGoal(2, new RavenousMeleeAttackGoal((CreatureEntity) entity, 1d, false));
				}
				((MobEntity) entity).goalSelector.addGoal(1, new CompelledWalkGoal((CreatureEntity) entity, 1.25d));
			}
		}
	}

	@SubscribeEvent
	public static void onEffectExpiry(final PotionEvent.PotionExpiryEvent event) {
		if (!event.getEntityLiving().level.isClientSide) {
			EffectInstance effectInstance = event.getPotionEffect();
			if (effectInstance != null && effectInstance.getEffect() == ModEffects.ADRENALINE_RUSH.get()) {
				event.getEntityLiving().addEffect(new EffectInstance(ModEffects.ADRENAL_FATIGUE.get(), AdrenalineReagent.DURATION, AdrenalineReagent.AMPLIFIER));
			}
		}
	}

//	@SubscribeEvent
//	public static void onEffectRemoved(final PotionEvent.PotionRemoveEvent event) {
//		//shouldn't add new effects due to concurrent modification exception --> see LivingEntity.removeAllEffects() method
//	}

	@Nullable
	public static EffectInstance createAdrenalFatigueEffectFrom(@Nullable EffectInstance effectInstance) {
		int duration = AdrenalineReagent.DURATION;
		if (effectInstance != null) {
			duration -= effectInstance.getDuration(); //only punish for the active effect time
		}
		if (duration > 0) {
			return new EffectInstance(ModEffects.ADRENAL_FATIGUE.get(), duration, AdrenalineReagent.AMPLIFIER);
		}
		return null;
	}

	@SubscribeEvent
	public static void onFoodEaten(final LivingEntityUseItemEvent.Finish event) {
		if (!event.getEntityLiving().level.isClientSide) {
			ItemStack stack = event.getItem();
			if (stack.isEdible() && stack.getItem().is(ModTags.Items.SUGARS)) {
				Food food = stack.getItem().getFoodProperties();
				reduceAdrenalFatigue(food != null ? food.getNutrition() : 0, event.getEntityLiving());
			}
		}
	}

	public static void reduceAdrenalFatigue(int nutrition, LivingEntity livingEntity) {
		EffectInstance effectInstance = livingEntity.getEffect(ModEffects.ADRENAL_FATIGUE.get());
		if (effectInstance != null) {
			int duration = effectInstance.getDuration() - ((nutrition * nutrition / 2 + 4) * 20); //decrease effect duration by at least 4 sec
			int amplifier = effectInstance.getAmplifier();
			boolean ambient = effectInstance.isAmbient();
			boolean visible = effectInstance.isVisible();
			boolean showIcon = effectInstance.showIcon();

			//we have to remove the old effect because the new effect has less duration and LivingEntity.addEffect() doesn't downgrade active effects
			// LivingEntity.addEffect() & EffectInstance.update() can only upgrade (duration/amplifier)
			livingEntity.removeEffect(ModEffects.ADRENAL_FATIGUE.get());
			livingEntity.addEffect(new EffectInstance(ModEffects.ADRENAL_FATIGUE.get(), duration, amplifier, ambient, visible, showIcon));
		}
	}

}
