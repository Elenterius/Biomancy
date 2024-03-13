package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class BoneMarrowItem extends SimpleItem {

	public BoneMarrowItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		return ItemUtils.startUsingInstantly(level, player, usedHand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack resultStack = super.finishUsingItem(stack, level, livingEntity);

		if (!level.isClientSide) {
			if (livingEntity instanceof ServerPlayer serverplayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
				serverplayer.awardStat(Stats.ITEM_USED.get(this));
			}

			livingEntity.removeEffect(MobEffects.HUNGER);
		}

		return resultStack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return ModSoundEvents.MARROW_DRINK.get();
	}

	@Override
	public SoundEvent getEatingSound() {
		return ModSoundEvents.MARROW_DRINK.get();
	}

}
