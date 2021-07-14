package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.fluid.simibubi.FluidIngredient;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidIngredientRecipe {

	FluidIngredient getFluidIngredient();

	boolean matches(IFluidHandler fluidHandler, World worldIn);

}
