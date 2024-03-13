package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DespoilLootModifier extends LootModifier {

	public static final Supplier<Codec<DespoilLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
			codecStart(inst).apply(inst, DespoilLootModifier::new)
	));

	public static final String LOOT_PREFIX = "biomancy/despoil/";

	public DespoilLootModifier() {
		this(
				//Can't use MatchTool, because the tool is missing for Entity Kills (1.18.2, 1.19.2)
				//only apply the loot modifier to adult mobs killed by a player
				new LootItemCondition[]{
						LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build())).build(),
						LootItemKilledByPlayerCondition.killedByPlayer().build()
				});
	}

	public DespoilLootModifier(LootItemCondition[] conditions) {
		super(conditions);
	}

	public static ResourceLocation getLootTableId(EntityType<?> entityType) {
		ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		return key.withPrefix(LOOT_PREFIX);
	}

	public static ResourceLocation getLootTableId(ResourceLocation entityTypeId) {
		return entityTypeId.withPrefix(LOOT_PREFIX);
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
		return stack.getEnchantmentLevel(ModEnchantments.DESPOIL.get());
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

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	public LootItemCondition[] getConditions() {
		return conditions;
	}

	@NotNull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);

		if (thisEntity instanceof LivingEntity || thisEntity instanceof Player) {
			final int despoilLevel = getDespoilLevel(context);

			if (despoilLevel > 0) {
				LootTable lootTable = getLootTable(context.getLevel(), thisEntity);

				if (lootTable == LootTable.EMPTY) return generatedLoot;

				float despoilChance = 1f;
				if (!hurtAndBreak(context, ModItems.DESPOIL_SICKLE.get(), 1)) {
					despoilChance -= 0.15f; //only the despoil sickle has a 100% guarantee to drop despoil loot
				}

				LootParams lootParams = createLootParams(context);
				Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(context.getLevel(), generatedLoot::add);

				for (int rolls = despoilLevel; rolls > 0; rolls--) {
					if (context.getRandom().nextFloat() > despoilChance) continue;

					getRandomItems(lootTable, lootParams, stackSplitter);
				}
			}
		}

		return generatedLoot;
	}

	@SuppressWarnings("deprecation")
	private static void getRandomItems(LootTable lootTable, LootParams lootParams, Consumer<ItemStack> output) {
		//we use the 'Raw' method to prevent a stackoverflow caused by calling of ForgeHooks#modifyLoot inside GlobalLootModifiers
		lootTable.getRandomItemsRaw(lootParams, output);
	}

	protected LootTable getLootTable(ServerLevel level, Entity entity) {
		ResourceLocation lootTableId = getLootTableId(entity.getType());
		return level.getServer().getLootData().getLootTable(lootTableId);

		//Fallback
		//		if (lootTable == LootTable.EMPTY && entity instanceof LivingEntity livingEntity) {
		//			lootTable = createDynamicLootTableFallback(livingEntity);
		//		}

		//		return lootTable;
	}

	protected LootParams createLootParams(LootContext context) {
		LootParams.Builder builder = new LootParams.Builder(context.getLevel())
				.withParameter(LootContextParams.THIS_ENTITY, context.getParam(LootContextParams.THIS_ENTITY))
				.withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
				.withParameter(LootContextParams.DAMAGE_SOURCE, context.getParam(LootContextParams.DAMAGE_SOURCE))
				.withOptionalParameter(LootContextParams.KILLER_ENTITY, context.getParam(LootContextParams.KILLER_ENTITY))
				.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, context.getParam(LootContextParams.DIRECT_KILLER_ENTITY));

		if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
			Player player = context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
			builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
		}

		return builder.create(LootContextParamSets.ENTITY);
	}

	protected LootTable createDynamicLootTableFallback(LivingEntity livingEntity) {
		EntityType<?> entityType = livingEntity.getType();

		LootTable.Builder lootTable = LootTable.lootTable();
		LootPool.Builder lootPool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		boolean hasLoot = false;

		if (entityType.is(EntityTypeTags.SKELETONS)) {
			lootPool.add(
					LootItem.lootTableItem(ModItems.MOB_MARROW.get()).setWeight(70)
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			hasLoot = true;
		}

		if (isValidForMeatyLoot(livingEntity, entityType)) {
			lootPool
					.add(
							LootItem.lootTableItem(ModItems.MOB_SINEW.get()).setWeight(50)
									.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
									.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
					)
					.add(
							LootItem.lootTableItem(ModItems.GENERIC_MOB_GLAND.get()).setWeight(30)
									.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
					);

			hasLoot = true;
		}

		return hasLoot ? lootTable.withPool(lootPool).build() : LootTable.EMPTY;
	}

	private boolean isValidForMeatyLoot(LivingEntity livingEntity, EntityType<?> entityType) {
		if (livingEntity instanceof Zombie) return true;
		if (livingEntity instanceof ZombieHorse) return true;
		if (livingEntity instanceof ZombifiedPiglin) return true;
		if (livingEntity instanceof ZombieVillager) return true;

		if (livingEntity instanceof Villager) return true;

		if (livingEntity instanceof AbstractGolem) return false;
		if (livingEntity instanceof Slime) return false;
		if (livingEntity instanceof Allay) return false;
		if (livingEntity instanceof Vex) return false;
		if (livingEntity instanceof Phantom) return false;
		if (livingEntity instanceof Warden) return false;

		return livingEntity.getMobType() != MobType.UNDEAD;
	}

}
