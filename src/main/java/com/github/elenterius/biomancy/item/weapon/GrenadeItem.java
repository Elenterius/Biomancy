package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.entity.projectile.GrenadeProjectile;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.SimpleItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrenadeItem extends SimpleItem {

	public GrenadeItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.GRENADE_THROW.get(), SoundSource.PLAYERS, 0.5f, 1f - 0.375f * level.getRandom().nextFloat());

		if (!level.isClientSide) {
			GrenadeProjectile grenade = new GrenadeProjectile(level, player);
			grenade.setItem(stack);
			grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), -20f, 0.65f, 0.9f);
			level.addFreshEntity(grenade);
		}

		player.awardStat(Stats.ITEM_USED.get(this));

		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

}
