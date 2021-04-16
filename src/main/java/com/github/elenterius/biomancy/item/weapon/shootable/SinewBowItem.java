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
		return (float) (stack.getUseDuration() - livingEntity.getItemInUseCount()) / drawTime;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack bowStack, World worldIn, LivingEntity livingEntity, int timeLeft) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) livingEntity;
			boolean hasInfiniteAmmo = player.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bowStack) > 0;
			ItemStack ammoStack = player.findAmmo(bowStack);

			int charge = getUseDuration(bowStack) - timeLeft;
			charge = ForgeEventFactory.onArrowLoose(bowStack, worldIn, player, charge, !ammoStack.isEmpty() || hasInfiniteAmmo);
			if (charge < 0) return;

			if (!ammoStack.isEmpty() || hasInfiniteAmmo) {
				if (ammoStack.isEmpty()) {
					ammoStack = new ItemStack(Items.ARROW);
				}

				float velocityPct = getArrowVelocity(bowStack, charge);
				if (velocityPct < 0.23f) return;

				boolean isInfiniteArrow = player.abilities.isCreativeMode || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem) ammoStack.getItem()).isInfinite(ammoStack, bowStack, player));
				if (!worldIn.isRemote) {
					ArrowItem arrowItem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
					AbstractArrowEntity arrowEntity = arrowItem.createArrow(worldIn, ammoStack, player);
					arrowEntity = customArrow(arrowEntity);
					arrowEntity.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0f, baseVelocity * velocityPct, 1f);

					if (velocityPct >= 0.9f) {
						arrowEntity.setIsCritical(true);
						arrowEntity.setPierceLevel((byte) (arrowEntity.getPierceLevel() + bonusPierce)); //extra pierce bonus on full draw
					}

					arrowEntity.setDamage(arrowEntity.getDamage() + (EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack) * 0.5d + bonusPower * velocityPct));

					int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
					if (punchLevel > 0) {
						arrowEntity.setKnockbackStrength(punchLevel);
					}

					if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0) {
						arrowEntity.setFire(100);
					}

					bowStack.damageItem(1, player, (playerEntity) -> playerEntity.sendBreakAnimation(playerEntity.getActiveHand()));

					if (isInfiniteArrow || player.abilities.isCreativeMode && (ammoStack.getItem() == Items.SPECTRAL_ARROW || ammoStack.getItem() == Items.TIPPED_ARROW)) {
						arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
					}

					worldIn.addEntity(arrowEntity);
				}

				worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1f, 1f / (random.nextFloat() * 0.4f + 1.2f) + velocityPct * 0.5f);
				if (!isInfiniteArrow && !player.abilities.isCreativeMode) {
					ammoStack.shrink(1);
					if (ammoStack.isEmpty()) {
						player.inventory.deleteStack(ammoStack);
					}
				}

				player.addStat(Stats.ITEM_USED.get(this));
			}
		}
	}

}
