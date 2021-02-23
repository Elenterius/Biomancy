package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

	//suppresses unknown recipe category warnings for our recipes
	@Inject(method = "getCategory", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;[Lorg/apache/logging/log4j/util/Supplier;)V"), cancellable = true)
	private static void getCategory(IRecipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
		IRecipeType<?> recipeType = recipe.getType();
		if (recipeType == ModRecipes.DECOMPOSING_RECIPE_TYPE || recipeType == ModRecipes.EVOLUTION_POOL_RECIPE_TYPE) {
			cir.setReturnValue(RecipeBookCategories.UNKNOWN);
		}
	}

}