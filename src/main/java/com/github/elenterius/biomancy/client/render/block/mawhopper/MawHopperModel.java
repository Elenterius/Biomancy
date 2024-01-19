package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlockEntity;
import com.github.elenterius.biomancy.block.property.VertexType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MawHopperModel extends GeoModel<MawHopperBlockEntity> {

	protected static final ResourceLocation SOURCE_MODEL = BiomancyMod.createRL("geo/block/maw_hopper.geo.json");
	protected static final ResourceLocation STRAIGHT_MODEL = BiomancyMod.createRL("geo/block/maw_hopper_connected_straight.geo.json");
	protected static final ResourceLocation CORNER_MODEL = BiomancyMod.createRL("geo/block/maw_hopper_connected_corner.geo.json");

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/maw_hopper.png");

	protected static final ResourceLocation INPUT_ANIMATION = BiomancyMod.createRL("animations/block/maw_hopper.animation.json");
	protected static final ResourceLocation CONNECTED_ANIMATION = BiomancyMod.createRL("animations/block/maw_hopper_connected.animation.json");

	@Override
	public ResourceLocation getModelResource(MawHopperBlockEntity blockEntity) {
		VertexType vertexType = MawHopperBlock.getVertexType(blockEntity.getBlockState());
		if (vertexType == VertexType.SOURCE) return SOURCE_MODEL;

		return MawHopperBlock.getConnection(blockEntity.getBlockState()).isCorner() ? CORNER_MODEL : STRAIGHT_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(MawHopperBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(MawHopperBlockEntity blockEntity) {
		VertexType vertexType = MawHopperBlock.getVertexType(blockEntity.getBlockState());
		if (vertexType == VertexType.SOURCE) return INPUT_ANIMATION;

		return CONNECTED_ANIMATION;
	}

	@Override
	public RenderType getRenderType(MawHopperBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
