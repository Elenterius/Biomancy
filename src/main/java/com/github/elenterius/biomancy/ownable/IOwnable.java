package com.github.elenterius.biomancy.ownable;

import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public interface IOwnable {

	void setOwner(UUID userId);

	default void setOwner(Entity entity) {
		setOwner(entity.getUUID());
	}

	Optional<UUID> getOptionalOwnerUUID();

	void removeOwner();

	default boolean hasOwner() {
		return getOptionalOwnerUUID().isPresent();
	}

	default boolean isOwner(UUID userId) {
		return getOptionalOwnerUUID().map(value -> value.equals(userId)).orElse(false);
	}

	default boolean isOwner(Entity entity) {
		return getOptionalOwnerUUID().map(value -> value.equals(entity.getUUID())).orElse(false);
	}

}
