package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.TintedFluidType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ModFluids {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BiomancyMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BiomancyMod.MOD_ID);

	private ModFluids() {}

	static void registerInteractions() {
	}

	private static <T extends Fluid> RegistryObject<T> register(String name, Supplier<T> factory) {
		return FLUIDS.register(name, factory);
	}

	private static RegistryObject<FluidType> registerTintedType(String name, int colorARGB, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new TintedFluidType(operator.apply(FluidType.Properties.create()), colorARGB));
	}

	private static RegistryObject<FluidType> registerType(String name, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new FluidType(operator.apply(FluidType.Properties.create())) {

			private final ResourceLocation stillTexture = BiomancyMod.createRL("fluid/%s_still".formatted(name));
			private final ResourceLocation flowingTexture = BiomancyMod.createRL("fluid/%s_flowing".formatted(name));
			private final ResourceLocation overlayTexture = BiomancyMod.createRL("fluid/%s_overlay".formatted(name));

			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(new IClientFluidTypeExtensions() {
					@Override
					public ResourceLocation getStillTexture() {
						return stillTexture;
					}

					@Override
					public ResourceLocation getFlowingTexture() {
						return flowingTexture;
					}

					@Override
					public ResourceLocation getOverlayTexture() {
						return overlayTexture;
					}

					@Override
					public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
						return overlayTexture;
					}
				});
			}
		});
	}
}
