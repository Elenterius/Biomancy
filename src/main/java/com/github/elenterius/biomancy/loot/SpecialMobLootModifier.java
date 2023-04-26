package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.util.random.DynamicLootTable;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.github.elenterius.biomancy.util.random.DynamicLootTable.*;

public class SpecialMobLootModifier extends LootModifier {
	public static final Supplier<Codec<SpecialMobLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
			codecStart(inst)
					.and(Weights.CODEC.get().fieldOf("weights").forGetter(m -> m.weights))
					.apply(inst, SpecialMobLootModifier::new)
	));

	private static final ItemLoot SHARP_FANG = new ItemLoot(ModItems.MOB_FANG, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot SHARP_CLAW = new ItemLoot(ModItems.MOB_CLAW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot SINEW = new ItemLoot(ModItems.MOB_SINEW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot TOXIN_GLAND = new ItemLoot(ModItems.TOXIN_GLAND, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot VOLATILE_GLAND = new ItemLoot(ModItems.VOLATILE_GLAND, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot GENERIC_GLAND = new ItemLoot(ModItems.GENERIC_MOB_GLAND, CONSTANT_ITEM_AMOUNT_FUNC);
	private static final ItemLoot BONE_MARROW = new ItemLoot(ModItems.MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot WITHERED_BONE_MARROW = new ItemLoot(ModItems.WITHERED_MOB_MARROW, RANDOM_ITEM_AMOUNT_FUNC_2);
	private static final ItemLoot FLESH_BITS = new ItemLoot(ModItems.FLESH_BITS, RANDOM_ITEM_AMOUNT_FUNC_2); //bonus drop for bone cleaver
	private static final ItemLoot ECHO_SHARD = new ItemLoot(() -> Items.ECHO_SHARD, RANDOM_ITEM_AMOUNT_FUNC_1);
	private static final ItemLoot EMPTY = new ItemLoot(() -> Items.AIR, CONSTANT_ITEM_AMOUNT_FUNC);

	private final Weights weights;

	public SpecialMobLootModifier() {
		this(
				//Can't use MatchTool, because the tool is missing for Entity Kills (1.18.2, 1.19.2)
				//only apply the loot modifier to adult mobs killed by a player
				new LootItemCondition[]{
						LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build())).build(),
						LootItemKilledByPlayerCondition.killedByPlayer().build()
				},
				new Weights(140, 150, 75, 50, 40, 65, 45, 70));
	}

	public SpecialMobLootModifier(LootItemCondition[] conditions, Weights weights) {
		super(conditions);
		this.weights = weights;
	}

	private static String getName(RegistryObject<? extends Item> itemHolder) {
		return itemHolder.getId().toDebugFileName();
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	public LootItemCondition[] getConditions() {
		return conditions;
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
		if (hasBoneMarrow) lootTable.add(BONE_MARROW, weights.boneMarrow);
		if (hasWitheredBoneMarrow) lootTable.add(WITHERED_BONE_MARROW, weights.witheredBoneMarrow);

		if (livingEntity instanceof Warden) {
			lootTable.add(ECHO_SHARD, 10);
		}

		return lootTable;
	}

	@NotNull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity victim) {
			DynamicLootTable lootTable = buildLootTable(victim);
			if (lootTable.isEmpty()) return generatedLoot;

			lootTable.add(EMPTY, 15);

			int despoilLevel = getDespoilLevel(context);
			int lootingLevel = context.getLootingModifier();
			ItemStack heldStack = getItemInMainHand(context);

			//bonus
			if (heldStack.is(ModItems.BONE_CLEAVER.get())) {
				despoilLevel++;
				lootTable.add(FLESH_BITS, 15);
			}
			else if (heldStack.is(ModItemTags.FORGE_TOOLS_KNIVES)) {
				despoilLevel++;
			}

			RandomSource random = context.getRandom();
			if (despoilLevel > 0 || random.nextFloat() < 0.05f) {
				int diceRolls = Mth.nextInt(random, 1, 1 + despoilLevel); //max is inclusive
				for (; diceRolls > 0; diceRolls--) {
					lootTable.getRandomItemStack(random, lootingLevel).filter(stack -> !stack.isEmpty()).ifPresent(generatedLoot::add);
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

	private ItemStack getItemInMainHand(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
		if (killer instanceof LivingEntity livingEntity) {
			return livingEntity.getMainHandItem();
		}
		return ItemStack.EMPTY;
	}

	record Weights(int fang, int claw, int toxinGland, int volatileGland, int genericGland, int witheredBoneMarrow, int boneMarrow, int sinew) {
		public static final Supplier<Codec<Weights>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
						Codec.INT.fieldOf(getName(ModItems.MOB_FANG)).forGetter(Weights::fang),
						Codec.INT.fieldOf(getName(ModItems.MOB_CLAW)).forGetter(Weights::claw),
						Codec.INT.fieldOf(getName(ModItems.TOXIN_GLAND)).forGetter(Weights::toxinGland),
						Codec.INT.fieldOf(getName(ModItems.VOLATILE_GLAND)).forGetter(Weights::volatileGland),
						Codec.INT.fieldOf(getName(ModItems.GENERIC_MOB_GLAND)).forGetter(Weights::genericGland),
						Codec.INT.fieldOf(getName(ModItems.WITHERED_MOB_MARROW)).forGetter(Weights::witheredBoneMarrow),
						Codec.INT.fieldOf(getName(ModItems.MOB_MARROW)).forGetter(Weights::boneMarrow),
						Codec.INT.fieldOf(getName(ModItems.MOB_SINEW)).forGetter(Weights::sinew)
				).apply(inst, Weights::new))
		);
	}

}
