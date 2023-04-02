package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.world.item.InjectorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class InjectorRenderer extends GeoItemRenderer<InjectorItem> {

	private TransformType currentTransformType;
	private int serumColor = -1;

	public InjectorRenderer() {
		super(new InjectorModel());
	}

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		currentTransformType = transformType;
		super.renderByItem(stack, transformType, poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void render(InjectorItem item, PoseStack poseStack, MultiBufferSource bufferIn, int packedLight, ItemStack itemStack) {
		serumColor = item.getSerumColor(itemStack);
		super.render(item, poseStack, bufferIn, packedLight, itemStack);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.name.equals("serum")) {
			renderSerumBone(bone, stack, buffer, packedLight, packedOverlay, 0.8f);
		}
		else if (bone.name.equals("serum_core")) {
			renderSerumBone(bone, stack, buffer, packedLight, packedOverlay, 1f);
		}
		else {
			super.renderRecursively(bone, stack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	private void renderSerumBone(GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, float alpha) {
		if (serumColor == -1) return; //don't render
		float r = FastColor.ARGB32.red(serumColor) / 255f;
		float g = FastColor.ARGB32.green(serumColor) / 255f;
		float b = FastColor.ARGB32.blue(serumColor) / 255f;
		super.renderRecursively(bone, stack, buffer, packedLight, packedOverlay, r, g, b, alpha);
	}

	@Override
	public RenderType getRenderType(InjectorItem item, float partialTicks, PoseStack stack, @Nullable MultiBufferSource buffer, @Nullable VertexConsumer vertexBuilder, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public int getInstanceId(InjectorItem item) {
		if (currentTransformType == TransformType.GUI) return -1; //don't render animation in GUI
		return super.getInstanceId(animatable);
	}

	public ItemStack getCurrentItemStack() {
		return currentItemStack;
	}

}
