package com.creativechasm.blightlings.client.renderer.entity.model;
//Made by Elenterius

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloblingModel<T extends Entity> extends SegmentedModel<T>
{
    private final ModelRenderer body;
    private final ModelRenderer blob;
    private final ModelRenderer head;
    private final ModelRenderer leftLeg0;
    private final ModelRenderer leftLeg2;
    private final ModelRenderer leftLeg3;
    private final ModelRenderer rightLeg0;
    private final ModelRenderer rightLeg1;
    private final ModelRenderer rightLeg2;

    public BloblingModel() {
        textureWidth = 16;
        textureHeight = 16;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 23.0F, 0.0F);
        body.setTextureOffset(0, 12).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        blob = new ModelRenderer(this);
        blob.setRotationPoint(0.5F, -1.0F, -0.5F);
        body.addChild(blob);
        setRotationAngle(blob, -0.0873F, 0.0F, 0.0F);
        blob.setTextureOffset(0, 0).addBox(-2.5F, -3.8F, -1.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -1.0F, -2.0F);
        body.addChild(head);
        head.setTextureOffset(0, 8).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(13, 0).addBox(-1.09F, -0.83F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, true);
        head.setTextureOffset(13, 0).addBox(0.61F, -0.83F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, true);
        head.setTextureOffset(14, 0).addBox(0.1625F, -0.2375F, -1.7125F, 0.625F, 0.625F, 0.625F, 0.0F, true);
        head.setTextureOffset(14, 0).addBox(-0.775F, -0.2375F, -1.7125F, 0.625F, 0.625F, 0.625F, 0.0F, true);

        leftLeg0 = new ModelRenderer(this);
        leftLeg0.setRotationPoint(1.5F, 0.0F, -1.25F);
        body.addChild(leftLeg0);
        setRotationAngle(leftLeg0, -0.4363F, 0.0F, -0.7854F);
        leftLeg0.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        leftLeg2 = new ModelRenderer(this);
        leftLeg2.setRotationPoint(1.5F, 0.0F, -0.25F);
        body.addChild(leftLeg2);
        setRotationAngle(leftLeg2, 0.0F, 0.0F, -0.7854F);
        leftLeg2.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

        leftLeg3 = new ModelRenderer(this);
        leftLeg3.setRotationPoint(1.5F, 0.0F, 0.75F);
        body.addChild(leftLeg3);
        setRotationAngle(leftLeg3, 0.4363F, 0.0F, -0.7854F);
        leftLeg3.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        rightLeg0 = new ModelRenderer(this);
        rightLeg0.setRotationPoint(-1.5F, 0.0F, -1.25F);
        body.addChild(rightLeg0);
        setRotationAngle(rightLeg0, -0.4363F, 0.0F, 0.7854F);
        rightLeg0.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        rightLeg1 = new ModelRenderer(this);
        rightLeg1.setRotationPoint(-1.5F, 0.0F, -0.25F);
        body.addChild(rightLeg1);
        setRotationAngle(rightLeg1, 0.0F, 0.0F, 0.7854F);
        rightLeg1.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

        rightLeg2 = new ModelRenderer(this);
        rightLeg2.setRotationPoint(-1.5F, 0.0F, 0.75F);
        body.addChild(rightLeg2);
        setRotationAngle(rightLeg2, 0.4363F, 0.0F, 0.7854F);
        rightLeg2.setTextureOffset(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        float f = 0.7854F;
        leftLeg0.rotateAngleZ = leftLeg2.rotateAngleZ = leftLeg3.rotateAngleZ = -f;
        rightLeg0.rotateAngleZ = rightLeg1.rotateAngleZ = rightLeg2.rotateAngleZ = f;
        leftLeg0.rotateAngleX = rightLeg0.rotateAngleX = -0.4363F;
        leftLeg2.rotateAngleX = rightLeg1.rotateAngleX = 0;
        leftLeg3.rotateAngleX = rightLeg2.rotateAngleX = 0.4363F;
        float m = 1.6f;
        float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + 0F) * m) * limbSwingAmount;
        float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + (float) Math.PI) * m) * limbSwingAmount;
        float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
        float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
        float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0F) * m) * limbSwingAmount;
        float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * m) * limbSwingAmount;
        float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
        float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
        leftLeg0.rotateAngleX += f3;
        leftLeg2.rotateAngleX += -f3;
        leftLeg3.rotateAngleX += f4;
        rightLeg0.rotateAngleX += -f5;
        rightLeg1.rotateAngleX += f6;
        rightLeg2.rotateAngleX += -f6;
        leftLeg0.rotateAngleZ += f7;
        leftLeg2.rotateAngleZ += -f7;
        leftLeg3.rotateAngleZ += f8;
        rightLeg0.rotateAngleZ += -f9;
        rightLeg1.rotateAngleZ += f10;
        rightLeg2.rotateAngleZ += -f10;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(body);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}