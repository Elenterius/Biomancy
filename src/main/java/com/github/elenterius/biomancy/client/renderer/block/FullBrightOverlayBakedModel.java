package com.github.elenterius.biomancy.client.renderer.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FullBrightOverlayBakedModel implements IBakedModel {
	private final IBakedModel baseModel;

	public FullBrightOverlayBakedModel(IBakedModel baseModel) {
		this.baseModel = baseModel;
	}

	// IForgeBakedModel stuff //

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
		List<BakedQuad> quads = baseModel.getQuads(state, side, rand);
		if (MinecraftForgeClient.getRenderLayer() == RenderType.translucent()) {
			for (int i = 0; i < quads.size(); i++) {
				BakedQuad quad = quads.get(i);
				int[] vertexData = quad.getVertices();
				for (int j = 0; j < 4; j++) {
					vertexData[8 * j + 6] = getLightValue(15, 15);
				}
				quads.set(i, new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade()));
			}
		}
		return quads;
	}

	private static int getLightValue(int skyLighting, int blockLighting) {
		// 65536 = 2^16
		return 65536 * skyLighting * 16 + blockLighting * 16;
	}

// old IBakedModel stuff //

	@Override
	public boolean useAmbientOcclusion() {
		return baseModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return baseModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return baseModel.isCustomRenderer();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return baseModel.getOverrides();
	}

	//deprecated
	@Override
	public ItemCameraTransforms getTransforms() {
		return baseModel.getTransforms();
	}

	//deprecated
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
	}

	//deprecated
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return baseModel.getParticleIcon();
	}

}
