package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ToxinEffect extends StatusEffect {

	public ToxinEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		livingEntity.hurt(ModDamageSources.toxin(livingEntity.level(), null), 1f);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int tick = 25 >> amplifier;
		if (tick > 0) {
			return duration % tick == 0;
		}

		return true;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(Items.HONEY_BOTTLE));
		return stacks;
	}

}
