package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.AcidFluid;
import com.github.elenterius.biomancy.fluid.TintedFluidType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ModFluids {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BiomancyMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BiomancyMod.MOD_ID);

	public static final RegistryObject<TintedFluidType> ACID_TYPE = registerTintedType("acid", 0xFF_39FF14, properties -> properties.density(1024).viscosity(1024));
	public static final Supplier<ForgeFlowingFluid.Properties> ACID_FLUID_PROPERTIES = () -> new ForgeFlowingFluid
			.Properties(ACID_TYPE, ModFluids.ACID, ModFluids.FLOWING_ACID)
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2)
			.block(ModBlocks.ACID_FLUID_BLOCK)
			.bucket(ModItems.ACID_BUCKET);
	public static final RegistryObject<ForgeFlowingFluid> ACID = register("acid", () -> new AcidFluid.Source(ACID_FLUID_PROPERTIES.get()));
	public static final RegistryObject<ForgeFlowingFluid> FLOWING_ACID = register("flowing_acid", () -> new AcidFluid.Flowing(ACID_FLUID_PROPERTIES.get()));

	private ModFluids() {}

	static void registerInteractions() {
		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
				ForgeMod.WATER_TYPE.get(),
				fluidState -> fluidState.isSource() ? Blocks.CALCITE.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		));
		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
				ForgeMod.LAVA_TYPE.get(),
				fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		));
	}

	private static <T extends Fluid> RegistryObject<T> register(String name, Supplier<T> factory) {
		return FLUIDS.register(name, factory);
	}

	private static RegistryObject<TintedFluidType> registerTintedType(String name, int colorARGB, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new TintedFluidType(operator.apply(createFluidTypeProperties()), colorARGB));
	}

	private static RegistryObject<FluidType> registerType(String name, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new FluidType(operator.apply(createFluidTypeProperties())) {

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

	private static FluidType.Properties createFluidTypeProperties() {
		return FluidType.Properties.create()
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
	}

}
