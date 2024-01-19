package com.github.elenterius.biomancy.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

public class UnstableCompoundItem extends SimpleItem {

	public UnstableCompoundItem(Properties properties) {
		super(properties);
	}

	public static void explode(ItemEntity itemEntity, boolean isBurning) {
		if (itemEntity.level().isClientSide) return;
		if (itemEntity.fallDistance <= 1f) return;

		float explosionRadius = 0.5f + (itemEntity.getItem().getCount() / 64f) * 1.5f;
		float multiplier = isBurning ? 2f : 1f;

		itemEntity.level().explode(itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), explosionRadius * multiplier, Level.ExplosionInteraction.TNT);

		itemEntity.discard();
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
		super.onDestroyed(itemEntity, damageSource);

		if (damageSource.is(DamageTypeTags.IS_FIRE)) {
			explode(itemEntity, true);
		}
	}

}
