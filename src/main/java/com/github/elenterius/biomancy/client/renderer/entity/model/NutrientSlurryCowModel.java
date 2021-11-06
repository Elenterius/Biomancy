package com.github.elenterius.biomancy.client.renderer.entity.model;

import com.github.elenterius.biomancy.entity.mutation.NutrientSlurryCowEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NutrientSlurryCowModel extends CowModel<NutrientSlurryCowEntity> {

	private static final int COLOR = 0xCCD65B;

	protected final ModelRenderer udder;
	protected final ModelRenderer udder2;
	protected float slurryPct;

	private float headRotationAngleX;

	public NutrientSlurryCowModel() {
		body = new ModelRenderer(this, 18, 4);
		body.addBox(-6f, -10f, -7f, 12f, 18f, 10f, 0f);
		body.setPos(0f, 5f, 2f);
		body.texOffs(52, 0);

		udder = new ModelRenderer(this, 18, 4);
		udder.texOffs(52, 0);
		udder.addBox(-2f, 2f, -8f, 4f, 6f, 1f);

		udder2 = new ModelRenderer(this, 18, 4);
		udder2.setPos(0f, 0f, -1f);
		udder2.texOffs(52, 0);
		udder2.addBox(-2f, 2f, -8f, 4f, 6f, 1f);

		body.addChild(udder);
		body.addChild(udder2);
	}

	@Override
	public void prepareMobModel(NutrientSlurryCowEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		head.y = 4f + entity.getHeadRotationPointY(partialTick) * 9f;
		headRotationAngleX = entity.getHeadRotationAngleX(partialTick);
		slurryPct = entity.getSlurryAmount() / 200f;
		udder.visible = slurryPct >= 0.5f;
		udder2.visible = slurryPct >= 1f;
	}

	@Override
	public void setupAnim(NutrientSlurryCowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		head.xRot = headRotationAngleX;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		red = (COLOR >> 16 & 255) / 255f;
		green = (COLOR >> 8 & 255) / 255f;
		blue = (COLOR & 255) / 255f;
		super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}
