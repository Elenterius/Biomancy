package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;

import java.util.List;

public class FleshEatingDiseaseEffect extends StatusEffect {

	public FleshEatingDiseaseEffect(EffectType type, int liquidColor) {
		super(type, liquidColor);
	}

	@Override
	public void performEffect(LivingEntity livingEntity, int amplifier) {
		if (livingEntity.isPotionActive(Effects.REGENERATION)) {
			if (livingEntity.world.rand.nextFloat() < 0.5f) return; // 50% chance to not perform the effect
		}

		float damage = (amplifier + 1f) * 0.5f;
		livingEntity.attackEntityFrom(ModDamageSources.DISEASE, damage);

		if (livingEntity instanceof PlayerEntity) {
			((PlayerEntity) livingEntity).addExhaustion((amplifier + 1f) * 0.0025f);
			if (livingEntity.world.getWorldInfo().getDifficulty().getId() > Difficulty.NORMAL.getId() && !livingEntity.isPotionActive(Effects.HUNGER)) {
				EffectInstance effect = livingEntity.getActivePotionEffect(ModEffects.FLESH_EATING_DISEASE.get());
				if (effect != null) {
					livingEntity.addPotionEffect(new EffectInstance(Effects.HUNGER, effect.getDuration(), 0));
				}
			}
		}

		if (!livingEntity.world.isRemote() && livingEntity.world.rand.nextFloat() < 0.15f) {
			ItemStack stack = new ItemStack(livingEntity.world.rand.nextFloat() < 0.55f ? ModItems.NECROTIC_FLESH.get() : ModItems.SKIN_CHUNK.get());
			ItemEntity itemEntity = new ItemEntity(livingEntity.world, livingEntity.getPosX(), livingEntity.getPosY() + 0.5, livingEntity.getPosZ(), stack);
			itemEntity.setPickupDelay(40);
			itemEntity.setMotion(itemEntity.getMotion().mul(0, 1, 0));
			livingEntity.world.addEntity(itemEntity);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		int nTicks = 40 >> amplifier;
		return nTicks <= 0 || duration % nTicks == 0;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return ImmutableList.of(new ItemStack(ModItems.REJUVENATING_MUCUS.get()), new ItemStack(Items.MILK_BUCKET));
	}
}
