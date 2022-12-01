package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.client.model.item.InjectorModel;
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
		super.render(item, poseStack, bufferIn, packedLight, itemStack);

		//render "last"
//		if (currentTransformType != TransformType.GUI) {
//			LocalPlayer player = Minecraft.getInstance().player;
//			if (player == null) return;
//			RenderSystem.setShaderTexture(0, player.getSkinTextureLocation());
//			PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
//
//			AnimatedGeoModel<InjectorItem> model = getGeoModelProvider();
//			AnimationProcessor<?> animationProcessor = model.getAnimationProcessor();
//			IBone leftArmBone = animationProcessor.getBone("leftArm");
//
//			poseStack.pushPose();
//			poseStack.translate(0, 0.01f, 0);
//			poseStack.translate(0.5, 0.5, 0.5);
//
//			translate(leftArmBone, poseStack);
//			moveToPivot(leftArmBone, poseStack);
//			rotate(leftArmBone, poseStack);
//			scale(leftArmBone, poseStack);
//			moveBackFromPivot(leftArmBone, poseStack);
//			playerRenderer.renderLeftHand(poseStack, bufferIn, packedLight, player);
//			poseStack.popPose();
//		}
	}

//	public static void moveToPivot(IBone bone, PoseStack stack) {
//		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
//	}
//
//	public static void moveBackFromPivot(IBone bone, PoseStack stack) {
//		stack.translate(-bone.getPivotX() / 16, -bone.getPivotY() / 16, -bone.getPivotZ() / 16);
//	}
//
//	public static void scale(IBone bone, PoseStack stack) {
//		stack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
//	}
//
//	public static void translate(IBone bone, PoseStack stack) {
//		stack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
//	}
//
//	public static void rotate(IBone bone, PoseStack stack) {
//		if (bone.getRotationZ() != 0f) stack.mulPose(Vector3f.ZP.rotation(bone.getRotationZ()));
//		if (bone.getRotationY() != 0f) stack.mulPose(Vector3f.YP.rotation(bone.getRotationY()));
//		if (bone.getRotationX() != 0f) stack.mulPose(Vector3f.XP.rotation(bone.getRotationX()));
//	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.name.equals("serum") && currentItemStack.getItem() instanceof InjectorItem injector) {
			int serumColor = injector.getSerumColor(currentItemStack);
//			if (serumColor == -1) return; //don't render? :)
			float r = FastColor.ARGB32.red(serumColor) / 255f;
			float g = FastColor.ARGB32.green(serumColor) / 255f;
			float b = FastColor.ARGB32.blue(serumColor) / 255f;
			super.renderRecursively(bone, stack, buffer, packedLight, packedOverlay, r, g, b, 0.85f);
		}
		else {
			super.renderRecursively(bone, stack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
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
