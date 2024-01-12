package com.github.elenterius.biomancy.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.function.BiFunction;

public class TintedFluidType extends FluidAttributes {

	public static final ResourceLocation STILL_TEXTURE = new ResourceLocation("block/water_still");
	public static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation("block/water_flow");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("block/water_overlay");
	public static final ResourceLocation INSIDE_FLUID_TEXTURE = new ResourceLocation("textures/block/water_overlay.png");

	protected TintedFluidType(FluidAttributes.Builder properties, Fluid fluid) {
		super(properties, fluid);
	}

	public static FluidAttributes.Builder builder() {
		return new Builder(TintedFluidType::new);
	}

	public static class Builder extends FluidAttributes.Builder {
		protected Builder(BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
			super(TintedFluidType.STILL_TEXTURE, TintedFluidType.FLOWING_TEXTURE, factory);
			overlay(OVERLAY_TEXTURE);
		}

	}

	//	public static void renderTintedScreenOverlay(Minecraft minecraft, PoseStack poseStack, ResourceLocation texture, int colorARGB) {
	//		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	//		RenderSystem.enableTexture();
	//		RenderSystem.setShaderTexture(0, texture);
	//
	//		BlockPos blockPos = new BlockPos(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
	//		float brightness = LightTexture.getBrightness(minecraft.player.level.dimensionType(), minecraft.player.level.getMaxLocalRawBrightness(blockPos));
	//		float red = (FastColor.ARGB32.red(colorARGB) / 255f) * brightness;
	//		float green = (FastColor.ARGB32.green(colorARGB) / 255f) * brightness;
	//		float blue = (FastColor.ARGB32.blue(colorARGB) / 255f) * brightness;
	//
	//		RenderSystem.enableBlend();
	//		RenderSystem.defaultBlendFunc();
	//		RenderSystem.setShaderColor(red, green, blue, 0.5f);
	//
	//		float uOffset = -minecraft.player.getYRot() / 64f;
	//		float vOffset = minecraft.player.getXRot() / 64f;
	//		float depth = -0.5f;
	//
	//		Matrix4f matrix4f = poseStack.last().pose();
	//		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
	//		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
	//		bufferbuilder.vertex(matrix4f, -1f, -1f, depth).uv(4f + uOffset, 4f + vOffset).endVertex();
	//		bufferbuilder.vertex(matrix4f, 1f, -1f, depth).uv(uOffset, 4f + vOffset).endVertex();
	//		bufferbuilder.vertex(matrix4f, 1f, 1f, depth).uv(uOffset, vOffset).endVertex();
	//		bufferbuilder.vertex(matrix4f, -1f, 1f, depth).uv(4f + uOffset, vOffset).endVertex();
	//		bufferbuilder.end();
	//		BufferUploader.end(bufferbuilder);
	//
	//		RenderSystem.disableBlend();
	//	}

	//	@Override
	//	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
	//		consumer.accept(new IClientFluidTypeExtensions() {
	//			@Override
	//			public int getTintColor() {
	//				return colorARGB;
	//			}
	//
	//			@Override
	//			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
	//				float red = (FastColor.ARGB32.red(colorARGB) / 255f) * fluidFogColor.x();
	//				float green = (FastColor.ARGB32.green(colorARGB) / 255f) * fluidFogColor.y();
	//				float blue = (FastColor.ARGB32.blue(colorARGB) / 255f) * fluidFogColor.z();
	//				fluidFogColor.set(red, green, blue);
	//				return fluidFogColor;
	//			}
	//
	//			@Override
	//			public ResourceLocation getStillTexture() {
	//				return STILL_TEXTURE;
	//			}
	//
	//			@Override
	//			public ResourceLocation getFlowingTexture() {
	//				return FLOWING_TEXTURE;
	//			}
	//
	//			@Override
	//			public ResourceLocation getOverlayTexture() {
	//				return OVERLAY_TEXTURE;
	//			}
	//
	//			@Override
	//			public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
	//				return INSIDE_FLUID_TEXTURE;
	//			}
	//
	//			@Override
	//			public void renderOverlay(Minecraft mc, PoseStack poseStack) {
	//				renderTintedScreenOverlay(mc, poseStack, INSIDE_FLUID_TEXTURE, colorARGB);
	//			}
	//		});
	//	}
}
