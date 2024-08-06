package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.serum.FrenzySerum;
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
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatusEffectHandler {

	private StatusEffectHandler() {}

	@SubscribeEvent
	public static void onEffectRemoval(final MobEffectEvent.Remove event) {
		if (event.getEntity().level().isClientSide) return;

		if (event.getEffect() == ModMobEffects.ESSENCE_ANEMIA.get() && ModMobEffectTags.isNotRemovableWithCleansingSerum(ModMobEffects.ESSENCE_ANEMIA.get())) {
			event.setCanceled(true);
		}

		addWithdrawalAfterFrenzy(event.getEntity(), event.getEffectInstance());
	}

	@SubscribeEvent
	public static void onEffectExpiry(final MobEffectEvent.Expired event) {
		if (event.getEntity().level().isClientSide) return;
		addWithdrawalAfterFrenzy(event.getEntity(), event.getEffectInstance());
	}

	private static void addWithdrawalAfterFrenzy(LivingEntity livingEntity, @Nullable MobEffectInstance removedEffectInstance) {
		if (removedEffectInstance == null) return;
		if (removedEffectInstance.getEffect() != ModMobEffects.FRENZY.get()) return;

		int amplifier = removedEffectInstance.getAmplifier();
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.WITHDRAWAL.get(), FrenzySerum.DEFAULT_DURATION_TICKS / 2 + amplifier * 30 * 20, amplifier));
	}

	@SubscribeEvent
	public static void onFoodEaten(final LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity().level().isClientSide) return;

		ItemStack stack = event.getItem();
		if (stack.isEdible() && stack.is(ModItemTags.SUGARS)) {
			FoodProperties food = stack.getFoodProperties(event.getEntity());
			reduceWithdrawal(food != null ? food.getNutrition() : 0, event.getEntity());
		}
	}

	public static void reduceWithdrawal(int nutrition, LivingEntity livingEntity) {
		MobEffectInstance withdrawalEffect = livingEntity.getEffect(ModMobEffects.WITHDRAWAL.get());
		if (withdrawalEffect != null) {
			int duration = withdrawalEffect.getDuration() - ((nutrition * nutrition / 2 + 4) * 20); //decrease effect duration by at least 4 sec
			int amplifier = withdrawalEffect.getAmplifier();
			boolean ambient = withdrawalEffect.isAmbient();
			boolean visible = withdrawalEffect.isVisible();
			boolean showIcon = withdrawalEffect.showIcon();
			overrideMobEffect(livingEntity, new MobEffectInstance(ModMobEffects.WITHDRAWAL.get(), duration, amplifier, ambient, visible, showIcon));
		}
	}

	public static void overrideMobEffect(LivingEntity livingEntity, MobEffectInstance newEffectInstance) {
		// we have to remove the old effect because the new effect has less duration and LivingEntity.addEffect() doesn't downgrade active effects
		// LivingEntity.addEffect() & EffectInstance.update() can only upgrade (duration/amplifier) effects
		livingEntity.removeEffect(newEffectInstance.getEffect());
		livingEntity.addEffect(newEffectInstance);
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
