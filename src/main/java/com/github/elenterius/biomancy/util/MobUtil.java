package com.github.elenterius.biomancy.util;

import net.minecraft.entity.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.BiConsumer;

public final class MobUtil {

	private MobUtil() {}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType) {
		return convertMobEntityTo(world, entityIn, outcomeType, true);
	}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType, boolean copyEquipment) {
		return convertMobEntityTo(world, entityIn, outcomeType, copyEquipment, (oldEntity, outcome) -> {});
	}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E oldEntity, EntityType<T> outcomeType, boolean copyEquipment, BiConsumer<E, T> onConvert) {
		if (ForgeEventFactory.canLivingConvert(oldEntity, outcomeType, timer -> {})) {
			T newEntity = oldEntity.func_233656_b_(outcomeType, copyEquipment);// create new entity with same settings & equipment and remove old entity
			if (newEntity != null) {
				newEntity.onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
				newEntity.hurtResistantTime = 60;
				onConvert.accept(oldEntity, newEntity);
				ForgeEventFactory.onLivingConvert(oldEntity, newEntity);
				return true;
			}
		}
		return false;
	}

	public static boolean convertLivingEntityTo(ServerWorld world, LivingEntity oldEntity, EntityType<?> outcomeType) {
		if (oldEntity.removed) return false;

		Entity entity = outcomeType.create(world);
		if (entity != null) {
			if (entity instanceof LivingEntity) {
				//noinspection unchecked
				EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) outcomeType;
				if (ForgeEventFactory.canLivingConvert(oldEntity, entityType, timer -> {})) {
					entity.copyLocationAndAnglesFrom(oldEntity);
					oldEntity.remove();
					world.addEntity(entity);
					if (entity instanceof MobEntity) {
						((MobEntity) entity).onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
					}
					entity.hurtResistantTime = 60;
					ForgeEventFactory.onLivingConvert(oldEntity, (LivingEntity) entity);
					return true;
				}
			}

			entity.remove();
		}

		return false;
	}
}
