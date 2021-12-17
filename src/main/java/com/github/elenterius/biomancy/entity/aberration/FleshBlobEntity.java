package com.github.elenterius.biomancy.entity.aberration;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.IJumpMovementMob;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpController;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpMovementController;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpMovementHelper;
import com.github.elenterius.biomancy.entity.ai.goal.FindItemGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FleshBlobEntity extends CreatureEntity implements IJumpMovementMob<FleshBlobEntity> {

	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_2 = BiomancyMod.createRL("entities/flesh_blob/size_2");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_3 = BiomancyMod.createRL("entities/flesh_blob/size_3");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_4 = BiomancyMod.createRL("entities/flesh_blob/size_4");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_5 = BiomancyMod.createRL("entities/flesh_blob/size_5");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_6 = BiomancyMod.createRL("entities/flesh_blob/size_6");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_7 = BiomancyMod.createRL("entities/flesh_blob/size_7");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_8 = BiomancyMod.createRL("entities/flesh_blob/size_8");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_9 = BiomancyMod.createRL("entities/flesh_blob/size_9");
	public static final ResourceLocation LOOT_TABLE_FOR_SIZE_10 = BiomancyMod.createRL("entities/flesh_blob/size_10");

	public static final byte EATING_STATE_ID = 61; // any id > 55 should be future compatible. see handleStatusUpdate()
	public static final byte JUMPING_STATE_ID = 60;

	private static final DataParameter<Byte> BLOB_SIZE = EntityDataManager.defineId(FleshBlobEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> FLESH_BLOB_DATA = EntityDataManager.defineId(FleshBlobEntity.class, DataSerializers.BYTE);
	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity) && itemEntity.getItem().isEdible();

	protected GenericJumpMovementHelper<FleshBlobEntity> jumpMovementState;
	private int eatTimer;

	@Nullable
	private List<EntityType<?>> injectedDNAs = null;

	public FleshBlobEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
		jumpMovementState = new GenericJumpMovementHelper<>(this, JUMPING_STATE_ID);
		setCanPickUpLoot(true);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10d)
				.add(Attributes.MOVEMENT_SPEED, 0.3d)
				.add(Attributes.ARMOR, 8d)
				.add(Attributes.ATTACK_DAMAGE, 3d);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new JumpPanicGoal(this, 2.2d));
		goalSelector.addGoal(2, new CustomAvoidEntityGoal<>(this, PlayerEntity.class, 6f, 1.2d, 2d));
		goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.6d));
		goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 10f));
		goalSelector.addGoal(5, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FLESH_BLOB_DATA, (byte) 0);
		entityData.define(BLOB_SIZE, (byte) 1);
	}

	public void setBlobSize(byte size, boolean resetHealth) {
		size = (byte) MathHelper.clamp(size, 1, 10);
		entityData.set(BLOB_SIZE, size);
		reapplyPosition();
		refreshDimensions();
		//noinspection ConstantConditions
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(size * 10);
		//noinspection ConstantConditions
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * size);
		//noinspection ConstantConditions
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(size + (getFleshBlobData() == 1 ? 8 : 3));
		//noinspection ConstantConditions
		getAttribute(Attributes.ARMOR).setBaseValue(size * 3);
		if (resetHealth) setHealth(getMaxHealth());
		xpReward = size;
	}

	public byte getBlobSize() {
		return entityData.get(BLOB_SIZE);
	}

	@Override
	public EntitySize getDimensions(Pose poseIn) {
		return super.getDimensions(poseIn).scale(0.5f + getBlobSize() * 0.5f);
	}

	@Nullable
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		byte flag = (byte) (random.nextFloat() < 0.45 ? 1 : 0);
		if (spawnDataIn instanceof FleshBlobData) {
			flag = ((FleshBlobData) spawnDataIn).flag;
		}
		else {
			spawnDataIn = new FleshBlobData(flag);
		}

		setCustomEntityData(flag);
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public byte getFleshBlobData() {
		return entityData.get(FLESH_BLOB_DATA);
	}

	public boolean isHangry() {
		return entityData.get(FLESH_BLOB_DATA) == 1;
	}

	public void setHangry() {
		if (!isHangry()) setCustomEntityData((byte) 1);
	}

	public void setCustomEntityData(byte flag) {
		if (flag == 1) {
			//noinspection ConstantConditions
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getBlobSize() + 8d);
			goalSelector.addGoal(2, new CustomAttackGoal(this, 1.2d));
			targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, true));

		}
		else if (flag == 0) {
			//noinspection ConstantConditions
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getBlobSize() + 3d);
		}
		entityData.set(FLESH_BLOB_DATA, flag);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		setCustomEntityData(compound.getByte("FleshBlobData"));
		setBlobSize(compound.getByte("Size"), false);
		ListNBT storedDNAs = compound.getList("InjectedDNAs", Constants.NBT.TAG_STRING);
		if (storedDNAs.isEmpty()) {
			injectedDNAs = null;
		}
		else {
			if (injectedDNAs == null) injectedDNAs = new ArrayList<>();
			else injectedDNAs.clear();

			for (int i = 0; i < storedDNAs.size(); i++) {
				String entityTypeId = storedDNAs.getString(i);
				if (!entityTypeId.isEmpty()) {
					EntityType.byString(entityTypeId).ifPresent(type -> injectedDNAs.add(type));
				}
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("FleshBlobData", getFleshBlobData());
		compound.putByte("Size", getBlobSize());

		ListNBT listnbt = new ListNBT();
		if (injectedDNAs != null && !injectedDNAs.isEmpty()) {
			for (EntityType<?> entityType : injectedDNAs) {
				ResourceLocation rl = EntityType.getKey(entityType);
				if (entityType.canSerialize()) listnbt.add(StringNBT.valueOf(rl.toString()));
			}
		}
		compound.put("InjectedDNAs", listnbt);
	}

	public void addForeignEntityDNA(EntityType<?> entityType) {
		if (injectedDNAs == null) injectedDNAs = new ArrayList<>();
		injectedDNAs.add(entityType);

		if (injectedDNAs.size() > 5) { //prevent storing of too many DNAs
			Explosion.Mode mode = ForgeEventFactory.getMobGriefingEvent(level, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
			level.explode(this, getX(), getY(), getZ(), getBlobSize() + 4f * 1.25f, mode);
			remove();
		}
	}

	@Nullable
	public List<EntityType<?>> getForeignEntityDNA() {
		return injectedDNAs;
	}

	public boolean hasForeignEntityDNA() {
		return injectedDNAs != null && !injectedDNAs.isEmpty();
	}

	public void clearForeignEntityDNA() {
		if (injectedDNAs != null) injectedDNAs.clear();
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
	public void onSyncedDataUpdated(DataParameter<?> key) {
		if (BLOB_SIZE.equals(key)) {
			refreshDimensions();
			yRot = yHeadRot;
			yBodyRot = yHeadRot;
			if (isInWater() && random.nextFloat() < 0.05f) {
				doWaterSplashEffect();
			}
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	protected void jumpFromGround() {
		super.jumpFromGround();
		jumpMovementState.updateJump(this);
	}

	@Override
	protected void customServerAiStep() {
		jumpMovementState.updateAIMovement(this);
	}

	@Override
	public void updateRotationYaw(double x, double z) {
		yRot = (float) (MathHelper.atan2(z - getZ(), x - getX()) * (double) (180F / (float) Math.PI)) - 90.0F;
	}

	@Override
	public GenericJumpMovementHelper<? extends IJumpMovementMob<FleshBlobEntity>> getJumpMovementState() {
		return jumpMovementState;
	}

	@Override
	public void aiStep() {
		if (!level.isClientSide && isAlive() && isEffectiveAi()) {
			eatTimer++;
			ItemStack heldStack = getItemBySlot(EquipmentSlotType.MAINHAND);
			if (heldStack.getItem().isEdible() && getTarget() == null) {
				if (eatTimer > 600) {
					Food food = heldStack.getItem().getFoodProperties();
					ItemStack eatenStack = heldStack.finishUsingItem(level, this);
					if (!eatenStack.isEmpty()) {
						setItemSlot(EquipmentSlotType.MAINHAND, eatenStack);
					}
					float modifier = food != null ? (food.getNutrition() * (food.isMeat() ? 0.5f : 0.25f)) / 10f : 0f;
					if (random.nextFloat() < 0.4f + modifier) {
						byte blobSize = getBlobSize();
						if (blobSize < 10) setBlobSize((byte) (blobSize + 1), true);
						else heal(getMaxHealth() - getHealth()); //if the blob is already at max size, heal up to max health instead
					}
					eatTimer = 0;
				}
				else if (eatTimer > 500 && random.nextFloat() < 0.1f) {
					playSound(getEatingSound(heldStack), 1f, 1f);
					level.broadcastEntityEvent(this, EATING_STATE_ID);
				}
			}
		}

		super.aiStep();
		jumpMovementState.updateTick(this);
	}

	@Override
	public boolean canTakeItem(ItemStack stack) {
		if (!canPickUpLoot()) return false;
		EquipmentSlotType slotType = MobEntity.getEquipmentSlotForItem(stack);
		return slotType == EquipmentSlotType.MAINHAND && getItemBySlot(slotType).isEmpty();
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		Item item = stack.getItem();
		ItemStack heldStack = getItemBySlot(EquipmentSlotType.MAINHAND);
		return eatTimer > 0 && item.isEdible() && (heldStack.isEmpty() || !heldStack.getItem().isEdible());
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			onItemPickup(itemEntity);
			setItemSlot(EquipmentSlotType.MAINHAND, stack);
			handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2f;
			take(itemEntity, stack.getCount());
			itemEntity.remove();
			eatTimer = 0;
		}
	}

	@Override
	protected void dropAllDeathLoot(DamageSource damageSourceIn) {
		ItemStack stack = getItemBySlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty()) {
			spawnAtLocation(stack);
			setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
		}

		super.dropAllDeathLoot(damageSourceIn);
	}

	@Override
	public ResourceLocation getDefaultLootTable() {
		switch (getBlobSize()) {
			default:
			case 1:
				return getType().getDefaultLootTable();
			case 2:
				return LOOT_TABLE_FOR_SIZE_2;
			case 3:
				return LOOT_TABLE_FOR_SIZE_3;
			case 4:
				return LOOT_TABLE_FOR_SIZE_4;
			case 5:
				return LOOT_TABLE_FOR_SIZE_5;
			case 6:
				return LOOT_TABLE_FOR_SIZE_6;
			case 7:
				return LOOT_TABLE_FOR_SIZE_7;
			case 8:
				return LOOT_TABLE_FOR_SIZE_8;
			case 9:
				return LOOT_TABLE_FOR_SIZE_9;
			case 10:
				return LOOT_TABLE_FOR_SIZE_10;
		}
	}


	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == jumpMovementState.stateUpdateId) {
			spawnSprintParticle();
			jumpMovementState.onEntityStateUpdate();
		}
		else if (id == EATING_STATE_ID) {
			ItemStack stack = getItemBySlot(EquipmentSlotType.MAINHAND);
			if (!stack.isEmpty()) {
				float pitch = -xRot * ((float) Math.PI / 180f);
				float yaw = -yRot * ((float) Math.PI / 180f);
				for (int i = 0; i < 8; ++i) {
					Vector3d vector3d = new Vector3d((random.nextFloat() - 0.5d) * 0.1d, random.nextFloat() * 0.1d + 0.1d, 0d).xRot(pitch).yRot(yaw);
					level.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), getX() + getLookAngle().x / 2d, getY(), getZ() + getLookAngle().z / 2d, vector3d.x, vector3d.y + 0.05d, vector3d.z);
				}
			}
		}
		else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public boolean isJumping() {
		return jumping;
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			playSound(getJumpSound(), getSoundVolume(), ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	@Override
	public void setMovementController(GenericJumpMovementController controller) {
		moveControl = controller;
	}

	@Override
	public void setJumpController(GenericJumpController controller) {
		jumpControl = controller;
	}

	@Override
	public boolean canSpawnSprintParticle() {
		return false;
	}

	@Override
	public SoundEvent getJumpSound() {
		return SoundEvents.SLIME_JUMP;
	}

	@Override
	public SoundCategory getSoundSource() {
		return getFleshBlobData() == 1 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height * 0.5f;
	}

	static class CustomAttackGoal extends MeleeAttackGoal {

		public CustomAttackGoal(CreatureEntity creature, double speed) {
			super(creature, speed, true);
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 2f + attackTarget.getBbWidth();
		}

	}

	static class JumpPanicGoal extends PanicGoal {
		private final IJumpMovementMob<?> jumpMoveMob;

		public JumpPanicGoal(IJumpMovementMob<?> jumpMoveMobIn, double speedIn) {
			super((CreatureEntity) jumpMoveMobIn.getJumpingEntity(), speedIn);
			jumpMoveMob = jumpMoveMobIn;
		}

		public void tick() {
			super.tick();
			jumpMoveMob.getJumpMovementState().setMovementSpeed(jumpMoveMob, speedModifier);
		}
	}

	static class CustomAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
		private final FleshBlobEntity blobEntity;

		public CustomAvoidEntityGoal(FleshBlobEntity blobEntityIn, Class<T> clazz, float avoidDist, double farSpeed, double nearSpeed) {
			super(blobEntityIn, clazz, avoidDist, farSpeed, nearSpeed);
			blobEntity = blobEntityIn;
		}

		public boolean canUse() {
			return blobEntity.getFleshBlobData() != 1 && super.canUse();
		}
	}

	public static class FleshBlobData implements ILivingEntityData {
		public final byte flag;

		public FleshBlobData(byte flagIn) {
			flag = flagIn;
		}
	}
}
