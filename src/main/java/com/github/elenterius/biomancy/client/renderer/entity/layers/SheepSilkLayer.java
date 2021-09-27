package com.github.elenterius.biomancy.client.renderer.entity.layers;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.SheepSilkModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepSilkLayer<T extends SheepEntity> extends LayerRenderer<T, SheepModel<T>> {

	private static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/sheep/sheep_silk.png");
	private final SheepSilkModel<T> silkModel = new SheepSilkModel<>();

	public SheepSilkLayer(IEntityRenderer<T, SheepModel<T>> rendererIn) {
		super(rendererIn);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.isSheared() && !entity.isInvisible()) {
			float[] color = SheepEntity.getColorArray(entity.getColor());
			coloredCutoutModelCopyLayerRender(getParentModel(), silkModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, color[0], color[1], color[2]);
		}
	}

}
