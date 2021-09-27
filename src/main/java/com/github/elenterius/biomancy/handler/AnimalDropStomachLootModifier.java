package com.github.elenterius.biomancy.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class AnimalDropStomachLootModifier extends LootModifier {

	private final ItemStack loot;
	private final float chance;

	public AnimalDropStomachLootModifier(ILootCondition[] conditionsIn, ItemStack lootIn, float chanceIn) {
		super(conditionsIn);
		loot = lootIn;
		chance = chanceIn;
	}

	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
		if (entity instanceof AnimalEntity && !((AnimalEntity) entity).isBaby()) {
			if (context.getRandom().nextFloat() < chance) {
				generatedLoot.add(loot.copy());
			}
		}
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<AnimalDropStomachLootModifier> {
		@Override
		public AnimalDropStomachLootModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
			float chance = JSONUtils.getAsFloat(json, "chance");
			if (chance <= 0f || chance > 1f) throw new JsonParseException(String.format("Chance %f is outside interval (0, 1]", chance));

			ItemStack loot = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "loot"));
			return new AnimalDropStomachLootModifier(conditionsIn, loot, chance);
		}

		@Override
		public JsonObject write(AnimalDropStomachLootModifier instance) {
			JsonObject json = new JsonObject();

			JsonObject itemJson = new JsonObject();
			ItemStack loot = instance.loot;
			//noinspection ConstantConditions
			itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(loot.getItem()).toString());
			if (loot.getCount() > 1) {
				itemJson.addProperty("count", loot.getCount());
			}
			if (loot.getTag() != null && !loot.getTag().isEmpty()) {
				itemJson.addProperty("nbt", loot.getTag().toString());
			}
			json.addProperty("#comment", "the add_mob_loot serializer supports count (int) and nbt (string) keys");
			json.add("loot", itemJson);

			json.addProperty("chance", instance.chance);
			return json;
		}
	}
}
