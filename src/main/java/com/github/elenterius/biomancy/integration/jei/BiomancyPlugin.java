package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.inventory.*;
import com.google.common.collect.ImmutableSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

@JeiPlugin
public class BiomancyPlugin implements IModPlugin {

	private static final ResourceLocation ID = BiomancyMod.createRL("plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ModItems.REAGENT.get(), ReagentSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new ChewerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DigesterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DecomposerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SmallEvolutionPoolRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SolidifierRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientWorld world = Objects.requireNonNull(Minecraft.getInstance().level);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.CHEWER_RECIPE_TYPE)), ChewerRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.DIGESTER_RECIPE_TYPE)), DigesterRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.DECOMPOSING_RECIPE_TYPE)), DecomposerRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.EVOLUTION_POOL_RECIPE_TYPE)), SmallEvolutionPoolRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.SOLIDIFIER_RECIPE_TYPE)), SolidifierRecipeCategory.ID);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ChewerContainerScreen.class, 90, 29, 14, 10, ChewerRecipeCategory.ID);
		registration.addRecipeClickArea(DecomposerContainerScreen.class, 81, 20, 14, 10, DecomposerRecipeCategory.ID);
		registration.addRecipeClickArea(DigesterContainerScreen.class, 81, 29, 14, 10, DigesterRecipeCategory.ID);
		registration.addRecipeClickArea(EvolutionPoolContainerScreen.class, 108, 29, 14, 10, SmallEvolutionPoolRecipeCategory.ID);
		registration.addRecipeClickArea(SolidifierContainerScreen.class, 81, 33, 14, 10, SolidifierRecipeCategory.ID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registerInputSlots(registration, ChewerContainer.class, ChewerRecipeCategory.ID, ChewerContainer.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, DecomposerContainer.class, DecomposerRecipeCategory.ID, DecomposerContainer.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, DigesterContainer.class, DigesterRecipeCategory.ID, DigesterContainer.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, EvolutionPoolContainer.class, SmallEvolutionPoolRecipeCategory.ID, EvolutionPoolContainer.SlotZone.INPUT_ZONE);
	}

	private <C extends Container> void registerInputSlots(IRecipeTransferRegistration registration, Class<C> containerClass, ResourceLocation recipeCategoryUid, ISlotZone slotZone) {
		registration.addRecipeTransferHandler(containerClass, recipeCategoryUid, slotZone.getFirstIndex(), slotZone.getSlotCount(), 0, 36);
	}
}
