package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientAccessor {

	@Accessor("values")
	Ingredient.Value[] biomancy$values();

}
