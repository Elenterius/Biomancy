package com.github.elenterius.biomancy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public class Byproduct {

	private final Item item;
	private final @Nullable
	CompoundNBT nbt;
	private final int count;
	private final float chance; //interval (0, 1]

	public Byproduct(IItemProvider itemIn) {
		this(itemIn, 1);
	}

	public Byproduct(IItemProvider item, int countIn) {
		this(item, countIn, 1f);
	}

	public Byproduct(ItemStack stack, float chanceIn) {
		this(stack.getItem(), stack.getCount(), stack.getTag(), chanceIn);
	}

	public Byproduct(ItemStack stack) {
		this(stack.getItem(), stack.getCount(), stack.getTag(), 1f);
	}

	public Byproduct(IItemProvider itemIn, float chanceIn) {
		this(itemIn, 1, chanceIn);
	}

	public Byproduct(IItemProvider item, int count, float chance) {
		this(item, count, null, chance);
	}

	public Byproduct(IItemProvider itemIn, int countIn, @Nullable CompoundNBT nbtIn, float chanceIn) {
		assert chanceIn > 0f && chanceIn <= 1f;
		assert countIn > 0;
		item = itemIn.asItem();
		count = countIn;
		nbt = nbtIn;
		chance = MathHelper.clamp(chanceIn, 0f, 1f);
	}

	public static Byproduct read(PacketBuffer buffer) {
		return new Byproduct(buffer.readItemStack(), buffer.readFloat());
	}

	public static Byproduct deserialize(JsonObject jsonObject) {
		ItemStack stack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(jsonObject, "result"));
		float chance = JSONUtils.getFloat(jsonObject, "chance", 1f);
		if (chance <= 0f || chance > 1f) throw new JsonParseException(String.format("Chance %f is outside interval (0, 1]", chance));
		if (stack.isEmpty()) throw new JsonParseException("Defined byproduct can't be Empty");
		return new Byproduct(stack, chance);
	}

	public void write(PacketBuffer buffer) {
		buffer.writeItemStack(getItemStack());
		buffer.writeFloat(chance);
	}

	public JsonElement serialize() {
		JsonObject parent = new JsonObject();

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.getKey(item).toString());
		if (count > 1) {
			jsonObject.addProperty("count", count);
		}
		if (nbt != null && !nbt.isEmpty()) {
			jsonObject.addProperty("nbt", nbt.getString());
		}
		parent.add("result", jsonObject);

		parent.addProperty("chance", chance);

		return parent;
	}

	public Item getItem() {
		return item;
	}

	/**
	 * @return a new ItemStack instance
	 */
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(item, count);
		if (nbt != null && !nbt.isEmpty()) {
			stack.setTag(nbt.copy());
		}
		return stack;
	}

	public int getCount() { return count; }

	public float getChance() {
		return chance;
	}
}
