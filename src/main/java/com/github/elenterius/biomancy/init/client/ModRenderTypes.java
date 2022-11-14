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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRenderTypes {

	@Nullable
	private static ShaderInstance entityCutoutPartyTimeShader;

	private ModRenderTypes() {}

	@SubscribeEvent
	public static void onClientSetup(RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceManager(), BiomancyMod.createRL("entity_cutout_party_time"), DefaultVertexFormat.NEW_ENTITY), instance -> entityCutoutPartyTimeShader = instance);
	}

	public static RenderType getCutoutPartyTime(ResourceLocation textureLocation) {
		return ModRenderType.ENTITY_CUTOUT_PARTY_TIME.apply(textureLocation);
	}

	public static ShaderInstance getEntityCutoutPartyTimeShader() {
		return Objects.requireNonNull(entityCutoutPartyTimeShader, "Attempted to call getEntityCutoutPartyTimeShader before shaders have finished loading.");
	}

	private static final class ModRenderType extends RenderType {
		private static final RenderStateShard.ShaderStateShard RENDER_TYPE_ENTITY_CUTOUT_PARTY_TIME_SHADER = new RenderStateShard.ShaderStateShard(ModRenderTypes::getEntityCutoutPartyTimeShader);

		private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_PARTY_TIME = Util.memoize(tex -> {
			RenderType.CompositeState renderState = RenderType.CompositeState.builder()
					.setShaderState(RENDER_TYPE_ENTITY_CUTOUT_PARTY_TIME_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
					.setTransparencyState(NO_TRANSPARENCY)
					.setLightmapState(LIGHTMAP)
					.setOverlayState(OVERLAY)
					.createCompositeState(true);
			return create("biomancy_entity_cutout_party_time", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, renderState);
		});

		private ModRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
			super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
		}
	}

}
