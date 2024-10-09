package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SpecialCraftingRecipeMaker {

	private SpecialCraftingRecipeMaker() {}

	public static List<CraftingRecipe> createCradleCleansingRecipes() {
		ItemStack cradle = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();
		CompoundTag tag = new CompoundTag();
		CompoundTag tagProcGen = new CompoundTag();
		MoundShape.ProcGenValues procGenValues = new MoundShape.ProcGenValues(1234L, (byte) 0, (byte) 0, (byte) 1, 250, 66, 0.7f, 0.5f);
		procGenValues.writeTo(tagProcGen);
		tag.put(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY, tagProcGen);
		BlockItem.setBlockEntityData(cradle, ModBlockEntities.PRIMORDIAL_CRADLE.get(), tag);

		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(Ingredient.of(cradle));
		ingredients.add(Ingredient.of(ModItems.CLEANSING_SERUM.get()));
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ResourceLocation recipeId = ModItems.PRIMORDIAL_CRADLE.getId().withSuffix("_cleansing" + ".cleansed");

		ItemStack result = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();

		return List.of(
				new ShapelessRecipe(recipeId, "", CraftingBookCategory.MISC, result, inputs)
		);
	}

	public static List<CraftingRecipe> createPlayerHeadRecipes() {
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
				Ingredient.of(Items.PLAYER_HEAD),
				Ingredient.of(ModItems.EXOTIC_DUST.get()),
				Ingredient.of(createUniquePlayerEssence(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), "jeb_"))
		);
		ItemStack result = createPlayerHead("jeb_");

		return List.of(
				new ShapelessRecipe(BiomancyMod.createRL("special_crafting/player_head"), "", CraftingBookCategory.MISC, result, inputs)
		);
	}

	private static ItemStack createPlayerHead(String name) {
		GameProfile gameProfile = new GameProfile(null, name);

		ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(PlayerHeadItem.TAG_SKULL_OWNER, NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));

		return stack;
	}

	private static ItemStack createUniquePlayerEssence(UUID entityUUID, String playerName) {
		ItemStack stack = ModItems.ESSENCE.get().getDefaultInstance();
		CompoundTag tag = stack.getOrCreateTag();

		EntityType<?> entityType = EntityType.PLAYER;
		int[] colors = EssenceItem.getEssenceColors(entityUUID);

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());
		essenceTag.putString(EssenceItem.ENTITY_NAME_KEY, entityType.getDescriptionId());
		essenceTag.putUUID(EssenceItem.ENTITY_UUID_KEY, entityUUID);

		tag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
		tag.putInt(EssenceItem.ESSENCE_TIER_KEY, 3);
		tag.putIntArray(EssenceItem.COLORS_KEY, colors);
		tag.putString(EssenceItem.PLAYER_NAME_KEY, playerName);

		return stack;
	}

}
