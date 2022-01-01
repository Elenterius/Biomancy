package com.github.elenterius.biomancy.world.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FleshEatingDiseaseEffect extends StatusEffect {

	public FleshEatingDiseaseEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (MobUtil.isSkeleton(livingEntity)) return; //can't affect entities without flesh

		if (livingEntity.hasEffect(MobEffects.REGENERATION) && livingEntity.level.random.nextFloat() < 0.5f) return; // 50% chance to not perform the effect

		float damage = (amplifier + 1f) * 0.5f;
		boolean attackSuccessful = livingEntity.hurt(ModDamageSources.DISEASE, damage);

		if (!livingEntity.level.isClientSide() && livingEntity.level.random.nextFloat() < 0.15f) {
			ItemStack stack = new ItemStack(livingEntity.level.random.nextFloat() < 0.55f ? ModItems.NECROTIC_FLESH.get() : ModItems.SKIN_CHUNK.get());
			dropItem(livingEntity.level, stack, livingEntity.getX(), livingEntity.getY() + 0.5f, livingEntity.getZ());
		}

		if (livingEntity instanceof Player player) {
			player.causeFoodExhaustion((amplifier + 1f) * 0.0025f);
			if (livingEntity.level.getLevelData().getDifficulty().getId() > Difficulty.NORMAL.getId() && !player.hasEffect(MobEffects.HUNGER)) {
				MobEffectInstance effect = livingEntity.getEffect(ModMobEffects.FLESH_EATING_DISEASE.get());
				if (effect != null) {
					livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, effect.getDuration(), 0));
				}
			}
		}
	}

	private void dropItem(Level world, ItemStack stack, double x, double y, double z) {
		ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
		itemEntity.setPickUpDelay(40);
		itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
		world.addFreshEntity(itemEntity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int nTicks = 40 >> amplifier;
		return nTicks <= 0 || duration % nTicks == 0;
	}

}
