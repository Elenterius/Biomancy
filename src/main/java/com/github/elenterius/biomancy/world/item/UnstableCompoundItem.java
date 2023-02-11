package com.github.elenterius.biomancy.world.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.ForgeEventFactory;

public class UnstableCompoundItem extends SimpleItem {

	public UnstableCompoundItem(Properties properties) {
		super(properties);
	}

	public static void explode(ItemEntity itemEntity, boolean isBurning) {
		if (itemEntity.level.isClientSide) return;
		if (itemEntity.fallDistance <= 1f) return;

		float explosionRadius = 0.5f + (itemEntity.getItem().getCount() / 64f) * 1.5f;
		float multiplier = isBurning ? 2f : 1f;

		Explosion.BlockInteraction interaction = ForgeEventFactory.getMobGriefingEvent(itemEntity.level, itemEntity) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
		itemEntity.level.explode(itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), explosionRadius * multiplier, interaction);

		itemEntity.discard();
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
		super.onDestroyed(itemEntity, damageSource);

		if (damageSource.isFire()) {
			explode(itemEntity, true);
		}
	}

}
