package com.github.elenterius.biomancy.ownable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface IOwnableMob extends IOwnable, OwnableEntity {

	Optional<Player> getOwnerAsPlayer();

	Optional<Entity> getOwnerAsEntity();

	@Nullable
	@Override
	default UUID getOwnerUUID() {
		return getOptionalOwnerUUID().orElse(null);
	}

	@Nullable
	@Override
	default Entity getOwner() {
		return getOwnerAsEntity().orElse(null);
	}

	default boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
		if (target.getUUID().equals(owner.getUUID())) return false;

		if (target instanceof IOwnableMob mob) {
			return !mob.isOwner(owner);
		}
		else if (target instanceof OwnableEntity entity) {
			return !owner.getUUID().equals(entity.getOwnerUUID());
		}
		else return !(target instanceof Player targetPlayer) || !(owner instanceof Player ownerPlayer) || ownerPlayer.canHarmPlayer(targetPlayer);
	}

	boolean tryToReturnIntoPlayerInventory();

}
