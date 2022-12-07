package com.github.elenterius.biomancy.integration.compat.pehkui;

import net.minecraft.world.entity.LivingEntity;

public sealed interface IPehkuiHelper permits IPehkuiHelper.EmptyPehkuiHelper, PehkuiCompat.PehkuiHelperImpl {

	IPehkuiHelper EMPTY = new EmptyPehkuiHelper();

	void setScale(LivingEntity livingEntity, float scale);

	void resetSize(LivingEntity livingEntity);

	void resize(LivingEntity livingEntity, float multiplier);

	float getScale(LivingEntity livingEntity);

	boolean isResizable(LivingEntity livingEntity);

	final class EmptyPehkuiHelper implements IPehkuiHelper {
		@Override
		public void setScale(LivingEntity livingEntity, float scale) {
			//empty
		}

		@Override
		public void resetSize(LivingEntity livingEntity) {
			//empty
		}

		@Override
		public void resize(LivingEntity livingEntity, float multiplier) {
			//empty
		}

		@Override
		public float getScale(LivingEntity livingEntity) {
			return 1f;
		}

		@Override
		public boolean isResizable(LivingEntity livingEntity) {
			return false;
		}

	}

}
