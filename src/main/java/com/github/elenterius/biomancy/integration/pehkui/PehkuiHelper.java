package com.github.elenterius.biomancy.integration.pehkui;

import net.minecraft.world.entity.LivingEntity;

public sealed interface PehkuiHelper permits PehkuiHelper.EmptyPehkuiHelper, PehkuiIntegration.PehkuiHelperImpl {

	PehkuiHelper EMPTY = new EmptyPehkuiHelper();

	void setScale(LivingEntity livingEntity, float scale);

	void resetSize(LivingEntity livingEntity);

	void resize(LivingEntity livingEntity, float multiplier);

	float getScale(LivingEntity livingEntity);

	final class EmptyPehkuiHelper implements PehkuiHelper {
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

	}

}
