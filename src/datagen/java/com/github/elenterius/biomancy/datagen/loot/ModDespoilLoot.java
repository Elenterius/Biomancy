package com.github.elenterius.biomancy.datagen.loot;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModDespoilLoot extends DespoilLootProvider {

	protected static final Set<EntityType<?>> BONE_MARROW_MOBS = Set.of(
			EntityType.SKELETON_HORSE, EntityType.SKELETON, EntityType.STRAY,
			EntityType.WARDEN,

			AMEntityRegistry.SKELEWAG.get(), AMEntityRegistry.BONE_SERPENT.get()
	);

	protected static final Set<EntityType<?>> WITHERED_BONE_MARROW_MOBS = Set.of(
			EntityType.WITHER_SKELETON, EntityType.WITHER,

			ACEntityRegistry.RAYCAT.get()
	);

	protected static final Set<EntityType<?>> TOXIC_MOBS = Set.of(
			EntityType.CAVE_SPIDER,
			EntityType.PUFFERFISH,
			EntityType.BEE,

			AMEntityRegistry.KOMODO_DRAGON.get(), AMEntityRegistry.PLATYPUS.get(), AMEntityRegistry.RATTLESNAKE.get(),

			ACEntityRegistry.RADGILL.get(), ACEntityRegistry.SEA_PIG.get()
	);

	protected static final Set<EntityType<?>> VOLATILE_MOBS = Set.of(
			EntityType.CREEPER,
			EntityType.GHAST, EntityType.BLAZE,
			EntityType.WITHER, EntityType.ENDER_DRAGON,

			ACEntityRegistry.NUCLEEPER.get()
	);

	protected static final Set<EntityType<?>> SHARP_CLAW_MOBS = Set.of(
			EntityType.BAT, EntityType.PARROT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.ENDER_DRAGON,

			AMEntityRegistry.GRIZZLY_BEAR.get(), AMEntityRegistry.DROPBEAR.get(), AMEntityRegistry.SEA_BEAR.get(),
			AMEntityRegistry.ROADRUNNER.get(), AMEntityRegistry.SOUL_VULTURE.get(), AMEntityRegistry.BALD_EAGLE.get(), AMEntityRegistry.EMU.get(),
			AMEntityRegistry.PLATYPUS.get(),
			AMEntityRegistry.RACCOON.get(), AMEntityRegistry.TASMANIAN_DEVIL.get(),
			AMEntityRegistry.TIGER.get(), AMEntityRegistry.MANED_WOLF.get(), AMEntityRegistry.SNOW_LEOPARD.get(),

			ACEntityRegistry.SUBTERRANODON.get(), ACEntityRegistry.VALLUMRAPTOR.get(), ACEntityRegistry.TREMORSAURUS.get(), ACEntityRegistry.RELICHEIRUS.get(),
			ACEntityRegistry.RAYCAT.get(),
			ACEntityRegistry.DEEP_ONE.get(), ACEntityRegistry.DEEP_ONE_KNIGHT.get(),
			ACEntityRegistry.UNDERZEALOT.get(), ACEntityRegistry.WATCHER.get(), ACEntityRegistry.CORRODENT.get(), ACEntityRegistry.VESPER.get(), ACEntityRegistry.FORSAKEN.get()
	);

	protected static final Set<EntityType<?>> SHARP_FANG_MOBS = Set.of(
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.HOGLIN, EntityType.ZOGLIN,
			EntityType.ENDER_DRAGON,

			AMEntityRegistry.GRIZZLY_BEAR.get(), AMEntityRegistry.DROPBEAR.get(), AMEntityRegistry.SEA_BEAR.get(),
			AMEntityRegistry.GORILLA.get(), AMEntityRegistry.GELADA_MONKEY.get(), AMEntityRegistry.CAPUCHIN_MONKEY.get(),
			AMEntityRegistry.RATTLESNAKE.get(), AMEntityRegistry.ANACONDA.get(),
			AMEntityRegistry.TIGER.get(), AMEntityRegistry.MANED_WOLF.get(), AMEntityRegistry.SNOW_LEOPARD.get(),
			AMEntityRegistry.TUSKLIN.get(),

			ACEntityRegistry.VALLUMRAPTOR.get(), ACEntityRegistry.TREMORSAURUS.get(),
			ACEntityRegistry.RAYCAT.get(),
			ACEntityRegistry.HULLBREAKER.get(), ACEntityRegistry.DEEP_ONE.get(), ACEntityRegistry.DEEP_ONE_KNIGHT.get(),
			ACEntityRegistry.VESPER.get(), ACEntityRegistry.FORSAKEN.get()
	);

	protected static final Set<EntityType<?>> INVALID_MOBS_FOR_MEATY_LOOT = Set.of(
			EntityType.SLIME, EntityType.MAGMA_CUBE,
			EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER,
			EntityType.VEX, EntityType.GHAST, EntityType.ALLAY, EntityType.PHANTOM,
			EntityType.BLAZE,
			EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER,
			EntityType.WARDEN,
			EntityType.CREEPER,

			AMEntityRegistry.SPECTRE.get(), AMEntityRegistry.VOID_WORM.get(), AMEntityRegistry.SKELEWAG.get(), AMEntityRegistry.BONE_SERPENT.get(),
			AMEntityRegistry.MIMICUBE.get(), AMEntityRegistry.FLUTTER.get(), AMEntityRegistry.GUSTER.get(),

			ACEntityRegistry.TELETOR.get(), ACEntityRegistry.MAGNETRON.get(), ACEntityRegistry.BOUNDROID.get(), ACEntityRegistry.NOTOR.get(), ACEntityRegistry.FERROUSLIME.get(),
			ACEntityRegistry.RAYCAT.get(), ACEntityRegistry.NUCLEEPER.get(),
			ACEntityRegistry.MINE_GUARDIAN.get()
	);

	@Override
	public void generate() {
		Set<String> validNamespaces = Set.of("minecraft", BiomancyMod.MOD_ID, AlexsMobs.MODID, AlexsCaves.MODID);
		Predicate<EntityType<?>> allowedNamespace = entityType -> validNamespaces.contains(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType)).getNamespace());

		Predicate<EntityType<?>> validEntityType = entityType -> entityType.getCategory() != MobCategory.MISC; //excludes Players & Villagers as well
		Predicate<EntityType<?>> ignoreEntityType = entityType -> entityType != EntityType.WARDEN;

		ForgeRegistries.ENTITY_TYPES.getValues().stream()
				.filter(allowedNamespace)
				.filter(validEntityType)
				.filter(ignoreEntityType)
				.forEach(this::add);

		add(EntityType.PLAYER);
		add(EntityType.VILLAGER);

		add(EntityType.WARDEN, lootTable -> lootTable.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.ECHO_SHARD))));
	}

	protected void add(EntityType<?> entityType) {
		add(entityType, createLootTable(entityType));
	}

	protected void add(EntityType<?> entityType, Consumer<LootTable.Builder> consumer) {
		LootTable.Builder lootTable = createLootTable(entityType);
		consumer.accept(lootTable);
		add(entityType, lootTable);
	}

	protected LootTable.Builder createLootTable(EntityType<?> entityType) {
		LootTable.Builder lootTable = LootTable.lootTable();

		createCommonPool(entityType).ifPresent(lootTable::withPool);
		createSpecialPool(entityType).ifPresent(lootTable::withPool);

		return lootTable;
	}

	protected Optional<LootPool.Builder> createCommonPool(EntityType<?> entityType) {
		LootPool.Builder builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		boolean hasLoot = false;

		EntityType<? extends LivingEntity> livingEntityType = (EntityType<? extends LivingEntity>) entityType;
		AttributeSupplier baseAttributes = DefaultAttributes.getSupplier(livingEntityType);

		float volume = entityType.getWidth() * entityType.getHeight() * entityType.getWidth();
		float fangMultiplier = 0.825f;
		float clawMultiplier = 7f;
		float marrowMultiplier = 2.9f;
		float sinewMultiplier = 7f;
		float bileGlandMultiplier = 0.5f;

		boolean hasToxinGland = TOXIC_MOBS.contains(entityType);
		boolean hasVolatileGland = VOLATILE_MOBS.contains(entityType);

		if (SHARP_FANG_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * fangMultiplier + 1));

			if (baseAttributes.hasAttribute(Attributes.ATTACK_DAMAGE) && baseAttributes.getValue(Attributes.ATTACK_DAMAGE) >= 5d) {
				maxCount += 1;
			}

			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_FANG.get()).setWeight(140)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			hasLoot = true;
		}

		if (SHARP_CLAW_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * clawMultiplier + 1));

			if (baseAttributes.hasAttribute(Attributes.ATTACK_DAMAGE) && baseAttributes.getValue(Attributes.ATTACK_DAMAGE) >= 5d) {
				maxCount += 1;
			}

			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_CLAW.get()).setWeight(150)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (!INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * sinewMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_SINEW.get()).setWeight(70)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (volume >= 0.25f && !hasToxinGland && !hasVolatileGland && !INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * bileGlandMultiplier + 1));
			NumberProvider countProvider = UniformGenerator.between(0, maxCount);

			int weight = 40;

			if (!baseAttributes.hasAttribute(Attributes.ATTACK_DAMAGE) || baseAttributes.getValue(Attributes.ATTACK_DAMAGE) <= 0.125d) {
				weight += 10;
			}

			builder.add(
					LootItem.lootTableItem(ModItems.GENERIC_MOB_GLAND.get()).setWeight(weight)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (BONE_MARROW_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * marrowMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_MARROW.get()).setWeight(45)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (!hasLoot) return Optional.empty();

		return Optional.of(builder);
	}

	protected Optional<LootPool.Builder> createSpecialPool(EntityType<?> entityType) {
		LootPool.Builder builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		boolean hasLoot = false;

		float volume = entityType.getWidth() * entityType.getHeight() * entityType.getWidth();
		float organMultiplier = 0.9f;
		float witheredMarrowMultiplier = 3f;

		boolean hasToxinGland = TOXIC_MOBS.contains(entityType);
		boolean hasVolatileGland = VOLATILE_MOBS.contains(entityType);

		if (hasToxinGland) {
			int maxCount = Mth.ceil(Math.log(volume * organMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.TOXIN_GLAND.get()).setWeight(75)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (hasVolatileGland) {
			int maxCount = Mth.ceil(Math.log(volume * organMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.VOLATILE_GLAND.get()).setWeight(50)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (WITHERED_BONE_MARROW_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * witheredMarrowMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.WITHERED_MOB_MARROW.get()).setWeight(65)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);
			hasLoot = true;
		}

		if (!hasLoot) return Optional.empty();

		return Optional.of(builder);
	}

}
