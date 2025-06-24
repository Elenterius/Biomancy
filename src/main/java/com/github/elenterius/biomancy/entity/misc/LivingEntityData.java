package com.github.elenterius.biomancy.entity.misc;

import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityData {

	private LivingEntityData() {}

	public interface TransientDataProvider {
		DataHolder biomancy$getData();

		final class DataHolder {
			boolean discardFriction = false;
			boolean wasDiscardFriction = false;

			public void setDiscardFriction(boolean discardFriction) {
				this.discardFriction = discardFriction;
			}

			public boolean shouldDiscardFriction() {
				return discardFriction || wasDiscardFriction;
			}

			public void tick(LivingEntity context) {
				wasDiscardFriction = discardFriction;
				discardFriction = false;
			}
		}

	}

}
