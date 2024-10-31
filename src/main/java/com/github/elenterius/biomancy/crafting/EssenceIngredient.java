package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class EssenceIngredient extends AbstractIngredient {

	private final ItemStack itemStack;
	private final CompoundTag partialTag;
	private final NbtPredicate predicate;

	protected EssenceIngredient(ItemStack itemStack, CompoundTag partialTag) {
		super(Stream.of(new Ingredient.ItemValue(itemStack)));

		this.itemStack = itemStack;
		this.partialTag = partialTag;

		predicate = new NbtPredicate(partialTag);
	}

	public static EssenceIngredient of(EntityType<?> entityType) {
		return of(entityType, 1);
	}

	public static EssenceIngredient of(EntityType<?> entityType, int tier) {
		if (tier < 1 || tier > 3) throw new IllegalArgumentException("Cannot create a EssenceIngredient with invalid tier");

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());

		CompoundTag partialTag = new CompoundTag();
		partialTag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
		partialTag.putInt(EssenceItem.ESSENCE_TIER_KEY, tier);

		ItemStack stack = EssenceItem.fromEntityType(entityType, tier);

		return new EssenceIngredient(stack, partialTag);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		return itemStack.getItem() == stack.getItem() && predicate.matches(stack.getShareTag());
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
		json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(Serializer.INSTANCE)).toString());
		json.add("item", RecipeUtil.writeItemStack(itemStack));
		json.addProperty("predicate_tag", partialTag.toString());
		return json;
	}

	public static class Serializer implements IIngredientSerializer<EssenceIngredient> {

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public EssenceIngredient parse(JsonObject json) {
			ItemStack stack = RecipeUtil.readItemStack(json.getAsJsonObject("item"));
			CompoundTag tag = CraftingHelper.getNBT(json.get("predicate_tag"));
			return new EssenceIngredient(stack, tag);
		}

		@Override
		public EssenceIngredient parse(FriendlyByteBuf buffer) {
			ItemStack stack = buffer.readItem();
			CompoundTag tag = buffer.readNbt();
			return new EssenceIngredient(stack, Objects.requireNonNull(tag));
		}

		@Override
		public void write(FriendlyByteBuf buffer, EssenceIngredient ingredient) {
			buffer.writeItem(ingredient.itemStack);
			buffer.writeNbt(ingredient.partialTag);
		}

	}

}
