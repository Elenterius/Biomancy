package com.github.elenterius.biomancy.client.renderer.entity.model;

import com.github.elenterius.biomancy.entity.golem.FleshkinEntity;
import net.minecraft.client.renderer.entity.model.AbstractZombieModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FleshkinModel<T extends FleshkinEntity> extends AbstractZombieModel<T> {

	protected FleshkinModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
		super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
	}

	public FleshkinModel(float modelSize, boolean isArmor) {
		super(modelSize, 0f, 64, isArmor ? 32 : 64);
	}

	@Override
	public boolean isAggressive(T entityIn) {
		return entityIn.isAggressive();
	}
}
