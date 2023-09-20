package com.github.elenterius.biomancy.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class SapberryItem extends SimpleItem {

	public SapberryItem(Properties properties) {
		super(properties);
	}

	private static void applyPotion(LivingEntity livingEntity, Potion potion) {
		for (MobEffectInstance effectInstance : potion.getEffects()) {
			if (effectInstance.getEffect().isInstantenous()) {
				effectInstance.getEffect().applyInstantenousEffect(livingEntity, livingEntity, livingEntity, effectInstance.getAmplifier(), 1);
			}
			else {
				livingEntity.addEffect(new MobEffectInstance(effectInstance));
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack result = livingEntity.eat(level, stack);

		if (!level.isClientSide) {
			Collection<Potion> potions = ForgeRegistries.POTIONS.getValues();
			potions.stream().skip(level.random.nextInt(potions.size())).findFirst().ifPresent(potion -> applyPotion(livingEntity, potion));
		}

		return result;
	}
}
