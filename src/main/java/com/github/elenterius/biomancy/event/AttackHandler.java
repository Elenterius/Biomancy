package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.tags.ModDamageTypeTags;
import com.github.elenterius.biomancy.item.CriticalHitListener;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AttackHandler {

	private AttackHandler() {}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onCriticalHit(final CriticalHitEvent event) {
		if (event.getDamageModifier() > 0 && event.getTarget() instanceof LivingEntity target && (event.getResult() == Event.Result.ALLOW || (event.isVanillaCritical() && event.getResult() == Event.Result.DEFAULT))) {
			ItemStack heldStack = event.getEntity().getMainHandItem();
			if (heldStack.getItem() instanceof CriticalHitListener listener) {
				listener.onCriticalHitEntity(heldStack, event.getEntity(), target);
			}
		}
	}

	@SubscribeEvent
	public static void onApplyDamage(final LivingHurtEvent event) {
		DamageSource damageSource = event.getSource();
		if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		if (!damageSource.is(ModDamageTypeTags.FORGE_IS_ACID)) return;
		if (event.getAmount() <= 0f) return;

		int resistProbability = 0;
		for (ItemStack itemStack : event.getEntity().getArmorSlots()) {
			if (itemStack.getItem() instanceof AcolyteArmorItem armor && armor.hasNutrients(itemStack)) {
				resistProbability += 25;
				armor.decreaseNutrients(itemStack, 1);
			}
		}

		if (resistProbability > 0 && event.getEntity().getRandom().nextInt(100) >= resistProbability) {
			event.setAmount(event.getAmount() * (1f - resistProbability / 100f));
		}
	}

	//	@SubscribeEvent(priority = EventPriority.HIGHEST)
	//	public static void onHurt(final LivingHurtEvent event) {
	//		if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return; //use BYPASSES_ARMOR instead?
	//
	//		List<ItemStack> armorPieces = new ArrayList<>();
	//		for (ItemStack stack : event.getEntity().getArmorSlots()) {
	//			if (stack.getItem() instanceof ArmorItem) armorPieces.add(stack);
	//		}
	//		int numberOfArmorPieces = armorPieces.size();
	//
	//		if (numberOfArmorPieces > 0) {
	//			float damage = event.getAmount();
	//			float partialDamage = damage * (1f / numberOfArmorPieces); //divide damage into number of available armor pieces
	//			float reducedDamage = 0;
	//
	//			for (ItemStack stack : armorPieces) {
	//				if (stack.getItem() instanceof AcolyteArmorItem armor) {
	//					reducedDamage += AdaptiveDamageResistanceHandler.absorbDamage(event.getEntity(), event.getSource(), partialDamage, armor, stack);
	//				}
	//				else {
	//					reducedDamage += partialDamage;
	//				}
	//			}
	//
	//			event.setAmount(reducedDamage);
	//		}
	//	}

	@Deprecated(forRemoval = true)
	@SubscribeEvent(receiveCanceled = false)
	public static void onKnockback(final LivingKnockBackEvent event) {
		//TODO: mutate damage type to have no knockback
		event.getEntity().getCapability(ModCapabilities.NO_KNOCKBACK_FLAG_CAP).ifPresent(flag -> {
			if (flag.isEnabled()) {
				flag.disable();
				event.setCanceled(true);
			}
		});
	}

}
