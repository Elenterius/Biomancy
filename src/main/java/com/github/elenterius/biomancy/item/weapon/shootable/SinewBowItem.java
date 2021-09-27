package com.github.elenterius.biomancy.item.weapon.shootable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class SinewBowItem extends BowItem {

	public final float drawTime = 50f;
	public final float baseVelocity = 5f;
	public final byte bonusPierce = 1;
	public final byte bonusPower = 2;

	public SinewBowItem(Properties builder) {
		super(builder);
	}

	public float getArrowVelocity(ItemStack stack, int charge) {
//		float v = MathHelper.sqrt(charge / drawTime);
		float v = (float) Math.pow(charge / drawTime, 0.6d);
		return Math.min(v, 1f);
	}

	public float getPullProgress(ItemStack stack, LivingEntity livingEntity) {
		return (float) (stack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / drawTime;
	}

	@Override
	public void releaseUsing(ItemStack bowStack, World worldIn, LivingEntity livingEntity, int timeLeft) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) livingEntity;
			boolean hasInfiniteAmmo = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) > 0;
			ItemStack ammoStack = player.getProjectile(bowStack);

			int charge = getUseDuration(bowStack) - timeLeft;
			charge = ForgeEventFactory.onArrowLoose(bowStack, worldIn, player, charge, !ammoStack.isEmpty() || hasInfiniteAmmo);
			if (charge < 0) return;

			if (!ammoStack.isEmpty() || hasInfiniteAmmo) {
				if (ammoStack.isEmpty()) {
					ammoStack = new ItemStack(Items.ARROW);
				}

				float velocityPct = getArrowVelocity(bowStack, charge);
				if (velocityPct < 0.23f) return;

				boolean isInfiniteArrow = player.abilities.instabuild || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem) ammoStack.getItem()).isInfinite(ammoStack, bowStack, player));
				if (!worldIn.isClientSide) {
					ArrowItem arrowItem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
					AbstractArrowEntity arrowEntity = arrowItem.createArrow(worldIn, ammoStack, player);
					arrowEntity = customArrow(arrowEntity);
					arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0f, baseVelocity * velocityPct, 1f);

					if (velocityPct >= 0.9f) {
						arrowEntity.setCritArrow(true);
						arrowEntity.setPierceLevel((byte) (arrowEntity.getPierceLevel() + bonusPierce)); //extra pierce bonus on full draw
					}

					arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bowStack) * 0.5d + bonusPower * velocityPct));

					int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bowStack);
					if (punchLevel > 0) {
						arrowEntity.setKnockback(punchLevel);
					}

					if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bowStack) > 0) {
						arrowEntity.setSecondsOnFire(100);
					}

					bowStack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand()));

					if (isInfiniteArrow || player.abilities.instabuild && (ammoStack.getItem() == Items.SPECTRAL_ARROW || ammoStack.getItem() == Items.TIPPED_ARROW)) {
						arrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
					}

					worldIn.addFreshEntity(arrowEntity);
				}

				worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1f, 1f / (random.nextFloat() * 0.4f + 1.2f) + velocityPct * 0.5f);
				if (!isInfiniteArrow && !player.abilities.instabuild) {
					ammoStack.shrink(1);
					if (ammoStack.isEmpty()) {
						player.inventory.removeItem(ammoStack);
					}
				}

				player.awardStat(Stats.ITEM_USED.get(this));
			}
		}
	}

}
