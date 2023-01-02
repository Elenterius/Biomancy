package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModItems;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

public class CatMorningGiftLootModifier extends LootModifier {

	public CatMorningGiftLootModifier() {
		this(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS).build(), LootTableIdCondition.builder(BuiltInLootTables.CAT_MORNING_GIFT).build());
	}

	public CatMorningGiftLootModifier(LootItemCondition... conditions) {
		super(conditions);
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Cat cat)) return generatedLoot;

		RandomSource random = context.getRandom();
		boolean isBlackCat = cat.getCatVariant() == CatVariant.ALL_BLACK || cat.getCatVariant() == CatVariant.BLACK;

		for (int i = 0; i < generatedLoot.size(); i++) {
			ItemStack stack = generatedLoot.get(i);

			if (stack.is(Items.PHANTOM_MEMBRANE)) continue;

			if (isBlackCat) {
				if (random.nextFloat() < 0.25f) {
					generatedLoot.set(i, new ItemStack(random.nextBoolean() ? ModItems.MOB_CLAW.get() : ModItems.MOB_FANG.get(), random.nextInt(1, 3)));
				}
			}
			else if (random.nextFloat() < 0.10f) {
				Item[] items = {ModItems.FLESH_BITS.get(), ModItems.MOB_CLAW.get(), ModItems.MOB_FANG.get()};
				generatedLoot.set(i, new ItemStack(items[random.nextInt(items.length)]));
			}

			if (stack.is(Items.STRING) && random.nextFloat() < 0.2f) {
				generatedLoot.add(new ItemStack(ModItems.ORGANIC_MATTER.get()));
			}
		}

		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		// TODO Auto-generated method stub
		return null;
	}

	//TODO: Work on this
	/*public static class Serializer extends GlobalLootModifierSerializer<CatMorningGiftLootModifier> {

		@Override
		public CatMorningGiftLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
			return new CatMorningGiftLootModifier(conditions);
		}

		@Override
		public JsonObject write(CatMorningGiftLootModifier instance) {
			return makeConditions(instance.conditions);
		}
	}*/

}
