package com.github.elenterius.biomancy.entity.mob.fleshblob;

import com.github.elenterius.biomancy.entity.mob.PrimordialCradleUser;
import com.github.elenterius.biomancy.entity.mob.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.mob.ai.goal.*;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class PrimordialHangryEaterFleshBlob extends EaterFleshBlob implements Enemy, PrimordialFleshkin, PrimordialCradleUser {

	public static final float BASE_MAX_HEALTH = 20;
	public static final float BASE_ARMOR = 2f;
	public static final float BASE_ATTACK_DAMAGE = 4f;

	protected static final Predicate<LivingEntity> IS_VALID_ATTACK_TARGET = Predicate.not(PrimordialFleshkin.class::isInstance);

	public PrimordialHangryEaterFleshBlob(EntityType<? extends PrimordialHangryEaterFleshBlob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, BASE_ARMOR)
				.add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * BASE_MAX_HEALTH);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * BASE_ARMOR);
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE * (size * 0.5f));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5f));
		goalSelector.addGoal(3, new FindItemGoal(this, 12f, SPECIAL_ITEM_ENTITY_FILTER));
		goalSelector.addGoal(3, new EatFoodItemGoal<>(this, 0.25f));
		goalSelector.addGoal(4, new FleshBlobAttackGoal(this, 1.2f));
		goalSelector.addGoal(5, new AvoidEntityGoal<>(this, AbstractGolem.class, 6f, 1f, 1.2f));
		goalSelector.addGoal(5, new UsePrimordialCradleGoal<>(this));
		goalSelector.addGoal(6, new DanceNearJukeboxGoal<>(this));
		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1f));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FleshBlob.class, false, IS_VALID_ATTACK_TARGET));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, false, IS_VALID_ATTACK_TARGET));
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		if (!(spawnData instanceof FleshBlobSpawnData)) {
			spawnData = new FleshBlobSpawnData.Tumors((byte) 0);
		}
		return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	public ItemStack getTributeItemForCradle() {
		return getItemBySlot(EquipmentSlot.OFFHAND);
	}

	@Override
	public boolean hasTributeForCradle() {
		ItemStack stack = getTributeItemForCradle();
		return SPECIAL_ITEMS_TO_HOLD.contains(stack.getItem());
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		if (SPECIAL_ITEMS_TO_HOLD.contains(stack.getItem())) {
			return getItemBySlot(EquipmentSlot.OFFHAND).isEmpty();
		}
		return super.canHoldItem(stack);
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (SPECIAL_ITEMS_TO_HOLD.contains(stack.getItem()) && getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
			onItemPickup(itemEntity);

			setItemSlot(EquipmentSlot.OFFHAND, stack);
			setGuaranteedDrop(EquipmentSlot.OFFHAND);

			take(itemEntity, stack.getCount());
			itemEntity.discard();
		}
		else {
			super.pickUpItem(itemEntity);
		}
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effectInstance) {
		if (effectInstance.getEffect() == ModMobEffects.PRIMORDIAL_INFESTATION.get()) return false;
		return super.canBeAffected(effectInstance);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (reason == RemovalReason.KILLED && !level().isClientSide && level() instanceof ServerLevel serverLevel) {
			placeMalignantFleshBlocks(serverLevel);
		}
		super.remove(reason);
	}

	protected void placeMalignantFleshBlocks(ServerLevel serverLevel) {
		if (!isDeadOrDying()) return;
		if (isFreezing() || isOnFire()) return;

		BlockPos pos = blockPosition();
		if (!PrimordialEcosystem.placeMalignantBlocks(serverLevel, pos, this)) {
			for (int i = 0; i < 4; i++) {
				BlockPos relativePos = pos.relative(Direction.from2DDataValue(i));
				if (PrimordialEcosystem.placeMalignantBlocks(serverLevel, relativePos, this)) break;
			}
		}
	}
}
