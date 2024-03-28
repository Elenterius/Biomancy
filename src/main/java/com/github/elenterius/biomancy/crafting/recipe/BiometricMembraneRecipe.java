package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlockEntity;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.EssenceItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class BiometricMembraneRecipe extends CustomRecipe {

	public BiometricMembraneRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		if (!canCraftInDimensions(container.getWidth(), container.getHeight())) return false;

		ItemStack membrane = ItemStack.EMPTY;
		ItemStack essence = ItemStack.EMPTY;
		ItemStack inversion = ItemStack.EMPTY;

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ModItems.BIOMETRIC_MEMBRANE.get())) {
				if (!membrane.isEmpty()) return false;
				membrane = stack;
			}
			else if (stack.getItem() instanceof EssenceItem) {
				if (!essence.isEmpty()) return false;
				essence = stack;
			}
			else if (stack.is(Items.REDSTONE_TORCH)) {
				if (!inversion.isEmpty()) return false;
				inversion = stack;
			}
		}

		if (membrane.isEmpty()) return false;

		if (essence.isEmpty() && inversion.isEmpty()) {
			return BlockItem.getBlockEntityData(membrane) != null;
		}

		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
		ItemStack membrane = ItemStack.EMPTY;
		ItemStack essence = ItemStack.EMPTY;
		ItemStack inversion = ItemStack.EMPTY;

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ModItems.BIOMETRIC_MEMBRANE.get())) {
				if (!membrane.isEmpty()) return ItemStack.EMPTY;
				membrane = stack;
			}
			else if (stack.getItem() instanceof EssenceItem) {
				if (!essence.isEmpty()) return ItemStack.EMPTY;
				essence = stack;
			}
			else if (stack.is(Items.REDSTONE_TORCH)) {
				if (!inversion.isEmpty()) return ItemStack.EMPTY;
				inversion = stack;
			}
		}

		return !membrane.isEmpty() ? createItem(membrane, essence, inversion) : ItemStack.EMPTY;
	}

	public ItemStack createItem(ItemStack membrane, ItemStack essenceStack, ItemStack inversionStack) {
		CompoundTag compoundTag = BlockItem.getBlockEntityData(membrane);
		CompoundTag tag = compoundTag != null ? compoundTag.getCompound(BiometricMembraneBlockEntity.MEMBRANE_KEY) : new CompoundTag();

		EntityType<?> entityType = EntityType.byString(tag.getString(BiometricMembraneBlockEntity.ENTITY_TYPE_KEY)).orElse(null);
		UUID entityUUID = tag.hasUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY) ? tag.getUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY) : null;
		int[] entityColors = tag.contains(BiometricMembraneBlockEntity.ENTITY_COLORS_KEY, Tag.TAG_INT_ARRAY) ? tag.getIntArray(BiometricMembraneBlockEntity.ENTITY_COLORS_KEY) : BiometricMembraneBlockEntity.DEFAULT_COLORS;
		boolean isInverted = tag.getBoolean(BiometricMembraneBlockEntity.IS_INVERTED_KEY);

		if (essenceStack.getItem() instanceof EssenceItem essenceItem) {
			Optional<EntityType<?>> optional = essenceItem.getEntityType(essenceStack);
			if (optional.isPresent()) {
				entityType = optional.orElse(null);
				entityUUID = essenceItem.getEntityUUID(essenceStack).orElse(null);
				entityColors = essenceItem.getColors(essenceStack);
			}
		}

		if (inversionStack.is(Items.REDSTONE_TORCH)) {
			isInverted = !isInverted;
		}

		if (essenceStack.isEmpty() && inversionStack.isEmpty()) {
			return new ItemStack(ModItems.BIOMETRIC_MEMBRANE.get());
		}

		return BiometricMembraneBlock.createItem(entityType, entityUUID, entityColors, isInverted);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 4;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIOMETRIC_MEMBRANE_CRAFTING_SERIALIZER.get();
	}

}
