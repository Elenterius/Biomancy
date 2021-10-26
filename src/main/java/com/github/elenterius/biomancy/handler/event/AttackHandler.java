package com.github.elenterius.biomancy.handler.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.damagesource.ModEntityDamageSource;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.weapon.ClawWeaponItem;
import com.github.elenterius.biomancy.item.weapon.FleshbornGuanDaoItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AttackHandler {
//    private static final UUID HEALTH_REDUCTION_ID = UUID.fromString("9eee3c0b-43cf-41e5-af9d-8cfd79381515");
//    public static final AttributeModifier negativeHealthModifier = new AttributeModifier(HEALTH_REDUCTION_ID, "health reduction", -2.0F, AttributeModifier.Operation.ADDITION);

	private AttackHandler() {}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingAttack(final LivingAttackEvent event) {
		if (!event.getEntityLiving().isEffectiveAi()) return;

		DamageSource damageSource = event.getSource();
		if (!event.isCanceled() && damageSource instanceof ModEntityDamageSource) {
			((ModEntityDamageSource) damageSource).updateAttackStrength(event.getAmount()); // calculate attacker strength ("attack cool down")
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingDamageAfterDamageReduction(final LivingDamageEvent event) {
		if (!event.getEntityLiving().isEffectiveAi()) return;

		//if this is called the victims armor didn't block all damage

		DamageSource damageSource = event.getSource();
		if (damageSource instanceof ModEntityDamageSource && ((ModEntityDamageSource) damageSource).statusProc.equals("blight_thorn")) {
			if (((ModEntityDamageSource) damageSource).getAttackStrength() < 0.9f) return; // only trigger if "attack cool down" was nearly full

			ModifiableAttributeInstance healthAttribute = event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH);
			if (healthAttribute != null) {
				Entity attacker = damageSource.getEntity();
				if (attacker instanceof LivingEntity) {
					LivingEntity victim = event.getEntityLiving();
					if (((LivingEntity) attacker).getHealth() < victim.getMaxHealth() * 0.75f && victim.getRandom().nextFloat() < 0.6f) {
						healthAttribute.addTransientModifier(new AttributeModifier("health reduction", -0.3F, AttributeModifier.Operation.MULTIPLY_BASE));
//                        victim.playSound(ModSoundEvents.IMPACT_SPLAT, 0.25f, 0.8f);
						victim.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 0.4f, 0.45f);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDamageAfterDamageReductionLast(final LivingDamageEvent event) {
		if (event.getAmount() > 0f && event.getSource().getDirectEntity() instanceof LivingEntity) {
			ItemStack heldStack = ((LivingEntity) event.getSource().getDirectEntity()).getMainHandItem();
			if (heldStack.getItem() instanceof ClawWeaponItem) {
				((ClawWeaponItem) heldStack.getItem()).onDamageEntity(heldStack, (LivingEntity) event.getSource().getDirectEntity(), event.getEntityLiving(), event.getAmount());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onCriticalHit(final CriticalHitEvent event) {
		if (event.getDamageModifier() > 0 && event.getTarget() instanceof LivingEntity && (event.getResult() == Event.Result.ALLOW || event.isVanillaCritical() && event.getResult() == Event.Result.DEFAULT)) {
			ItemStack heldStack = event.getEntityLiving().getMainHandItem();
			if (heldStack.getItem() instanceof ClawWeaponItem) {
				((ClawWeaponItem) heldStack.getItem()).onCriticalHitEntity(heldStack, event.getPlayer(), (LivingEntity) event.getTarget());
			}
		}
	}

	@SubscribeEvent
	public static void onAttackEntity(final AttackEntityEvent event) {
		if (event.getTarget().isAttackable()) {
			ItemStack heldStack = event.getPlayer().getMainHandItem();
			if (!heldStack.isEmpty()) {
				if (heldStack.getItem() == ModItems.FLESHBORN_GUAN_DAO.get() && event.getPlayer().getAttackStrengthScale(0.5f) > 0.8f) {
					FleshbornGuanDaoItem.adaptAttackDamageToTarget(heldStack, event.getPlayer(), event.getTarget());
				}
				if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ATTUNED_BANE.get(), heldStack) > 0 && !AttunedDamageEnchantment.isAttuned(heldStack)) {
					if (!event.getPlayer().level.isClientSide()) {
						AttunedDamageEnchantment.setAttunedTarget(heldStack, event.getTarget());
					}
					else {
						event.getPlayer().playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1f, 1f);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(final LivingHurtEvent event) {
		float damage = event.getAmount();
		LivingEntity victim = event.getEntityLiving();
		Entity attacker = event.getSource().getEntity();
		if (attacker instanceof LivingEntity) {
			ItemStack heldStack = ((LivingEntity) attacker).getMainHandItem();
			if (!heldStack.isEmpty()) {
				float modifier = 0f;
				if (AttunedDamageEnchantment.isAttuned(heldStack)) {
					modifier = ModEnchantments.ATTUNED_BANE.get().getAttackDamageModifier(heldStack, (LivingEntity) attacker, victim);
				}
				if (heldStack.getItem() == ModItems.FLESHBORN_GUAN_DAO.get()) {
					modifier += FleshbornGuanDaoItem.getAttackDamageModifier(heldStack, (LivingEntity) attacker, victim);
				}
				event.setAmount(damage + modifier);
			}
		}
	}

}
