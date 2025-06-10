package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModPaintings {

	public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<PaintingVariant> JERRY_PROVIDES = PAINTINGS.register("jerry_provides", () -> new PaintingVariant(16, 16));

	private ModPaintings() {}

}
