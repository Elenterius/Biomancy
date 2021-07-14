package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModFluids {

	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BiomancyMod.MOD_ID);

	public static final RegistryObject<ForgeFlowingFluid> NUTRIENT_SLURRY = FLUIDS.register("nutrient_slurry", () -> new ForgeFlowingFluid.Source(createNutrientSlurryProp()));
	public static final RegistryObject<ForgeFlowingFluid> NUTRIENT_SLURRY_FLOWING = FLUIDS.register("nutrient_slurry_flowing", () -> new ForgeFlowingFluid.Flowing(createNutrientSlurryProp()));

	private ModFluids() {}

	@OnlyIn(Dist.CLIENT)
	protected static void setRenderLayers() {
		RenderTypeLookup.setRenderLayer(NUTRIENT_SLURRY.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(NUTRIENT_SLURRY_FLOWING.get(), RenderType.getTranslucent());
	}

	private static ForgeFlowingFluid.Properties createNutrientSlurryProp() {
		return new ForgeFlowingFluid.Properties(NUTRIENT_SLURRY, NUTRIENT_SLURRY_FLOWING,
				createFluidAttribute("nutrient_slurry")/*.color(0xFF60963A)*/.viscosity(1024).density(1024))
				.bucket(ModItems.NUTRIENT_SLURRY_BUCKET)
				.block(ModBlocks.NUTRIENT_SLURRY_FLUID);
	}

	private static FluidAttributes.Builder createFluidAttribute(String name) {
		return FluidAttributes.builder(BiomancyMod.createRL(String.format("block/%s_still", name)), BiomancyMod.createRL(String.format("block/%s_flowing", name)))
				.translationKey(TextUtil.getTranslationKey("fluid", name))
				.sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
				.overlay(BiomancyMod.createRL(String.format("block/%s_overlay", name)));
	}

}
