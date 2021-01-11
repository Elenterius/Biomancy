package com.github.elenterius.blightlings.tileentity;

import java.util.Optional;
import java.util.UUID;

public interface IOwnableTile {

	void setOwner(UUID uuid);

	Optional<UUID> getOwner();

	void removeOwner();

	default boolean hasOwner() {
		return getOwner().isPresent();
	}

	default boolean isOwner(UUID uuid) {
		return getOwner().map(value -> value.equals(uuid)).orElse(false);
	}

}