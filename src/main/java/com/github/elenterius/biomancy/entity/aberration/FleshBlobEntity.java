package com.github.elenterius.biomancy.entity.aberration;

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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FleshBlobEntity extends CreatureEntity implements IJumpMovementMob<FleshBlobEntity> {

	public static final byte EATING_STATE_ID = 61; // any id > 55 should be future compatible. see handleStatusUpdate()
	public static final byte JUMPING_STATE_ID = 60;

	private static final DataParameter<Byte> BLOB_SIZE = EntityDataManager.createKey(FleshBlobEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> FLESH_BLOB_DATA = EntityDataManager.createKey(FleshBlobEntity.class, DataSerializers.BYTE);
	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity) && itemEntity.getItem().isFood();

	protected GenericJumpMovementHelper<FleshBlobEntity> jumpMovementState;
	private int eatTimer;

	public FleshBlobEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
		jumpMovementState = new GenericJumpMovementHelper<>(this, JUMPING_STATE_ID);
		setCanPickUpLoot(true);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 10d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3d)
				.createMutableAttribute(Attributes.ARMOR, 8d)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3d);
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
	protected void registerData() {
		super.registerData();
		dataManager.register(FLESH_BLOB_DATA, (byte) 0);
		dataManager.register(BLOB_SIZE, (byte) 1);
	}

	protected void setBlobSize(byte size, boolean resetHealth) {
		size = (byte) MathHelper.clamp(size, 1, 10);
		dataManager.set(BLOB_SIZE, size);
		recenterBoundingBox();
		recalculateSize();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(size * 10);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * size);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(size + (getFleshBlobData() == 1 ? 8 : 3));
		getAttribute(Attributes.ARMOR).setBaseValue(size * 3);
		if (resetHealth) setHealth(getMaxHealth());
		experienceValue = size;
	}

	public byte getBlobSize() {
		return dataManager.get(BLOB_SIZE);
	}

	@Override
	public EntitySize getSize(Pose poseIn) {
		return super.getSize(poseIn).scale(0.5f + getBlobSize() * 0.5f);
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		byte flag = (byte) (rand.nextFloat() < 0.45 ? 1 : 0);
		if (spawnDataIn instanceof FleshBlobData) {
			flag = ((FleshBlobData) spawnDataIn).flag;
		}
		else {
			spawnDataIn = new FleshBlobData(flag);
		}

		setCustomEntityData(flag);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public byte getFleshBlobData() {
		return dataManager.get(FLESH_BLOB_DATA);
	}

	public void setCustomEntityData(byte flag) {
		if (flag == 1) {
			//noinspection ConstantConditions
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getBlobSize() + 8d);
			goalSelector.addGoal(2, new CustomAttackGoal(this, 1.2d));
			targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, true));

		}
		dataManager.set(FLESH_BLOB_DATA, flag);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		setCustomEntityData(compound.getByte("FleshBlobData"));
		setBlobSize(compound.getByte("Size"), false);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("FleshBlobData", getFleshBlobData());
		compound.putByte("Size", getBlobSize());
	}

	@Override
	public void recalculateSize() {
		double x = getPosX();
		double y = getPosY();
		double z = getPosZ();
		super.recalculateSize();
		setPosition(x, y, z);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (BLOB_SIZE.equals(key)) {
			recalculateSize();
			rotationYaw = rotationYawHead;
			renderYawOffset = rotationYawHead;
			if (isInWater() && rand.nextFloat() < 0.05f) {
				doWaterSplashEffect();
			}
		}
		super.notifyDataManagerChange(key);
	}

	@Override
	protected void jump() {
		super.jump();
		jumpMovementState.updateJump(this);
	}

	@Override
	protected void updateAITasks() {
		jumpMovementState.updateAIMovement(this);
	}

	@Override
	public void updateRotationYaw(double x, double z) {
		rotationYaw = (float) (MathHelper.atan2(z - getPosZ(), x - getPosX()) * (double) (180F / (float) Math.PI)) - 90.0F;
	}

	@Override
	public GenericJumpMovementHelper<? extends IJumpMovementMob<FleshBlobEntity>> getJumpMovementState() {
		return jumpMovementState;
	}

	@Override
	public void livingTick() {
		if (!world.isRemote && isAlive() && isServerWorld()) {
			eatTimer++;
			ItemStack heldStack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
			if (heldStack.getItem().isFood() && getAttackTarget() == null) {
				if (eatTimer > 600) {
					Food food = heldStack.getItem().getFood();
					ItemStack eatenStack = heldStack.onItemUseFinish(world, this);
					if (!eatenStack.isEmpty()) {
						setItemStackToSlot(EquipmentSlotType.MAINHAND, eatenStack);
					}
					float modifier = food != null ? (food.getHealing() * (food.isMeat() ? 0.5f : 0.25f)) / 10f : 0f;
					if (rand.nextFloat() < 0.4f + modifier) {
						byte blobSize = getBlobSize();
						if (blobSize < 10) setBlobSize((byte) (blobSize + 1), true);
						else heal(getMaxHealth() - getHealth()); //if the blob is already at max size, heal up to max health instead
					}
					eatTimer = 0;
				}
				else if (eatTimer > 500 && rand.nextFloat() < 0.1f) {
					playSound(getEatSound(heldStack), 1f, 1f);
					world.setEntityState(this, EATING_STATE_ID);
				}
			}
		}

		super.livingTick();
		jumpMovementState.updateTick(this);
	}

	@Override
	public boolean canPickUpItem(ItemStack stack) {
		if (!canPickUpLoot()) return false;
		EquipmentSlotType slotType = MobEntity.getSlotForItemStack(stack);
		return slotType == EquipmentSlotType.MAINHAND && getItemStackFromSlot(slotType).isEmpty();
	}

	@Override
	public boolean canEquipItem(ItemStack stack) {
		Item item = stack.getItem();
		ItemStack heldStack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		return eatTimer > 0 && item.isFood() && (heldStack.isEmpty() || !heldStack.getItem().isFood());
	}

	@Override
	protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canEquipItem(stack)) {
			triggerItemPickupTrigger(itemEntity);
			setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
			inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2f;
			onItemPickup(itemEntity, stack.getCount());
			itemEntity.remove();
			eatTimer = 0;
		}
	}

	@Override
	protected void spawnDrops(DamageSource damageSourceIn) {
		ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!stack.isEmpty()) {
			entityDropItem(stack);
			setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
		}

		super.spawnDrops(damageSourceIn);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == jumpMovementState.stateUpdateId) {
			handleRunningEffect();
			jumpMovementState.onEntityStateUpdate();
		}
		else if (id == EATING_STATE_ID) {
			ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
			if (!stack.isEmpty()) {
				float pitch = -rotationPitch * ((float) Math.PI / 180f);
				float yaw = -rotationYaw * ((float) Math.PI / 180f);
				for (int i = 0; i < 8; ++i) {
					Vector3d vector3d = new Vector3d((rand.nextFloat() - 0.5d) * 0.1d, rand.nextFloat() * 0.1d + 0.1d, 0d).rotatePitch(pitch).rotateYaw(yaw);
					world.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), getPosX() + getLookVec().x / 2d, getPosY(), getPosZ() + getLookVec().z / 2d, vector3d.x, vector3d.y + 0.05d, vector3d.z);
				}
			}
		}
		else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public boolean isJumping() {
		return isJumping;
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			playSound(getJumpSound(), getSoundVolume(), ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	@Override
	public void setMovementController(GenericJumpMovementController controller) {
		moveController = controller;
	}

	@Override
	public void setJumpController(GenericJumpController controller) {
		jumpController = controller;
	}

	@Override
	public boolean shouldSpawnRunningEffects() {
		return false;
	}

	@Override
	public SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_SLIME_JUMP;
	}

	@Override
	public SoundCategory getSoundCategory() {
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
			return 2f + attackTarget.getWidth();
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
			jumpMoveMob.getJumpMovementState().setMovementSpeed(jumpMoveMob, speed);
		}
	}

	static class CustomAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
		private final FleshBlobEntity blobEntity;

		public CustomAvoidEntityGoal(FleshBlobEntity blobEntityIn, Class<T> clazz, float avoidDist, double farSpeed, double nearSpeed) {
			super(blobEntityIn, clazz, avoidDist, farSpeed, nearSpeed);
			blobEntity = blobEntityIn;
		}

		public boolean shouldExecute() {
			return blobEntity.getFleshBlobData() != 1 && super.shouldExecute();
		}
	}

	public static class FleshBlobData implements ILivingEntityData {
		public final byte flag;

		public FleshBlobData(byte flagIn) {
			flag = flagIn;
		}
	}
}
