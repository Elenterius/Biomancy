package com.github.elenterius.biomancy.client.render;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PartyTimeShaderHandler {

	private static int ticks = 0;
	private static Uniform time;

	private PartyTimeShaderHandler() {}

	private static Uniform getTimeUniform() {
		if (time == null) {
			time = ModRenderTypes.getEntityCutoutPartyTimeShader().getUniform("Time");
		}
		return time;
	}

	@SubscribeEvent
	static void onClientTick(final TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			ticks++;
		}
	}

	@SubscribeEvent
	static void onRenderTick(final TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			float totalTicks = ticks + event.renderTickTime;
			float t = totalTicks * 0.05f; //convert to seconds, ticks/20.0 ~= 1 sec
			getTimeUniform().set(t);
		}
	}

}
