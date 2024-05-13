package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.serum.AdrenalineSerum;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatusEffectHandler {

	private StatusEffectHandler() {}

	@SubscribeEvent
	public static void onEffectRemoval(final MobEffectEvent.Remove event) {
		if (event.getEntity().level().isClientSide) return;
		if (event.getEffect() == ModMobEffects.ESSENCE_ANEMIA.get() && ModMobEffectTags.isNotRemovableWithCleansingSerum(ModMobEffects.ESSENCE_ANEMIA.get())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onEffectExpiry(final MobEffectEvent.Expired event) {
		if (!event.getEntity().level().isClientSide) {
			MobEffectInstance effectInstance = event.getEffectInstance();
			if (effectInstance != null && effectInstance.getEffect() == ModMobEffects.ADRENALINE_RUSH.get()) {
				event.getEntity().addEffect(new MobEffectInstance(ModMobEffects.ADRENAL_FATIGUE.get(), AdrenalineSerum.DURATION, AdrenalineSerum.AMPLIFIER));
			}
		}
	}

	@Nullable
	public static MobEffectInstance createAdrenalFatigueEffectFrom(@Nullable MobEffectInstance effectInstance) {
		int duration = AdrenalineSerum.DURATION;
		if (effectInstance != null) {
			duration -= effectInstance.getDuration(); //only punish for the active effect time
		}
		if (duration > 0) {
			return new MobEffectInstance(ModMobEffects.ADRENAL_FATIGUE.get(), duration, AdrenalineSerum.AMPLIFIER);
		}
		return null;
	}

	@SubscribeEvent
	public static void onFoodEaten(final LivingEntityUseItemEvent.Finish event) {
		if (!event.getEntity().level().isClientSide) {
			ItemStack stack = event.getItem();
			if (stack.isEdible() && stack.is(ModItemTags.SUGARS)) {
				FoodProperties food = stack.getItem().getFoodProperties();
				reduceAdrenalFatigue(food != null ? food.getNutrition() : 0, event.getEntity());
			}
		}
	}

	public static void reduceAdrenalFatigue(int nutrition, LivingEntity livingEntity) {
		MobEffectInstance effectInstance = livingEntity.getEffect(ModMobEffects.ADRENAL_FATIGUE.get());
		if (effectInstance != null) {
			int duration = effectInstance.getDuration() - ((nutrition * nutrition / 2 + 4) * 20); //decrease effect duration by at least 4 sec
			int amplifier = effectInstance.getAmplifier();
			boolean ambient = effectInstance.isAmbient();
			boolean visible = effectInstance.isVisible();
			boolean showIcon = effectInstance.showIcon();
			overrideMobEffect(livingEntity, new MobEffectInstance(ModMobEffects.ADRENAL_FATIGUE.get(), duration, amplifier, ambient, visible, showIcon));
		}
	}

	public static void overrideMobEffect(LivingEntity livingEntity, MobEffectInstance newMobEffectInstance) {
		// we have to remove the old effect because the new effect has less duration and LivingEntity.addEffect() doesn't downgrade active effects
		// LivingEntity.addEffect() & EffectInstance.update() can only upgrade (duration/amplifier) effects
		livingEntity.removeEffect(newMobEffectInstance.getEffect());
		livingEntity.addEffect(newMobEffectInstance);
	}

	public static boolean canApplySplashEffectIfAllowed(MobEffect effect, LivingEntity target) {
		MobEffectCategory category = effect.getCategory();

		if (target.isInvertedHealAndHarm()) {
			if (effect == MobEffects.HEAL) {
				category = MobEffectCategory.HARMFUL;
			}
			else if (effect == MobEffects.HARM) {
				category = MobEffectCategory.BENEFICIAL;
			}
		}

		if (category == MobEffectCategory.HARMFUL) {
			int resistProbability = 0;

			for (ItemStack itemStack : target.getArmorSlots()) {
				if (itemStack.getItem() instanceof AcolyteArmorItem armor && armor.hasNutrients(itemStack)) {
					resistProbability += 15;
				}
			}

			if (resistProbability > 0) {
				return target.getRandom().nextInt(100) >= resistProbability;
			}
		}

		return true;
	}

}
