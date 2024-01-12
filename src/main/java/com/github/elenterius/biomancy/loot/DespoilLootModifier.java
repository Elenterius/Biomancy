package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.item.weapon.DespoilingSwordItem;
import com.github.elenterius.biomancy.util.random.DynamicLootTable;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import static com.github.elenterius.biomancy.util.random.DynamicLootTable.*;

public class DespoilLootModifier extends LootModifier {

	private static final ItemLoot SHARP_FANG = new ItemLoot(ModItems.MOB_FANG, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot SHARP_CLAW = new ItemLoot(ModItems.MOB_CLAW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot SINEW = new ItemLoot(ModItems.MOB_SINEW, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot TOXIN_GLAND = new ItemLoot(ModItems.TOXIN_GLAND, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot VOLATILE_GLAND = new ItemLoot(ModItems.VOLATILE_GLAND, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot GENERIC_GLAND = new ItemLoot(ModItems.GENERIC_MOB_GLAND, CONSTANT_ITEM_AMOUNT_FUNC);
	private static final ItemLoot BONE_MARROW = new ItemLoot(ModItems.MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot WITHERED_BONE_MARROW = new ItemLoot(ModItems.WITHERED_MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot EMPTY = new ItemLoot(() -> Items.AIR, CONSTANT_ITEM_AMOUNT_FUNC);

	private final Weights weights;

	public DespoilLootModifier() {
		this(
				//Can't use MatchTool, because the tool is missing for Entity Kills (1.18.2, 1.19.2)
				//only apply the loot modifier to adult mobs killed by a player
				new LootItemCondition[]{
						LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build())).build(),
						LootItemKilledByPlayerCondition.killedByPlayer().build()
				},
				new Weights(140, 150, 75, 50, 40, 65, 45, 70));
	}

	public DespoilLootModifier(LootItemCondition[] conditions, Weights weights) {
		super(conditions);
		this.weights = weights;
	}

	private static String getName(RegistryObject<? extends Item> itemHolder) {
		return itemHolder.getId().toDebugFileName();
	}

	public LootItemCondition[] getConditions() {
		return conditions;
	}

	protected static int getDespoilLevel(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
		if (killer instanceof LivingEntity livingEntity) {
			int itemDespoilLevel = ModEnchantments.DESPOIL.get().getSlotItems(livingEntity).values().stream()
					.mapToInt(DespoilLootModifier::getDespoilLevel)
					.max()
					.orElse(lootContext.getRandom().nextFloat() < 0.05f ? 1 : 0);

			MobEffectInstance effectInstance = livingEntity.getEffect(ModMobEffects.DESPOIL.get());
			int effectDespoilLevel = effectInstance != null ? effectInstance.getAmplifier() + 1 : 0;

			return Math.max(itemDespoilLevel, effectDespoilLevel);
		}

		return 0;
	}

	protected static int getDespoilLevel(ItemStack stack) {
		int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DESPOIL.get(), stack);

		if (stack.getItem() instanceof DespoilingSwordItem) {
			level++;
		}

		return level;
	}

	protected static boolean hurtAndBreak(LootContext lootContext, Item item, int amount) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);

		if (killer instanceof LivingEntity livingEntity) {
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
					ItemStack stack = livingEntity.getItemBySlot(equipmentSlot);
					if (stack.is(item)) {
						stack.hurtAndBreak(amount, livingEntity, user -> user.broadcastBreakEvent(equipmentSlot));
						return true;
					}
				}
			}
		}

		return false;
	}

	protected DynamicLootTable buildLootTable(LivingEntity livingEntity) {
		EntityType<?> type = livingEntity.getType();
		boolean hasFangs = type.is(ModEntityTags.SHARP_FANG);
		boolean hasClaws = type.is(ModEntityTags.SHARP_CLAW);
		boolean hasToxinGland = type.is(ModEntityTags.TOXIN_GLAND);
		boolean hasVolatileGland = type.is(ModEntityTags.VOLATILE_GLAND);
		boolean hasBileGland = type.is(ModEntityTags.BILE_GLAND);
		boolean hasSinew = type.is(ModEntityTags.SINEW);
		boolean hasBoneMarrow = type.is(ModEntityTags.BONE_MARROW);
		boolean hasWitheredBoneMarrow = type.is(ModEntityTags.WITHERED_BONE_MARROW);

		DynamicLootTable lootTable = new DynamicLootTable();
		if (hasFangs) lootTable.add(SHARP_FANG, weights.fang);
		if (hasClaws) lootTable.add(SHARP_CLAW, weights.claw);
		if (hasToxinGland) lootTable.add(TOXIN_GLAND, weights.toxinGland);
		if (hasVolatileGland) lootTable.add(VOLATILE_GLAND, weights.volatileGland);
		if (hasBileGland) lootTable.addSelfRemoving(GENERIC_GLAND, weights.genericGland);
		if (hasSinew) lootTable.add(SINEW, weights.sinew);
		if (hasBoneMarrow && !hasWitheredBoneMarrow) lootTable.add(BONE_MARROW, weights.boneMarrow);
		if (hasWitheredBoneMarrow) lootTable.add(WITHERED_BONE_MARROW, weights.witheredBoneMarrow);

		return lootTable;
	}

	@NotNull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity victim) {
			final int despoilLevel = getDespoilLevel(context);

			if (despoilLevel > 0) {
				final int lootingLevel = context.getLootingModifier();

				DynamicLootTable lootTable = buildLootTable(victim);
				if (lootTable.isEmpty()) return generatedLoot;

				if (!hurtAndBreak(context, ModItems.DESPOIL_SICKLE.get(), 1)) {
					lootTable.add(EMPTY, 15); //only the despoil sickle has a 100% guarantee to drop despoil loot
				}

				for (int rolls = despoilLevel; rolls > 0; rolls--) {
					lootTable.getRandomItemStack(context.getRandom(), lootingLevel).filter(stack -> !stack.isEmpty()).ifPresent(generatedLoot::add);
				}
			}
		}

		return generatedLoot;
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

	public static class Serializer extends GlobalLootModifierSerializer<DespoilLootModifier> {

		@Override
		public DespoilLootModifier read(ResourceLocation id, JsonObject object, LootItemCondition[] conditions) {
			return new DespoilLootModifier(conditions, Weights.fromJson(object.getAsJsonObject("weights")));
		}

		@Override
		public JsonObject write(DespoilLootModifier instance) {
			JsonObject jsonObject = makeConditions(instance.conditions);
			jsonObject.add("weights", instance.weights.toJson());
			return jsonObject;
		}

	}
}
