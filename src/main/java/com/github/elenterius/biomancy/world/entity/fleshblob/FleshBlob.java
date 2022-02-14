package com.github.elenterius.biomancy.world.entity.fleshblob;

import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.world.entity.JumpMoveMob;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpControl;
import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpMoveControl;
import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpMoveHelper;
import com.github.elenterius.biomancy.world.entity.ai.goal.FindItemGoal;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FleshBlob extends PathfinderMob implements Enemy, JumpMoveMob<FleshBlob>, IAnimatable {

	public static final byte EATING_STATE_ID = 60;
	public static final byte JUMPING_STATE_ID = 61;

	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity) && itemEntity.getItem().isEdible();
	public static final int MAX_SIZE = 10;

	private static final EntityDataAccessor<Byte> BLOB_SIZE = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> TUMORS = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> BLOB_TYPE = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);

	private final DNAStorage storedDNA = new DNAStorage(4);
	private final AnimationFactory animationFactory = new AnimationFactory(this);
	protected GenericJumpMoveHelper<FleshBlob> jumpMoveState;
	private int eatTimer;

	public FleshBlob(EntityType<? extends FleshBlob> entityType, Level level) {
		super(entityType, level);
		jumpMoveState = new GenericJumpMoveHelper<>(this, JUMPING_STATE_ID);
		setCanPickUpLoot(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, 8)
				.add(Attributes.ATTACK_DAMAGE, 3);
	}

	public static float getScaleMultiplier(FleshBlob fleshBlob) {
		return 0.5f + fleshBlob.getBlobSize() * 0.25f;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(BLOB_TYPE, (byte) 0);
		entityData.define(BLOB_SIZE, (byte) 1);
		entityData.define(TUMORS, TumorFlag.randomFlags(random));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new CustomPanicGoal(this, 1.5d));
		goalSelector.addGoal(2, new CustomAttackGoal(this, 1.2d));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1d));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, false));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	@Override
	public void aiStep() {
		updateEatTime();
		super.aiStep();
		jumpMoveState.onAiStep(this);
	}

	public void setBlobSize(byte size, boolean resetHealth) {
		size = (byte) Mth.clamp(size, 1, MAX_SIZE);
		entityData.set(BLOB_SIZE, size);
		reapplyPosition();
		refreshDimensions();
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * 10d);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.1f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, size + (getBlobType() == 1 ? 8d : 3d));
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * 3d);
		if (resetHealth) setHealth(getMaxHealth());
		xpReward = size;
	}

	public byte getBlobSize() {
		return entityData.get(BLOB_SIZE);
	}

	public void randomizeTumors() {
		entityData.set(TUMORS, (byte) random.nextInt(Byte.MAX_VALUE + 1));
	}

	public byte getTumorFlags() {
		return entityData.get(TUMORS);
	}

	public void setTumorFlags(byte flags) {
		entityData.set(TUMORS, flags);
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(getScaleMultiplier(this));
	}

	public boolean isHangry() {
		return entityData.get(BLOB_TYPE) == 1;
	}

	public void setHangry() {
		if (!isHangry()) setBlobType((byte) 1);
	}

	public byte getBlobType() {
		return entityData.get(BLOB_TYPE);
	}

	public void setBlobType(byte flag) {
		double damageModifier = flag == 1 ? 8d : 3d;
		double baseDamage = getBlobSize();
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, baseDamage + damageModifier);
		entityData.set(BLOB_TYPE, flag);
	}

	@Override
	public void refreshDimensions() {
		double x = getX();
		double y = getY();
		double z = getZ();
		super.refreshDimensions();
		setPos(x, y, z);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (BLOB_SIZE.equals(key)) {
			refreshDimensions();
			setYRot(yHeadRot);
			setYBodyRot(yHeadRot);
			if (isInWater() && random.nextFloat() < 0.05f) {
				doWaterSplashEffect();
			}
		}
		super.onSyncedDataUpdated(key);
	}

	public void addMobDNA(EntityType<LivingEntity> entityType) {
		if (!storedDNA.addDNA(entityType)) {
			Explosion.BlockInteraction mode = ForgeEventFactory.getMobGriefingEvent(level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
			level.explode(this, getX(), getY(), getZ(), getBlobSize() + 4f * 1.25f, mode);
			remove(RemovalReason.KILLED);
		}
	}

	public void clearStoredDNA() {
		storedDNA.clear();
	}

	public DNAStorage getStoredDNA() {
		return storedDNA;
	}

	private void updateEatTime() {
		if (isEffectiveAi() && isAlive() && ++eatTimer > 300 && getTarget() == null) {
			eatFood();
		}
	}

	private void eatFood() {
		ItemStack heldStack = getItemBySlot(EquipmentSlot.MAINHAND);
		if (heldStack.getItem().isEdible()) {
			if (eatTimer > 400) {
				FoodProperties food = heldStack.getItem().getFoodProperties();
				ItemStack eatenStack = heldStack.finishUsingItem(level, this);
				if (!eatenStack.isEmpty()) {
					setItemSlot(EquipmentSlot.MAINHAND, eatenStack);
				}
				float health = getHealth();
				if (health < getMaxHealth()) {
					heal(getFoodHealAmount(food));
				}
				else {
					byte blobSize = getBlobSize();
					if (blobSize < MAX_SIZE && random.nextFloat() < getGrowChance(food)) {
						setBlobSize((byte) (blobSize + 1), true);
					}
				}
				eatTimer = 0;
			}
			else if (random.nextFloat() < 0.1f) {
				playSound(getEatingSound(heldStack), 1f, 1f);
				level.broadcastEntityEvent(this, EATING_STATE_ID);
			}
		}
	}

	private float getFoodHealAmount(@Nullable FoodProperties food) {
		if (food == null) return 0.5f;
		return food.getNutrition() * (food.isMeat() ? 1.25f : 0.75f);
	}

	private float getGrowChance(@Nullable FoodProperties food) {
		if (food == null) return 0.4f;
		return 0.4f + (food.getNutrition() * (food.isMeat() ? 0.5f : 0.25f)) / MAX_SIZE;
	}

	@Override
	public boolean canTakeItem(ItemStack stack) {
		if (!canPickUpLoot()) return false;
		EquipmentSlot slotType = LivingEntity.getEquipmentSlotForItem(stack);
		return slotType == EquipmentSlot.MAINHAND && getItemBySlot(slotType).isEmpty();
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		Item item = stack.getItem();
		ItemStack heldStack = getItemBySlot(EquipmentSlot.MAINHAND);
		return eatTimer > 0 && item.isEdible() && (heldStack.isEmpty() || !heldStack.getItem().isEdible());
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			onItemPickup(itemEntity);
			setItemSlot(EquipmentSlot.MAINHAND, ItemHandlerHelper.copyStackWithSize(stack, 1));
			handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2f;
			take(itemEntity, 1);
			itemEntity.discard();
			eatTimer = 0;
		}
	}

	@Override
	protected void dropEquipment() {
		ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
		if (!stack.isEmpty()) {
			spawnAtLocation(stack);
			setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (jumpMoveState.handleEntityEvent(id)) {
			spawnSprintParticle();
		}
		else if (id == EATING_STATE_ID) {
			spawnEatingParticles();
		}
		else super.handleEntityEvent(id);
	}

	private void spawnEatingParticles() {
		ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
		if (!stack.isEmpty()) {
			float pitch = -getXRot() * Mth.DEG_TO_RAD;
			float yaw = -getYRot() * Mth.DEG_TO_RAD;
			for (int i = 0; i < 8; ++i) {
				Vec3 vector3d = new Vec3((random.nextFloat() - 0.5d) * 0.1d, random.nextFloat() * 0.1d + 0.1d, 0).xRot(pitch).yRot(yaw);
				level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), getX() + getLookAngle().x / 2d, getY(), getZ() + getLookAngle().z / 2d, vector3d.x, vector3d.y + 0.05d, vector3d.z);
			}
		}
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return size.height * 0.5f;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putByte("BlobType", getBlobType());
		tag.putByte("Size", getBlobSize());
		tag.putByte("Tumors", getTumorFlags());
		tag.put("StoredDNA", storedDNA.toJson());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setBlobType(tag.getByte("BlobType"));
		setBlobSize(tag.getByte("Size"), false);
		setTumorFlags(tag.getByte("Tumors"));
		storedDNA.fromJson(tag.getCompound("StoredDNA"));
	}

	@Override
	public SoundSource getSoundSource() {
		return isHangry() ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
	}

	@Override
	public boolean canSpawnSprintParticle() {
		return false;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return switch (getBlobSize()) {
			case 2 -> ModLoot.Entity.FLESH_BLOB_SIZE_2;
			case 3 -> ModLoot.Entity.FLESH_BLOB_SIZE_3;
			case 4 -> ModLoot.Entity.FLESH_BLOB_SIZE_4;
			case 5 -> ModLoot.Entity.FLESH_BLOB_SIZE_5;
			case 6 -> ModLoot.Entity.FLESH_BLOB_SIZE_6;
			case 7 -> ModLoot.Entity.FLESH_BLOB_SIZE_7;
			case 8 -> ModLoot.Entity.FLESH_BLOB_SIZE_8;
			case 9 -> ModLoot.Entity.FLESH_BLOB_SIZE_9;
			case 10 -> ModLoot.Entity.FLESH_BLOB_SIZE_10;
			default -> getType().getDefaultLootTable();
		};
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		if (spawnData instanceof SpawnData data) {
			setBlobType(data.customFlags);
			setTumorFlags(data.tumorFlags);
		}
		else {
			final byte customFlags = (byte) (random.nextFloat() < 0.45 ? 1 : 0);
			final byte tumorFlags = TumorFlag.randomFlags(random);
			setBlobType(customFlags);
			setTumorFlags(tumorFlags);
			spawnData = new SpawnData(customFlags, tumorFlags);
		}

		return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
	}

	@Override
	protected void jumpFromGround() {
		super.jumpFromGround();
		jumpMoveState.onJumpFromGround(this);
	}

	@Override
	protected void customServerAiStep() {
		jumpMoveState.onServerAiStep(this);
	}

	@Override
	public boolean isJumping() {
		return jumping;
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			playSound(getJumpSound());
		}
	}

	private void playSound(SoundEvent soundEvent) {
		playSound(soundEvent, getSoundVolume(), (random.nextFloat(-1, 1) * 0.2f + 1f) * 0.8f);
	}

	@Override
	public void setMoveControl(GenericJumpMoveControl control) {
		moveControl = control;
	}

	@Override
	public void setJumpControl(GenericJumpControl control) {
		jumpControl = control;
	}

	@Override
	public void setJumpHeading(double x, double z) {
		setYRot((float) (Mth.atan2(z - getZ(), x - getX()) * Mth.RAD_TO_DEG) - 90f);
	}

	@Override
	public GenericJumpMoveHelper<? extends JumpMoveMob<FleshBlob>> getJumpMoveState() {
		return jumpMoveState;
	}

	@Override
	public SoundEvent getJumpSound() {
		return SoundEvents.SLIME_JUMP;
	}

	public SoundEvent getImpactSound() {
		return SoundEvents.SLIME_SQUISH;
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		float jumpPct = jumpMoveState.getJumpCompletionPct(event.getPartialTick());
		if (jumpPct > 0) {
			event.getController().transitionLengthTicks = 0;
			if (jumpPct <= 0.28f) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("fleshkin_blob.jump.startup").addAnimation("fleshkin_blob.jump.air.loop", false));
			}
			else if (jumpPct < 0.72f) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("fleshkin_blob.jump.air.loop", true));
			}
			else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("fleshkin_blob.jump.impact"));
			}
		}
		else {
			event.getController().transitionLengthTicks = 10;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("fleshkin_blob.ground.loop", true));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	public record SpawnData(byte customFlags, byte tumorFlags) implements SpawnGroupData {}

	static class CustomAttackGoal extends MeleeAttackGoal {

		public CustomAttackGoal(FleshBlob mob, double speed) {
			super(mob, speed, true);
		}

		@Override
		public boolean canContinueToUse() {
			FleshBlob fleshBlob = (FleshBlob) mob;
			if (!fleshBlob.isHangry() && mob.getRandom().nextFloat() < 0.2f) {
				mob.setTarget(null);
				return false;
			}
			return super.canContinueToUse();
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 2f + attackTarget.getBbWidth();
		}
	}

	static class CustomPanicGoal extends PanicGoal {

		public CustomPanicGoal(FleshBlob mob, double speedModifier) {
			super(mob, speedModifier);
		}

		@Override
		public boolean canUse() {
			FleshBlob fleshBlob = (FleshBlob) mob;
			if (!fleshBlob.isHangry() || mob.isOnFire()) return super.canUse();
			return false;
		}

		@Override
		public boolean canContinueToUse() {
			FleshBlob fleshBlob = (FleshBlob) mob;
			if (!fleshBlob.isHangry() || mob.isOnFire()) return super.canContinueToUse();
			return false;
		}

	}

}
