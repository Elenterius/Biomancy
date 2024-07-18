package com.github.elenterius.biomancy.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class TintedFluidType extends FluidType {

	protected static final ResourceLocation STILL_TEXTURE = new ResourceLocation("block/water_still");
	protected static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation("block/water_flow");
	protected static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("block/water_overlay");
	protected static final ResourceLocation INSIDE_FLUID_TEXTURE = new ResourceLocation("textures/block/water_overlay.png");

	protected final int colorARGB;

	public TintedFluidType(Properties properties, int colorARGB) {
		super(properties);
		this.colorARGB = colorARGB;
	}

	public static void renderTintedScreenOverlay(Minecraft minecraft, PoseStack poseStack, ResourceLocation texture, int colorARGB) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);

		LocalPlayer player = minecraft.player;

		BlockPos blockPos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
		float brightness = LightTexture.getBrightness(player.level().dimensionType(), player.level().getMaxLocalRawBrightness(blockPos));
		float red = (FastColor.ARGB32.red(colorARGB) / 255f) * brightness;
		float green = (FastColor.ARGB32.green(colorARGB) / 255f) * brightness;
		float blue = (FastColor.ARGB32.blue(colorARGB) / 255f) * brightness;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(red, green, blue, 0.5f);

		float uOffset = -player.getYRot() / 64f;
		float vOffset = player.getXRot() / 64f;
		float depth = -0.5f;

		Matrix4f matrix4f = poseStack.last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, -1f, -1f, depth).uv(4f + uOffset, 4f + vOffset).endVertex();
		bufferbuilder.vertex(matrix4f, 1f, -1f, depth).uv(uOffset, 4f + vOffset).endVertex();
		bufferbuilder.vertex(matrix4f, 1f, 1f, depth).uv(uOffset, vOffset).endVertex();
		bufferbuilder.vertex(matrix4f, -1f, 1f, depth).uv(4f + uOffset, vOffset).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public int getTintColor() {
				return colorARGB;
			}

			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				float red = (FastColor.ARGB32.red(colorARGB) / 255f) * fluidFogColor.x();
				float green = (FastColor.ARGB32.green(colorARGB) / 255f) * fluidFogColor.y();
				float blue = (FastColor.ARGB32.blue(colorARGB) / 255f) * fluidFogColor.z();
				fluidFogColor.set(red, green, blue);
				return fluidFogColor;
			}

			@Override
			public ResourceLocation getStillTexture() {
				return STILL_TEXTURE;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return FLOWING_TEXTURE;
			}

			@Override
			public ResourceLocation getOverlayTexture() {
				return OVERLAY_TEXTURE;
			}

			@Override
			public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
				return INSIDE_FLUID_TEXTURE;
			}

			@Override
			public void renderOverlay(Minecraft mc, PoseStack poseStack) {
				renderTintedScreenOverlay(mc, poseStack, INSIDE_FLUID_TEXTURE, colorARGB);
			}
		});
	}

	public int getTintColor() {
		return colorARGB;
	}

}
