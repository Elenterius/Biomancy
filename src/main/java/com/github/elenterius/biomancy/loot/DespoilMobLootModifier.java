package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.world.item.LarynxItem;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DespoilMobLootModifier extends LootModifier {

	public DespoilMobLootModifier() {
		this(
				//Can't use MatchTool, since the tool is missing for Entity Kills
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build())).build(),
				LootItemKilledByPlayerCondition.killedByPlayer().build()
		);
	}

	public DespoilMobLootModifier(LootItemCondition... conditions) {
		super(conditions);
	}

	@NotNull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity victim) {
			int despoilLevel = getDespoilLevel(context);
			if (despoilLevel > 0) {
				Random random = context.getRandom();

				float chance = 0.25f + despoilLevel * 0.2f;
				int lootRolls = Mth.nextInt(random, 1, despoilLevel + 1);

				EntityType<?> victimType = victim.getType();

				Supplier<Boolean> rollDice = () -> random.nextFloat() < chance;

				if (victimType.is(ModTags.EntityTypes.SHARP_FANG) && rollDice.get()) {
					int amount = Mth.nextInt(random, -1, context.getLootingModifier() + 1);
					if (amount > 0) {
						generatedLoot.add(new ItemStack(ModItems.MOB_FANG.get(), amount));
						lootRolls--;
					}
				}

				if (lootRolls > 0 && victimType.is(ModTags.EntityTypes.SHARP_CLAW) && rollDice.get()) {
					int amount = Mth.nextInt(random, -1, context.getLootingModifier() + 1);
					if (amount > 0) {
						generatedLoot.add(new ItemStack(ModItems.MOB_CLAW.get(), amount));
						lootRolls--;
					}
				}

				if (lootRolls > 0 && rollDice.get()) {
					int amount = Mth.nextInt(random, -1, context.getLootingModifier() + 2);
					if (amount > 0) {
						generatedLoot.add(new ItemStack(ModItems.MOB_SINEW.get(), amount));
						lootRolls--;
					}
				}

				boolean hasSpecialGland = false;

				if (lootRolls > 0 && victimType.is(ModTags.EntityTypes.VENOM_GLAND) && rollDice.get()) {
					generatedLoot.add(new ItemStack(ModItems.VENOM_GLAND.get()));
					hasSpecialGland = true;
					lootRolls--;
				}

				if (lootRolls > 0 && victimType.is(ModTags.EntityTypes.VOLATILE_GLAND) && rollDice.get()) {
					generatedLoot.add(new ItemStack(ModItems.VOLATILE_GLAND.get()));
					hasSpecialGland = true;
					lootRolls--;
				}

				if (lootRolls > 0 && !hasSpecialGland && random.nextFloat() < chance - 0.05) {
					generatedLoot.add(new ItemStack(ModItems.MOB_GLAND.get()));
					lootRolls--;
				}

				if (lootRolls > 0 && rollDice.get()) {
					int amount = Mth.nextInt(random, -1, context.getLootingModifier() + 2);
					if (amount > 0) {
						generatedLoot.add(new ItemStack(ModItems.MOB_MARROW.get(), amount));
						lootRolls--;
					}
				}

				if (lootRolls > 0 && rollDice.get()) {
					ItemStack stack = new ItemStack(ModItems.LARYNX.get());
					LarynxItem.saveSounds(stack, victim);
					generatedLoot.add(stack); //only 1 larynx per entity possible
					lootRolls--;
				}
			}
		}
		return generatedLoot;
	}

	private int getDespoilLevel(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
		if (killer instanceof LivingEntity livingEntity) {
			return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.DESPOIL.get(), livingEntity);
		}
		return 0;
	}

	public static class Serializer extends GlobalLootModifierSerializer<DespoilMobLootModifier> {

		@Override
		public DespoilMobLootModifier read(ResourceLocation id, JsonObject object, LootItemCondition[] conditions) {
			return new DespoilMobLootModifier(conditions);
		}

		@Override
		public JsonObject write(DespoilMobLootModifier instance) {
			return makeConditions(instance.conditions);
		}

	}

}
