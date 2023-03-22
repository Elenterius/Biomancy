package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MawHopperModel extends AnimatedGeoModel<MawHopperBlockEntity> {

	protected static final ResourceLocation NORMAL_MODEL = BiomancyMod.createRL("geo/block/maw_hopper.geo.json");
	protected static final ResourceLocation CONNECTED_MODEL = BiomancyMod.createRL("geo/block/maw_hopper_connected.geo.json");

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/maw_hopper.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/maw_hopper.animation.json");

	@Override
	public ResourceLocation getModelResource(MawHopperBlockEntity blockEntity) {
		MawHopperBlock.Shape shape = MawHopperBlock.getShape(blockEntity.getBlockState());
		return shape == MawHopperBlock.Shape.NORMAL ? NORMAL_MODEL : CONNECTED_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(MawHopperBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(MawHopperBlockEntity blockEntity) {
		return ANIMATION;
	}

}
