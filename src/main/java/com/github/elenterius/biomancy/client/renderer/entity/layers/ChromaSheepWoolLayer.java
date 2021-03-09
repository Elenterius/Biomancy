package com.github.elenterius.biomancy.client.renderer.entity.layers;

import com.github.elenterius.biomancy.entity.mutation.ChromaSheepEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChromaSheepWoolLayer extends LayerRenderer<ChromaSheepEntity, SheepModel<ChromaSheepEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
	private final SheepWoolModel<ChromaSheepEntity> model = new SheepWoolModel<>();

	public ChromaSheepWoolLayer(IEntityRenderer<ChromaSheepEntity, SheepModel<ChromaSheepEntity>> rendererIn) {
		super(rendererIn);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ChromaSheepEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.getSheared() && !entity.isInvisible()) {
			int nColors = DyeColor.values().length;
			int colorTicks = entity.ticksExisted / 25 + entity.getEntityId();
			float[] colorA = SheepEntity.getDyeRgb(DyeColor.byId(colorTicks % nColors));
			float[] colorB = SheepEntity.getDyeRgb(DyeColor.byId((colorTicks + 1) % nColors));
			float shift = ((float) (entity.ticksExisted % 25) + partialTicks) / 25f;
			float r = colorA[0] * (1f - shift) + colorB[0] * shift;
			float g = colorA[1] * (1f - shift) + colorB[1] * shift;
			float b = colorA[2] * (1f - shift) + colorB[2] * shift;

			renderCopyCutoutModel(getEntityModel(), model, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, r, g, b);
		}
	}
}
