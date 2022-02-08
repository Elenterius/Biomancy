package com.github.elenterius.biomancy.world.entity.ownable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface IOwnableMob {

	Optional<UUID> getOwnerUUID();

	void setOwnerUUID(@Nullable UUID uuid);

	void setOwner(Player entity);

	Optional<Player> getOwner();

	default boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
		if (target.getUUID().equals(owner.getUUID())) return false;

		if (target instanceof IOwnableMob ownableCreature) {
			return !ownableCreature.isOwner(owner);
		}
		else if (target instanceof OwnableEntity ownableEntity) {
			return !owner.getUUID().equals(ownableEntity.getOwnerUUID());
		}
		else return !(target instanceof Player targetPlayer) || !(owner instanceof Player ownerPlayer) || ownerPlayer.canHarmPlayer(targetPlayer);
	}

	default boolean isOwner(LivingEntity entity) {
		return getOwnerUUID().map(uuid -> uuid.equals(entity.getUUID())).orElse(false);
	}

	boolean tryToReturnIntoPlayerInventory();

}
