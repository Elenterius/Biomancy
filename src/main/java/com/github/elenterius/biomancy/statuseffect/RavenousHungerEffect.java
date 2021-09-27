package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectType;
import net.minecraft.util.FoodStats;

import java.util.List;

public class RavenousHungerEffect extends StatusEffect {

	public RavenousHungerEffect(EffectType type, int liquidColor) {
		super(type, liquidColor);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof PlayerEntity) {
			FoodStats foodStats = ((PlayerEntity) livingEntity).getFoodData();
			if (foodStats.getSaturationLevel() > 0f) {
				foodStats.addExhaustion(4f);
				int foodLevel = foodStats.getFoodLevel();
				if (foodLevel > 0) foodStats.setFoodLevel(Math.max(foodLevel - (1 + amplifier), 0));
			}
		}
		else {
			if (livingEntity.getHealth() > 1f) livingEntity.hurt(ModDamageSources.RAVENOUS_HUNGER, 1f);
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int nTicks = 40 >> amplifier;
		return nTicks <= 0 || duration % nTicks == 0;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
//		ModTags.Items.RAW_MEATS.getAllElements().stream().map(ItemStack::new).collect(Collectors.toList());
		return ImmutableList.of(new ItemStack(Items.MELON_SLICE), new ItemStack(Items.HONEY_BOTTLE), new ItemStack(ModItems.NUTRIENT_BAR.get()) /*, new ItemStack(ModItems.VILE_MELON_SLICE.get()), new ItemStack(ModItems.COOKED_VILE_MELON_SLICE.get())*/);
	}
}
