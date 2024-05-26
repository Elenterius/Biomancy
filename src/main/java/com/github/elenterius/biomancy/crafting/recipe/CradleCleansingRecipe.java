package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CradleCleansingRecipe extends CustomRecipe {

	public CradleCleansingRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(CraftingContainer inventory, Level level) {
		ItemStack cradle = ItemStack.EMPTY;
		ItemStack cleansingSerum = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item == ModItems.PRIMORDIAL_CRADLE.get()) {
				if (!cradle.isEmpty()) {
					return false;
				}
				cradle = stack;
			}
			else if (item == ModItems.CLEANSING_SERUM.get()) {
				if (!cleansingSerum.isEmpty()) {
					return false;
				}
				cleansingSerum = stack;
			}
		}

		return !cradle.isEmpty() && !cleansingSerum.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess) {
		ItemStack cradle = ItemStack.EMPTY;
		ItemStack cleansingSerum = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item == ModItems.PRIMORDIAL_CRADLE.get()) {
				if (!cradle.isEmpty()) {
					return ItemStack.EMPTY;
				}
				cradle = stack.copy();
			}
			else if (item == ModItems.CLEANSING_SERUM.get()) {
				if (!cleansingSerum.isEmpty()) {
					return ItemStack.EMPTY;
				}
				cleansingSerum = stack;
			}
		}

		return !cradle.isEmpty() && !cleansingSerum.isEmpty() ? createItem(cradle) : ItemStack.EMPTY;
	}

	private ItemStack createItem(ItemStack stack) {
		CompoundTag tag = BlockItem.getBlockEntityData(stack);
		if (tag != null && tag.contains(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY)) {
			tag.remove(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY);
		}
		return stack;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.CRADLE_CLEANSING_SERIALIZER.get();
	}

}
