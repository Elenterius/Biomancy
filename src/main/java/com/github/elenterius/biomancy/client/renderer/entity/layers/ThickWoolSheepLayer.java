package com.github.elenterius.biomancy.client.renderer.entity.layers;

import com.github.elenterius.biomancy.entity.gmo.ThickWoolSheepEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThickWoolSheepLayer extends LayerRenderer<ThickWoolSheepEntity, SheepModel<ThickWoolSheepEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
	private final InflatedSheepWoolModel<ThickWoolSheepEntity> woolModel = new InflatedSheepWoolModel<>();

	public ThickWoolSheepLayer(IEntityRenderer<ThickWoolSheepEntity, SheepModel<ThickWoolSheepEntity>> rendererIn) {
		super(rendererIn);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ThickWoolSheepEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.getSheared() && !entity.isInvisible()) {
			float[] afloat = SheepEntity.getDyeRgb(entity.getFleeceColor());
			float r = afloat[0];
			float g = afloat[1];
			float b = afloat[2];

			woolModel.setBodyVisible(false);
			renderCopyCutoutModel(getEntityModel(), woolModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, r, g, b);

			matrixStackIn.push();
			float scale = 1f + (entity.getWoolSize() - 1f) / (float) (ThickWoolSheepEntity.MAX_WOOL_SIZE - 1);
			matrixStackIn.scale(scale, scale, 0.5f + scale * 0.5f);
			matrixStackIn.translate(0d, -(scale / 10d), scale * (scale * 0.6d) / 10d);
			woolModel.setBodyVisible(true);
			renderCopyCutoutModel(getEntityModel(), woolModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, r, g, b);
			matrixStackIn.pop();
		}
	}

	static class InflatedSheepWoolModel<T extends SheepEntity> extends SheepWoolModel<T> {
		void setBodyVisible(boolean flag) {
			body.showModel = flag;
			headModel.showModel = !flag;
			legBackRight.showModel = !flag;
			legBackLeft.showModel = !flag;
			legFrontRight.showModel = !flag;
			legFrontLeft.showModel = !flag;
		}
	}

}
