package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

public class DespoilMobLootModifier extends LootModifier {
	public static final ToIntBiFunction<Random, Integer> CONSTANT_ITEM_AMOUNT_FUNC = (random, lootingLevel) -> 1;
	public static final ToIntBiFunction<Random, Integer> RANDOM_ITEM_AMOUNT_FUNC_1 = (random, lootingLevel) -> Mth.nextInt(random, 1, 1 + lootingLevel);
	public static final ToIntBiFunction<Random, Integer> RANDOM_ITEM_AMOUNT_FUNC_2 = (random, lootingLevel) -> Mth.nextInt(random, 1, 2 + lootingLevel);
	private static final ItemLoot SHARP_FANG = new ItemLoot(ModItems.MOB_FANG, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot SHARP_CLAW = new ItemLoot(ModItems.MOB_CLAW, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot SINEW = new ItemLoot(ModItems.MOB_SINEW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot TOXIN_GLAND = new ItemLoot(ModItems.TOXIN_GLAND, CONSTANT_ITEM_AMOUNT_FUNC);
	private static final ItemLoot VOLATILE_GLAND = new ItemLoot(ModItems.VOLATILE_GLAND, CONSTANT_ITEM_AMOUNT_FUNC);
	private static final ItemLoot GENERIC_GLAND = new ItemLoot(ModItems.GENERIC_MOB_GLAND, CONSTANT_ITEM_AMOUNT_FUNC);
	private static final ItemLoot BONE_MARROW = new ItemLoot(ModItems.MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot WITHERED_BONE_MARROW = new ItemLoot(ModItems.WITHERED_MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_2);

	private final Weights weights;

	public DespoilMobLootModifier() {
		this(new Weights(90, 90, 65, 50, 75, 65, 50, 75),
				//Can't use MatchTool, since the tool is missing for Entity Kills
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build())).build(), LootItemKilledByPlayerCondition.killedByPlayer().build());
	}

	public DespoilMobLootModifier(Weights weights, LootItemCondition... conditions) {
		super(conditions);
		this.weights = weights;
	}

	protected SimpleWeightedRandomList<ItemLoot> buildLootTable(LivingEntity livingEntity) {
		SimpleWeightedRandomList.Builder<ItemLoot> builder = SimpleWeightedRandomList.builder();

		EntityType<?> type = livingEntity.getType();
		boolean hasFangs = type.is(ModTags.EntityTypes.SHARP_FANG);
		boolean hasClaws = type.is(ModTags.EntityTypes.SHARP_CLAW);
		boolean hasToxinGland = type.is(ModTags.EntityTypes.VENOM_GLAND);
		boolean hasVolatileGland = type.is(ModTags.EntityTypes.VOLATILE_GLAND);
		boolean isWithered = MobUtil.isWithered(livingEntity);
		boolean isSkeleton = MobUtil.isSkeleton(livingEntity);

		if (hasFangs) builder.add(SHARP_FANG, weights.fang);
		if (hasClaws) builder.add(SHARP_CLAW, weights.claw);
		if (hasToxinGland) builder.add(TOXIN_GLAND, weights.toxinGland);
		if (hasVolatileGland) builder.add(VOLATILE_GLAND, weights.volatileGland);
		if (!hasToxinGland && !hasVolatileGland) builder.add(GENERIC_GLAND, weights.genericGland);
		if (isWithered) builder.add(WITHERED_BONE_MARROW, weights.witheredBoneMarrow);
		if (!isWithered && isSkeleton) builder.add(BONE_MARROW, weights.boneMarrow);
		if (!isWithered && !isSkeleton) builder.add(SINEW, weights.sinew);

		return builder.build();
	}

	@NotNull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity victim) {
			int despoilLevel = getDespoilLevel(context);
			if (despoilLevel > 0) {
				Random random = context.getRandom();
				SimpleWeightedRandomList<ItemLoot> lootTable = buildLootTable(victim);
				int lootingLevel = context.getLootingModifier();
				for (int lootRolls = Mth.nextInt(random, 1, despoilLevel + 1); lootRolls > 0; lootRolls--) {
					lootTable.getRandomValue(random).ifPresent(itemLoot -> generatedLoot.add(itemLoot.getItemStack(random, lootingLevel)));
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

	private static String getName(RegistryObject<? extends Item> itemHolder) {
		return itemHolder.getId().toDebugFileName();
	}

	record Weights(int fang, int claw, int toxinGland, int volatileGland, int genericGland, int witheredBoneMarrow, int boneMarrow, int sinew) {
		public static Weights fromJson(JsonObject jsonObject) {
			return new Weights(GsonHelper.getAsInt(jsonObject, getName(ModItems.MOB_FANG)), GsonHelper.getAsInt(jsonObject, getName(ModItems.MOB_CLAW)), GsonHelper.getAsInt(jsonObject, getName(ModItems.TOXIN_GLAND)), GsonHelper.getAsInt(jsonObject, getName(ModItems.VOLATILE_GLAND)), GsonHelper.getAsInt(jsonObject, getName(ModItems.GENERIC_MOB_GLAND)), GsonHelper.getAsInt(jsonObject, getName(ModItems.WITHERED_MOB_MARROW)), GsonHelper.getAsInt(jsonObject, getName(ModItems.MOB_MARROW)), GsonHelper.getAsInt(jsonObject, getName(ModItems.MOB_SINEW)));
		}

		public JsonObject toJson() {
			JsonObject weights = new JsonObject();
			weights.addProperty(getName(ModItems.MOB_FANG), fang);
			weights.addProperty(getName(ModItems.MOB_CLAW), claw);
			weights.addProperty(getName(ModItems.TOXIN_GLAND), toxinGland);
			weights.addProperty(getName(ModItems.VOLATILE_GLAND), volatileGland);
			weights.addProperty(getName(ModItems.GENERIC_MOB_GLAND), genericGland);
			weights.addProperty(getName(ModItems.WITHERED_MOB_MARROW), witheredBoneMarrow);
			weights.addProperty(getName(ModItems.MOB_MARROW), boneMarrow);
			weights.addProperty(getName(ModItems.MOB_SINEW), sinew);
			return weights;
		}
	}

	record ItemLoot(Supplier<? extends Item> itemSupplier, ToIntBiFunction<Random, Integer> itemCountFunc) {
		int getItemAmount(Random random, int lootingLevel) {
			return itemCountFunc.applyAsInt(random, lootingLevel);
		}

		ItemStack getItemStack(Random random, int lootingLevel) {
			return new ItemStack(itemSupplier.get(), getItemAmount(random, lootingLevel));
		}
	}

	public static class Serializer extends GlobalLootModifierSerializer<DespoilMobLootModifier> {

		@Override
		public DespoilMobLootModifier read(ResourceLocation id, JsonObject object, LootItemCondition[] conditions) {
			return new DespoilMobLootModifier(Weights.fromJson(object.getAsJsonObject("weights")), conditions);
		}

		@Override
		public JsonObject write(DespoilMobLootModifier instance) {
			JsonObject jsonObject = makeConditions(instance.conditions);
			jsonObject.add("weights", instance.weights.toJson());
			return jsonObject;
		}

	}

}
