package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MawHopperModel extends AnimatedGeoModel<MawHopperBlockEntity> {

	protected static final ResourceLocation INPUT_MODEL = BiomancyMod.createRL("geo/block/maw_hopper.geo.json");
	protected static final ResourceLocation STRAIGHT_MODEL = BiomancyMod.createRL("geo/block/maw_hopper_connected_straight.geo.json");
	protected static final ResourceLocation CORNER_MODEL = BiomancyMod.createRL("geo/block/maw_hopper_connected_corner.geo.json");

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/maw_hopper.png");

	protected static final ResourceLocation INPUT_ANIMATION = BiomancyMod.createRL("animations/block/maw_hopper.animation.json");
	protected static final ResourceLocation CONNECTED_ANIMATION = BiomancyMod.createRL("animations/block/maw_hopper_connected.animation.json");

	@Override
	public ResourceLocation getModelResource(MawHopperBlockEntity blockEntity) {
		MawHopperBlock.Type type = MawHopperBlock.getType(blockEntity.getBlockState());
		return type == MawHopperBlock.Type.INPUT ? INPUT_MODEL : STRAIGHT_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(MawHopperBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(MawHopperBlockEntity blockEntity) {
		MawHopperBlock.Type type = MawHopperBlock.getType(blockEntity.getBlockState());
		return type == MawHopperBlock.Type.INPUT ? INPUT_ANIMATION : CONNECTED_ANIMATION;
	}

}
