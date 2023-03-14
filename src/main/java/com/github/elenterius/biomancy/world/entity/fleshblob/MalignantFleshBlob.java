package com.github.elenterius.biomancy.world.entity.fleshblob;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.DirectionalSlabBlock;
import com.github.elenterius.biomancy.world.block.FleshVeinsBlock;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.world.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.ai.goal.BurningOrFreezingPanicGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.DanceNearJukeboxGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.FindItemGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Predicate;

public class MalignantFleshBlob extends AbstractFleshBlob implements Enemy {

	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> {
		if (!FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity)) return false;

		ItemStack stack = itemEntity.getItem();
		if (stack.is(ModItems.LIVING_FLESH.get())) return true;
		return stack.isEdible() && Optional.ofNullable(stack.getFoodProperties(null)).map(FoodProperties::isMeat).orElse(false);
	};

	protected static final Predicate<LivingEntity> TARGET_PREDICATE = livingEntity -> !(livingEntity instanceof MalignantFleshBlob);
	private int biomass = 0;

	public MalignantFleshBlob(EntityType<? extends MalignantFleshBlob> entityType, Level level) {
		super(entityType, level);
		setCanPickUpLoot(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, 0.5f)
				.add(Attributes.ATTACK_DAMAGE, 6);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * 20f);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * 0.5f);
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, Math.max(6, size));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5f));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(4, new CustomAttackGoal(this, 1.2f));
		goalSelector.addGoal(4, new UsePrimordialCradleGoal(this));
		goalSelector.addGoal(6, new DanceNearJukeboxGoal<>(this));
		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1f));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FleshBlob.class, false));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, false, TARGET_PREDICATE));
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return getType().getDefaultLootTable();
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		if (stack.is(ModItems.LIVING_FLESH.get())) return true;
		return stack.isEdible() && Optional.ofNullable(stack.getFoodProperties(null)).map(FoodProperties::isMeat).orElse(false);
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			onItemPickup(itemEntity);
			take(itemEntity, stack.getCount());
			itemEntity.discard();

			addBiomass(stack);
			level.playSound(null, blockPosition(), ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.7f, 0.95f + random.nextFloat() * 0.5f);
		}
	}

	protected void addBiomass(ItemStack stack) {
		if (level.isClientSide) return;

		if (stack.is(ModItems.LIVING_FLESH.get())) {
			biomass += 200 * stack.getCount();
		}
		else if (stack.isEdible()) {
			biomass += Optional.ofNullable(stack.getFoodProperties(null))
					.filter(FoodProperties::isMeat)
					.map(food -> 20).orElse(0) * stack.getCount();
		}
	}

	protected void consumeBiomass() {
		if (level.isClientSide) return;
		if (biomass <= 0) return;

		float health = getHealth();
		float maxHealth = getMaxHealth();
		if (health < maxHealth) {
			float healAmount = Math.min(health - maxHealth, biomass);
			heal(healAmount);
			biomass -= healAmount;
		}
		else if (biomass > 80 && biomass < 120) {
			byte blobSize = getBlobSize();
			float growChance = 1f - blobSize / (float) MAX_SIZE;
			if (blobSize < MAX_SIZE && level.random.nextFloat() < growChance) {
				setBlobSize((byte) (blobSize + 1), true);
				biomass -= 80;
				level.playSound(null, blockPosition(), ModSoundEvents.FLESH_BLOCK_PLACE.get(), getSoundSource(), 1f, 0.95f + level.random.nextFloat() * 0.5f);
			}
		}
	}

	protected boolean hasBiomass() {
		return biomass >= 120;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (tickCount % 20 == 0 && level.random.nextFloat() < 0.25f) {
			consumeBiomass();
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level.isClientSide && isDeadOrDying() && !isFreezing() && !isOnFire()) {
			BlockPos pos = blockPosition();
			if (!placeMalignantFlesh(pos)) {
				for (int i = 0; i < 4; i++) {
					BlockPos relativePos = pos.relative(Direction.from2DDataValue(i));
					if (placeMalignantFlesh(relativePos)) break;
				}
			}
		}

		super.remove(reason);
	}

	private boolean placeMalignantFlesh(BlockPos pos) {
		BlockState currentState = level.getBlockState(pos);
		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		if (currentState.is(veinsBlock)) {
			if (veinsBlock.getSpreader().spreadAll(currentState, level, pos, false) > 0) {
				for (Direction subDirection : Direction.allShuffled(random)) {
					BlockPos neighborPos = pos.relative(subDirection);
					BlockState neighborState = level.getBlockState(neighborPos);
					veinsBlock.increaseCharge((ServerLevel) level, neighborPos, neighborState, 1);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			else {
				Block block = getBlobSize() < MAX_SIZE / 2f ? ModBlocks.MALIGNANT_FLESH_SLAB.get() : ModBlocks.MALIGNANT_FLESH.get();
				level.setBlockAndUpdate(pos, block.defaultBlockState());
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + random.nextFloat() * 0.5f);
			}
			return true;
		}
		else if (currentState.is(ModBlocks.MALIGNANT_FLESH_SLAB.get())) {
			if (currentState.getValue(DirectionalSlabBlock.TYPE) == DirectionalSlabType.FULL) return false;

			level.setBlockAndUpdate(pos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());


			level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);
			return true;
		}
		else if (currentState.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
			BlockState stateForPlacement = veinsBlock.getStateForPlacement(currentState, level, pos, Direction.DOWN, getBlobSize() * 2);
			if (stateForPlacement != null) {
				level.setBlockAndUpdate(pos, stateForPlacement);
				if (random.nextFloat() < (float) getBlobSize() / MAX_SIZE) {
					veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, pos, random);
				}
				level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
				return true;
			}
		}

		return false;
	}

	static class CustomAttackGoal extends MeleeAttackGoal {

		public CustomAttackGoal(MalignantFleshBlob mob, double speed) {
			super(mob, speed, true);
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 2f + attackTarget.getBbWidth();
		}
	}

	static class UsePrimordialCradleGoal extends MoveToBlockGoal {
		private final MalignantFleshBlob fleshBlob;
		private boolean wantsToUseCradle;
		private boolean canUseCradle;

		public UsePrimordialCradleGoal(MalignantFleshBlob mob) {
			super(mob, 1f, 24, 8);
			fleshBlob = mob;
		}

		@Override
		public boolean canUse() {
			if (nextStartTick <= 0) {
				canUseCradle = false;
				wantsToUseCradle = fleshBlob.hasBiomass();
			}
			return super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return canUseCradle && super.canContinueToUse();
		}

		@Override
		public void tick() {
			super.tick();

			fleshBlob.getLookControl().setLookAt(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d, 10f, fleshBlob.getMaxHeadXRot());
			if (!isReachedTarget()) return;

			if (canUseCradle) {
				Level level = fleshBlob.level;
				BlockPos pos = blockPos;
				BlockState state = level.getBlockState(pos);

				if (state.getBlock() instanceof PrimordialCradleBlock && level.getBlockEntity(pos) instanceof PrimordialCradleBlockEntity creator) {
					sacrificeItem(level, pos, creator, ModItems.LIVING_FLESH.get());
					int max = fleshBlob.biomass / 100;
					for (int i = 0; i < max; i++) {
						if (creator.isFull()) break;
						sacrificeItem(level, pos, creator, ModItems.CREATOR_MIX.get());
						fleshBlob.biomass -= 100;
					}
				}
			}

			canUseCradle = false;
			nextStartTick = 10;
		}

		@Override
		public double acceptedDistance() {
			return 1.5d;
		}

		@Override
		protected boolean isValidTarget(LevelReader level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof PrimordialCradleBlock && wantsToUseCradle && !canUseCradle && level.getBlockEntity(pos) instanceof PrimordialCradleBlockEntity creator && !creator.isFull()) {
				canUseCradle = true;
				return true;
			}
			return false;
		}

		private void sacrificeItem(Level level, BlockPos pos, PrimordialCradleBlockEntity creator, Item item) {
			ItemStack stack = new ItemStack(item, 1);
			if (creator.insertItem(stack)) {
				SoundEvent soundEvent = creator.isFull() ? ModSoundEvents.CREATOR_BECAME_FULL.get() : ModSoundEvents.CREATOR_EAT.get();
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, soundEvent);
			}
		}

	}
}
