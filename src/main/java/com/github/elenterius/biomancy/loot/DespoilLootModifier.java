package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraftforge.common.Tags;
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
					.orElse(0);

			MobEffectInstance effectInstance = livingEntity.getEffect(ModMobEffects.DESPOIL.get());
			int effectDespoilLevel = effectInstance != null ? effectInstance.getAmplifier() + 1 : 0;

			return Math.max(itemDespoilLevel, effectDespoilLevel);
		}

		return 0;
	}

	protected static int getDespoilLevel(ItemStack stack) {
		return stack.getEnchantmentLevel(ModEnchantments.DESPOIL.get());
	}

	protected static boolean isUsingTool(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);

		if (killer instanceof LivingEntity livingEntity) {
			ItemStack stack = livingEntity.getMainHandItem();
			return stack.is(Tags.Items.TOOLS);
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
			LootTable lootTable = getLootTable(context.getLevel(), thisEntity);
			if (lootTable == LootTable.EMPTY) return generatedLoot;

			boolean usingTool = isUsingTool(context);
			int despoilLevel = getDespoilLevel(context);
			float despoilChance = (usingTool ? 0.32f : 0.16f) + 0.32f * despoilLevel;

			LootParams lootParams = createLootParams(context);
			Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(context.getLevel(), generatedLoot::add);

			for (int roll = 0; roll < (generatedLoot.isEmpty() ? 2 : 1) + despoilLevel; roll++) {
				if (context.getRandom().nextFloat() > despoilChance) continue;
				getRandomItems(lootTable, lootParams, stackSplitter);
			}
		}

		return generatedLoot;
	}

	private static void getRandomItems(LootTable lootTable, LootParams lootParams, Consumer<ItemStack> output) {
		//noinspection deprecation
		lootTable.getRandomItemsRaw(lootParams, output); //we use the 'Raw' method to prevent a stackoverflow caused by calling of ForgeHooks#modifyLoot inside GlobalLootModifiers
	}

	protected LootTable getLootTable(ServerLevel level, Entity entity) {
		ResourceLocation lootTableId = getLootTableId(entity.getType());
		return level.getServer().getLootData().getLootTable(lootTableId);
	}

	protected LootParams createLootParams(LootContext context) {
		LootParams.Builder builder = new LootParams.Builder(context.getLevel())
				.withParameter(LootContextParams.THIS_ENTITY, context.getParam(LootContextParams.THIS_ENTITY))
				.withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
				.withParameter(LootContextParams.DAMAGE_SOURCE, context.getParam(LootContextParams.DAMAGE_SOURCE))
				.withOptionalParameter(LootContextParams.KILLER_ENTITY, context.getParamOrNull(LootContextParams.KILLER_ENTITY))
				.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY));

		if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
			Player player = context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
			builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
		}

		return builder.create(LootContextParamSets.ENTITY);
	}

}
