package com.github.elenterius.biomancy.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

public class BulletJumpEnchantment extends Enchantment {
	public BulletJumpEnchantment(Rarity rarityIn) {
		super(rarityIn, EnchantmentType.ARMOR_LEGS, new EquipmentSlotType[]{EquipmentSlotType.LEGS});
	}

	@Override
	public int getMinEnchantability(int enchantmentLevel) {
		return enchantmentLevel * 10;
	}

	@Override
	public int getMaxEnchantability(int enchantmentLevel) {
		return getMinEnchantability(enchantmentLevel) + 15;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean canVillagerTrade() {
		return false;
	}

	@Override
	public boolean canGenerateInLoot() {
		return false;
	}

	public void executeBulletJump(PlayerEntity player, int level, boolean applySneakFix) {
		Vector3d lookVec = player.getLookVec();
		Vector3d motion = player.getMotion();

		boolean startedFaLlFlying = tryToStartFallFlying(player); // get free boosted elytra takeoff without rockets :)

		if (!startedFaLlFlying) {
			float maxLevel = getMaxLevel();
			float factor = 3f * ((1f + (float) level) / (maxLevel + 1f));
			player.setMotion(motion.x + lookVec.x * factor, 0d + lookVec.y * factor, motion.z + lookVec.z * factor); // we ignore y motion, because we don't want jump boost etc to affected the leap
			player.startSpinAttack(20);
			if (player.isOnGround()) {
				player.move(MoverType.SELF, new Vector3d(0d, 1.1999999f, 0d));
			}
		}
		else {
			float factor = 0.3f;
			double jumpMotion = motion.y + factor * level + 0.15f;
			player.setMotion(motion.x + lookVec.x * jumpMotion, motion.y + lookVec.y * ((factor - 0.1f) * level), motion.z + lookVec.z * jumpMotion);

			//WARNING: "player moved wrongly!" --> caused by sneaking and trying to jump off block ledge (fall off prevention)
			if (applySneakFix) player.setSneaking(false); // the fix
		}
		player.isAirBorne = true;
//		player.world.playMovingSound(null, player, SoundEvents.ITEM_TRIDENT_RIPTIDE_3, SoundCategory.PLAYERS, 1.0F, 1.0F);

		player.addExhaustion(0.6F * (float) level);
	}

	private static boolean tryToStartFallFlying(PlayerEntity player) {
		if (!player.isElytraFlying() && !player.isInWater() && !player.isPotionActive(Effects.LEVITATION)) {
			ItemStack itemstack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
			if (itemstack.canElytraFly(player)) {
				player.startFallFlying();
				return true;
			}
		}
		return false;
	}
}
