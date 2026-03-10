package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRenderTypes {

	@Nullable
	private static ShaderInstance entityCutoutPartyTimeShader;

	private ModRenderTypes() {}

	@SubscribeEvent
	public static void registerShaders(final RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceProvider(), BiomancyMod.rl("entity_cutout_party_time"), DefaultVertexFormat.NEW_ENTITY), instance -> entityCutoutPartyTimeShader = instance);
	}

	public static RenderType getCutoutPartyTime(ResourceLocation textureLocation) {
		return ModRenderType.ENTITY_CUTOUT_PARTY_TIME.apply(textureLocation);
	}

	public static ShaderInstance getEntityCutoutPartyTimeShader() {
		return Objects.requireNonNull(entityCutoutPartyTimeShader, "Attempted to call getEntityCutoutPartyTimeShader before shaders have finished loading.");
	}

	public static RenderType trail() {
		return ModRenderType.PROJECTILE_TRAIL;
	}

	public static RenderType lineStrip() {
		return ModRenderType.LINE_STRIP;
	}

	private static final class ModRenderType extends RenderType {
		private static final RenderStateShard.ShaderStateShard RENDER_TYPE_ENTITY_CUTOUT_PARTY_TIME_SHADER = new RenderStateShard.ShaderStateShard(ModRenderTypes::getEntityCutoutPartyTimeShader);

		private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_PARTY_TIME = Util.memoize(tex -> {
			CompositeState renderState = CompositeState.builder()
					.setShaderState(RENDER_TYPE_ENTITY_CUTOUT_PARTY_TIME_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
					.setTransparencyState(NO_TRANSPARENCY)
					.setLightmapState(LIGHTMAP)
					.setOverlayState(OVERLAY)
					.createCompositeState(true);
			return create("biomancy_entity_cutout_party_time", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, renderState);
		});

		private static final RenderType PROJECTILE_TRAIL = create(
				"biomancy_projectile_trail",
				DefaultVertexFormat.POSITION_COLOR,
				VertexFormat.Mode.TRIANGLE_STRIP,
				256,
				false,
				true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
						.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
						.setCullState(RenderStateShard.NO_CULL)
						.createCompositeState(false)
		);

		private static final RenderType LINE_STRIP = create(
				"biomancy_line_strip",
				DefaultVertexFormat.POSITION_COLOR_NORMAL,
				VertexFormat.Mode.LINE_STRIP,
				256,
				false, false,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_LINES_SHADER)
						.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
						.setLayeringState(VIEW_OFFSET_Z_LAYERING)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setOutputState(ITEM_ENTITY_TARGET)
						.setWriteMaskState(COLOR_DEPTH_WRITE)
						.setCullState(NO_CULL)
						.createCompositeState(false));

		private ModRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
			super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
		}
	}

}
