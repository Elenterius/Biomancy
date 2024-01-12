package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.AcidFluid;
import com.github.elenterius.biomancy.fluid.TintedFluidType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModFluids {

	public static final TagKey<Fluid> ACID_TAG = tag("acid");
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BiomancyMod.MOD_ID);
	public static final Supplier<ForgeFlowingFluid.Properties> ACID_FLUID_PROPERTIES = () -> new ForgeFlowingFluid
			.Properties(ModFluids.ACID, ModFluids.FLOWING_ACID, TintedFluidType.builder().translationKey("biomancy:acid").color(0xFF_39FF14).density(1024).viscosity(1024))
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2)
			.block(ModBlocks.ACID_FLUID_BLOCK)
			.bucket(ModItems.ACID_BUCKET);

	public static final RegistryObject<ForgeFlowingFluid> FLOWING_ACID = register("flowing_acid", () -> new AcidFluid.Flowing(ACID_FLUID_PROPERTIES.get()));
	public static final RegistryObject<ForgeFlowingFluid> ACID = register("acid", () -> new AcidFluid.Source(ACID_FLUID_PROPERTIES.get()));

	private ModFluids() {}

	private static TagKey<Fluid> tag(String name) {
		return FluidTags.create(BiomancyMod.createRL(name));
	}

	static void registerInteractions() {
		//		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
		//				ForgeMod.WATER_TYPE.get(),
		//				fluidState -> fluidState.isSource() ? Blocks.CALCITE.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		//		));
		//		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
		//				ForgeMod.LAVA_TYPE.get(),
		//				fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		//		));
	}

	private static <T extends Fluid> RegistryObject<T> register(String name, Supplier<T> factory) {
		return FLUIDS.register(name, factory);
	}

}
