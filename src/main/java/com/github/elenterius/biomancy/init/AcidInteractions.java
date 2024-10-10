package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AcidInteractions {

	public static final Map<Item, CauldronInteraction> ACID_CAULDRON = CauldronInteraction.newInteractionMap();

	public static final Map<Block, BlockState> NORMAL_TO_ERODED_BLOCK_CONVERSION = Map.of(
			Blocks.GRASS_BLOCK, Blocks.DIRT.defaultBlockState(),
			Blocks.COBBLESTONE, Blocks.GRAVEL.defaultBlockState(),
			Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(),
			Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(),
			Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState(),
			Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(),
			Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS.defaultBlockState()
	);

	private AcidInteractions() {}

	static void register() {
		registerCauldronInteractions();
	}

	private static void registerCauldronInteractions() {
		final CauldronInteraction fillWithAcid = (state, level, pos, player, hand, stack) -> {
			SoundEvent sound = Objects.requireNonNullElse(ModFluids.ACID_TYPE.get().getSound(SoundActions.BUCKET_EMPTY), SoundEvents.BUCKET_EMPTY);
			return CauldronInteraction.emptyBucket(level, pos, player, hand, stack, ModBlocks.ACID_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), sound);
		};

		CauldronInteraction.EMPTY.put(ModItems.ACID_BUCKET.get(), fillWithAcid);

		//we wrap the original potion interaction to retain its behavior while injecting our own logic
		final CauldronInteraction originalPotionInteraction = Objects.requireNonNull(CauldronInteraction.EMPTY.get(Items.POTION));
		CauldronInteraction.EMPTY.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
			if (PotionUtils.getPotion(stack) != ModPotions.GASTRIC_JUICE.get()) {
				return originalPotionInteraction.interact(state, level, pos, player, hand, stack);
			}

			if (!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				level.setBlockAndUpdate(pos, ModBlocks.ACID_CAULDRON.get().defaultBlockState());
				level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1f, 1f);
				level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		});

		ACID_CAULDRON.put(ModItems.ACID_BUCKET.get(), fillWithAcid);
		ACID_CAULDRON.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
			SoundEvent sound = Objects.requireNonNullElse(ModFluids.ACID_TYPE.get().getSound(SoundActions.BUCKET_FILL), SoundEvents.BUCKET_FILL);
			return CauldronInteraction.fillBucket(state, level, pos, player, hand, stack, ModItems.ACID_BUCKET.get().getDefaultInstance(), AcidInteractions::isCauldronFull, sound);
		});
		ACID_CAULDRON.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.GASTRIC_JUICE.get())));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
				level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);
				level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		});
		ACID_CAULDRON.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
			if (!isCauldronFull(state) && PotionUtils.getPotion(stack) == ModPotions.GASTRIC_JUICE.get()) {
				if (!level.isClientSide) {
					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
					level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
					level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1f, 1f);
					level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			return InteractionResult.PASS;
		});
	}

	private static boolean isCauldronFull(BlockState state) {
		return state.getValue(LayeredCauldronBlock.LEVEL) == 3;
	}

	@SuppressWarnings("deprecation")
	@Nullable
	public static Block convertBlock(Block block) {
		if (block instanceof WeatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			return WeatheringCopper.getNext(block).get();
		}
		else if (block.builtInRegistryHolder().is(ModBlockTags.ACID_DESTRUCTIBLE)) {
			return Blocks.AIR;
		}
		else if (NORMAL_TO_ERODED_BLOCK_CONVERSION.containsKey(block)) {
			return NORMAL_TO_ERODED_BLOCK_CONVERSION.get(block).getBlock();
		}
		return null;
	}

	public static void handleEntityInsideAcidFluid(LivingEntity livingEntity) {
		if (livingEntity.isSpectator()) return;
		if (livingEntity.tickCount % 5 != 0) return;

		if (!livingEntity.isInFluidType(ModFluids.ACID_TYPE.get())) return;

		handleEntityInsideAcid(livingEntity);
	}

	public static void handleEntityInsideAcid(LivingEntity livingEntity) {
		if (!livingEntity.level().isClientSide) {
			CombatUtil.applyAcidEffect(livingEntity, 4);
		}
		else if (livingEntity.tickCount % 10 == 0 && livingEntity.getRandom().nextFloat() < 0.4f) {
			Level level = livingEntity.level();
			RandomSource random = livingEntity.getRandom();
			Vec3 pos = livingEntity.position();
			double height = livingEntity.getBoundingBox().getYsize() * 0.5f;

			level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
			for (int i = 0; i < 4; i++) {
				level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x + random.nextDouble(), pos.y + random.nextDouble() * height, pos.z + random.nextDouble(), 0, 0.1d, 0);
			}
		}
	}

	public static final class InWorldItemDigesting {
		//NBT Tag keys
		public static final String BASE_DATA_KEY = "biomancy:acid_digestion";
		public static final String TIMER_KEY = "timer";
		public static final String RECIPE_KEY = "recipe";
		//Balancing multiplier applied to the output of any found recipes.
		public static final double EFFICIENCY = 0.8;
		//How many times tryDigestSubmergedItem should be called on a digestible item before it is processed.
		//This is not directly ticks, as submerged items are only actually processed every 10 ticks.
		public static final int DELAY = 10;

		public static void tryDigestSubmergedItem(ItemEntity itemEntity) {
			if (!digestible(itemEntity)) return;
			ItemStack stack = itemEntity.getItem();
			CompoundTag entityData = itemEntity.getPersistentData();
			CompoundTag digestionData = getOrCreateDigestionData(entityData);
			if (itemEntity.level().isClientSide && digestionData.getInt(TIMER_KEY) > 0) {
				Vec3 pos = itemEntity.position();
				RandomSource random = itemEntity.level().getRandom();
				itemEntity.level().addParticle(ModParticleTypes.ACID_BUBBLE.get(),pos.x,pos.y,pos.z,random.nextGaussian()/100,Math.abs(random.nextGaussian()/50),random.nextGaussian()/100);
				itemEntity.level().addParticle(ParticleTypes.SMOKE,pos.x,pos.y,pos.z,random.nextGaussian()/100,Math.abs(random.nextGaussian()/100),random.nextGaussian()/100);
				return;
			}

			if (itemEntity.getAge() % 10 != 0) return; //Only tick digestion every 10 ticks.
			Optional<DigestingRecipe> optionalRecipe = getDigestionRecipe(itemEntity, stack, digestionData);

			if (optionalRecipe.isEmpty()) return;
			if (optionalRecipe.get().getId() != ResourceLocation.tryParse(digestionData.getString(RECIPE_KEY))) {
				digestionData.putString(RECIPE_KEY, optionalRecipe.get().getId().toString());
			}

			int currentDigestionTime = digestionData.getInt(TIMER_KEY);
			if (currentDigestionTime < DELAY) {
				digestionData.putInt(TIMER_KEY, currentDigestionTime + 1);
				entityData.put(BASE_DATA_KEY,digestionData);
			} else {
				digestIntoNutrientPasteStacks(itemEntity, optionalRecipe.get());
				itemEntity.getPersistentData().remove(BASE_DATA_KEY);
			}
		}

		public static CompoundTag getOrCreateDigestionData(CompoundTag entityData) {
			if (entityData.contains(BASE_DATA_KEY)) return entityData.getCompound(BASE_DATA_KEY);
			return new CompoundTag();
		}

		public static void digestIntoNutrientPasteStacks(ItemEntity itemEntity, DigestingRecipe recipe) {
			BehavioralInventory<?> tempInventory = BehavioralInventory.createServerContents(1, player -> false, () -> {});
			tempInventory.insertItemStack(itemEntity.getItem());
			ItemStack resultStack = recipe.assemble(tempInventory, itemEntity.level().registryAccess());
			int totalToOutput = (int) Math.floor(resultStack.getCount() * itemEntity.getItem().getCount() * EFFICIENCY);
			if (totalToOutput > 64) totalToOutput = splitIntoStacks(itemEntity, totalToOutput);
			itemEntity.setItem(new ItemStack(ModItems.NUTRIENT_PASTE.get(), totalToOutput));
			itemEntity.playSound(SoundEvents.PLAYER_BURP);
		}

		public static int splitIntoStacks(ItemEntity itemEntity, int numToSplit) {
			Level level = itemEntity.level();
			while (numToSplit > 64) {
				DefaultDispenseItemBehavior.spawnItem(level, new ItemStack(ModItems.NUTRIENT_PASTE.get(), 64), 1, Direction.UP, itemEntity.position());
				numToSplit -= 64;
			}
			return numToSplit;
		}

		@SuppressWarnings("RedundantIfStatement") //Let me write readable code please, thanks
		public static boolean digestible(ItemEntity itemEntity) {
			if (!itemEntity.isInFluidType(ModFluids.ACID_TYPE.get()) && !itemEntity.level().getBlockState(itemEntity.blockPosition()).is(ModBlocks.ACID_CAULDRON.get())) return false;
			if (itemEntity.getItem().is(ModItemTags.CANNOT_BE_DIGESTED_IN_ACID)) return false;
			return true;
		}

		private static Optional<DigestingRecipe> getDigestionRecipe(ItemEntity itemEntity, ItemStack stack, CompoundTag digestionData) {
			Optional<DigestingRecipe> recipe = Optional.empty();
			ResourceLocation lastRecipeId = ResourceLocation.tryParse(digestionData.getString(RECIPE_KEY));
			if (lastRecipeId != null) recipe = DigesterBlockEntity.RECIPE_TYPE.get().getRecipeById(itemEntity.level(), lastRecipeId);
			if (recipe.isEmpty()) recipe = DigesterBlockEntity.RECIPE_TYPE.get().getRecipeForIngredient(itemEntity.level(), stack);
			return recipe; //If it's still empty, there's no recipe that matches the item stack.
		}
	}
}
