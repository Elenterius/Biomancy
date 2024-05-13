package com.github.elenterius.biomancy.item.injector;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

final class InjectionScheduler {

	public static final String DELAY_KEY = "DelayInTicks";
	public static final String TIMESTAMP_KEY = "ScheduleTimestamp";

	private InjectionScheduler() {}

	public static void schedule(InjectorItem injector, ItemStack stack, Player player, LivingEntity target, int delayInTicks) {
		if (stack.isEmpty() || player.level().isClientSide || injector.getSerum(stack).isEmpty()) return;

		injector.setEntityHost(stack, player); //who is using the item
		injector.setEntityVictim(stack, target); //who is the victim

		injector.setInjectionSuccess(stack, CombatUtil.canPierceThroughArmor(stack, target, player)); //precompute injection success

		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt(DELAY_KEY, delayInTicks);
		tag.putLong(TIMESTAMP_KEY, player.level().getGameTime());
	}

	public static void tick(ServerLevel level, InjectorItem injector, ItemStack stack, ServerPlayer player) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(TIMESTAMP_KEY)) return;

		long delayInTicks = tag.getLong(DELAY_KEY);
		long starTimestamp = tag.getLong(TIMESTAMP_KEY);
		if (player.level().getGameTime() - starTimestamp > delayInTicks) {
			performScheduledSerumInjection(level, injector, stack, player);
			tag.remove(DELAY_KEY);
			tag.remove(TIMESTAMP_KEY);
		}
	}

	public static void performScheduledSerumInjection(ServerLevel level, InjectorItem injector, ItemStack stack, ServerPlayer player) {
		Serum serum = injector.getSerum(stack);
		if (serum.isEmpty()) return;

		Entity victim = injector.getEntityVictim(stack, level);
		Entity host = injector.getEntityHost(stack, level);
		boolean injectionSuccess = injector.getInjectionSuccess(stack);

		if (victim instanceof LivingEntity target) {
			if (!injectionSuccess) {
				stack.hurtAndBreak(2, player, p -> {});
				player.broadcastBreakEvent(EquipmentSlot.MAINHAND); //break needle
				injector.broadcastAnimation(level, player, stack, InjectorItem.InjectorAnimation.REGROW_NEEDLE);
				player.getCooldowns().addCooldown(stack.getItem(), InjectorItem.COOL_DOWN_TICKS * 2);
				return;
			}

			float damagePct = 1f;
			for (ItemStack itemStack : target.getArmorSlots()) {
				if (itemStack.getItem() instanceof AcolyteArmorItem armor && armor.hasNutrients(itemStack)) {
					damagePct -= 0.25f;
				}
			}

			if (stack.getEnchantmentLevel(ModEnchantments.ANESTHETIC.get()) <= 0) {
				float damage = 0.5f * damagePct;
				if (damage > 0) {
					target.hurt(level.damageSources().sting(player), damage);
				}
			}

			if (host == victim) {
				serum.affectPlayerSelf(Serum.getDataTag(stack), player);
			}
			else {
				serum.affectEntity(level, Serum.getDataTag(stack), player, target);
			}

			injector.consumeSerum(stack, player);
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
	}
}
