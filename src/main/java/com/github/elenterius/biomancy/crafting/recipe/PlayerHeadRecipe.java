package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PlayerHeadRecipe extends CustomRecipe {

	public PlayerHeadRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(CraftingContainer inventory, Level level) {
		boolean hasPlayerHead = false;
		boolean hasPlayerUUID = false;
		boolean hasExoticDust = false;

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item == Items.PLAYER_HEAD) {
				if (hasPlayerHead) return false;
				hasPlayerHead = true;
			}
			else if (item == ModItems.EXOTIC_DUST.get()) {
				if (hasExoticDust) return false;
				hasExoticDust = true;
			}
			else if (item instanceof EssenceItem essenceItem) {
				if (hasPlayerUUID) return false;

				if (essenceItem.getEntityType(stack).filter(entityType -> entityType == EntityType.PLAYER).isPresent()) {
					hasPlayerUUID = essenceItem.getEntityUUID(stack).isPresent();
				}
			}
			else return false;
		}

		return hasPlayerHead && hasPlayerUUID && hasExoticDust;
	}

	@Override
	public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess) {
		UUID uuid = null;
		boolean hasPlayerHead = false;
		boolean hasExoticDust = false;

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item == Items.PLAYER_HEAD) {
				if (hasPlayerHead) return ItemStack.EMPTY;
				hasPlayerHead = true;
			}
			else if (item == ModItems.EXOTIC_DUST.get()) {
				if (hasExoticDust) return ItemStack.EMPTY;
				hasExoticDust = true;
			}
			else if (item instanceof EssenceItem essenceItem) {
				if (uuid != null) {
					return ItemStack.EMPTY;
				}
				if (essenceItem.getEntityType(stack).filter(entityType -> entityType == EntityType.PLAYER).isPresent()) {
					uuid = essenceItem.getEntityUUID(stack).orElse(null);
				}
			}
			else return ItemStack.EMPTY;
		}

		return hasPlayerHead && uuid != null && hasExoticDust ? createPlayerHeadFrom(uuid) : ItemStack.EMPTY;
	}

	private ItemStack createPlayerHeadFrom(UUID uuid) {
		GameProfile gameProfile = new GameProfile(uuid, null);

		ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(PlayerHeadItem.TAG_SKULL_OWNER, NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));

		return stack;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.PLAYER_HEAD_SERIALIZER.get();
	}

}
