package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.item.Item;
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
		Entity victim = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (victim instanceof LivingEntity livingEntity) {
			int despoilLevel = getDespoilLevel(context);
			if (despoilLevel > 0) {
				float chance = 0.25f + despoilLevel * 0.2f;
				int poolSize = Mth.nextInt(context.getRandom(), 1, despoilLevel + 1);

				if (livingEntity.getType().is(ModTags.EntityTypes.SHARP_TEETH) && context.getRandom().nextFloat() < chance) {
					int amount = Mth.nextInt(context.getRandom(), -1, context.getLootingModifier() + 1);
					if (amount > 0) {
						generatedLoot.add(new ItemStack(ModItems.SHARP_TOOTH.get(), amount));
						poolSize--;
					}
				}

				if (poolSize > 0 && context.getRandom().nextFloat() < chance) {
					Item item = ModItems.STOMACH.get();
					if (victim instanceof ElderGuardian || victim instanceof EnderDragon) {
						item = ModItems.ANCIENT_STOMACH.get();
					}
					generatedLoot.add(new ItemStack(item)); //only 1 stomach per entity possible
					poolSize--;
				}

				if (poolSize > 0 && context.getRandom().nextFloat() < chance) {
					//TODO: do something
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
