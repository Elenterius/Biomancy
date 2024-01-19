package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InjectorRenderer extends GeoItemRenderer<InjectorItem> {

	private ItemDisplayContext currentTransformType;
	private int serumColor = -1;

	public InjectorRenderer() {
		super(new InjectorModel());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		currentTransformType = displayContext;
		super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void preRender(PoseStack poseStack, InjectorItem item, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		serumColor = item.getSerum(getCurrentItemStack()).getColor();
		super.preRender(poseStack, item, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, InjectorItem item, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("_serum_core")) {
			renderSerumBone(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, alpha);
		}
		else {
			super.renderRecursively(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	private void renderSerumBone(PoseStack poseStack, InjectorItem item, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float alpha) {
		if (serumColor == -1) return; //don't render
		float r = FastColor.ARGB32.red(serumColor) / 255f;
		float g = FastColor.ARGB32.green(serumColor) / 255f;
		float b = FastColor.ARGB32.blue(serumColor) / 255f;
		super.renderRecursively(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, r, g, b, alpha);
	}

	//	@Override
	//	protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
	//		//don't render in GUI
	//	}

	@Override
	public long getInstanceId(InjectorItem item) {
		if (currentTransformType == ItemDisplayContext.GUI) return -1L; //don't render animation in GUI
		return super.getInstanceId(animatable);
	}

}
