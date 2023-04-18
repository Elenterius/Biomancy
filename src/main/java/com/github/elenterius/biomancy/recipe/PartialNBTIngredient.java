package com.github.elenterius.biomancy.recipe;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.elenterius.biomancy.BiomancyMod.GSON;

public class PartialNBTIngredient extends Ingredient {
	private final Set<Item> items;
	private final CompoundNBT tag;
	private final NBTPredicate predicate;

	protected PartialNBTIngredient(Set<Item> items, CompoundNBT tag) {
		super(items.stream().map(item -> createSingleItemList(item, tag)));

		if (items.isEmpty()) {
			throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");
		}

		this.items = Collections.unmodifiableSet(items);
		this.tag = tag;
		this.predicate = new NBTPredicate(tag);
	}

	private static Ingredient.IItemList createSingleItemList(Item item, CompoundNBT tag) {
		ItemStack stack = new ItemStack(item);
		stack.setTag(tag.copy());
		return new SingleItemList(stack);
	}

	public static PartialNBTIngredient of(CompoundNBT nbt, IItemProvider... items) {
		return new PartialNBTIngredient(Arrays.stream(items).map(IItemProvider::asItem).collect(Collectors.toSet()), nbt);
	}

	public static PartialNBTIngredient of(IItemProvider item, CompoundNBT nbt) {
		return new PartialNBTIngredient(ImmutableSet.of(item.asItem()), nbt);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		return items.contains(stack.getItem()) && predicate.matches(stack.getShareTag());
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		if (items.size() == 1) {
			json.addProperty("item", ForgeRegistries.ITEMS.getKey(items.iterator().next()).toString());
		}
		else {
			JsonArray array = new JsonArray();
			// ensure the order of items in the set is deterministic when saved to JSON
			this.items.stream().map(ForgeRegistries.ITEMS::getKey).sorted().forEach(name -> array.add(name.toString()));
			json.add("items", array);
		}
		json.addProperty("nbt", tag.toString());
		return json;
	}

	public static class Serializer implements IIngredientSerializer<PartialNBTIngredient> {
		public static final Serializer INSTANCE = new Serializer();

		protected static CompoundNBT getNBT(JsonElement element) {
			try {
				if (element.isJsonObject()) return JsonToNBT.parseTag(GSON.toJson(element));
				else return JsonToNBT.parseTag(JSONUtils.convertToString(element, "nbt"));
			}
			catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Invalid NBT Entry: " + e);
			}
		}

		protected static Item getItem(String itemName) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
			if (item == null) throw new JsonSyntaxException(String.format("Unknown item '%s'", itemName));
			if (item == Items.AIR) throw new JsonSyntaxException(String.format("Invalid item: '%s'", itemName));
			return Objects.requireNonNull(item);
		}

		@Override
		public PartialNBTIngredient parse(JsonObject json) {

			Set<Item> items;
			if (json.has("item")) {
				String itemName = JSONUtils.getAsString(json, "item");
				items = ImmutableSet.of(getItem(itemName));
			}
			else if (json.has("items")) {
				ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
				JsonArray itemArray = JSONUtils.getAsJsonArray(json, "items");
				for (int i = 0; i < itemArray.size(); i++) {
					String itemName = JSONUtils.convertToString(itemArray.get(i), String.format("items[%d]", i));
					builder.add(getItem(itemName));
				}
				items = builder.build();
			}
			else {
				throw new JsonSyntaxException("Must set either 'item' or 'items'");
			}

			if (!json.has("nbt")) throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");

			return new PartialNBTIngredient(items, getNBT(json.get("nbt")));
		}

		@Override
		public PartialNBTIngredient parse(PacketBuffer buffer) {
			Set<Item> items = Stream.generate(() -> buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS)).limit(buffer.readVarInt()).collect(Collectors.toSet());
			CompoundNBT nbt = buffer.readNbt();
			return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
		}

		@Override
		public void write(PacketBuffer buffer, PartialNBTIngredient ingredient) {
			buffer.writeVarInt(ingredient.items.size());
			for (Item item : ingredient.items) {
				buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
			}
			buffer.writeNbt(ingredient.tag);
		}
	}
}
