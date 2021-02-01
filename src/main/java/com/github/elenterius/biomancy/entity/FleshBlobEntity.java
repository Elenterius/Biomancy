package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpController;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpMovementController;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class FleshBlobEntity extends CreatureEntity implements IJumpMovementMob<FleshBlobEntity> {

	private static final DataParameter<Byte> FLESH_BLOB_DATA = EntityDataManager.createKey(FleshBlobEntity.class, DataSerializers.BYTE);
	GenericJumpMovementHelper<FleshBlobEntity> jumpMovementState;

	public FleshBlobEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
		super(type, worldIn);
		jumpMovementState = new GenericJumpMovementHelper<>(this, (byte) 1);
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
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(FLESH_BLOB_DATA, (byte) 0);
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		byte flag = (byte) (rand.nextFloat() < 0.3 ? 13 : 0);
		if (spawnDataIn instanceof FleshBlobData) {
			flag = ((FleshBlobData) spawnDataIn).flag;
		}
		else {
			spawnDataIn = new FleshBlobData(flag);
		}

		setFleshBlobData(flag);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public byte getFleshBlobData() {
		return dataManager.get(FLESH_BLOB_DATA);
	}

	public void setFleshBlobData(byte flag) {
		if (flag == 13) {
			//noinspection ConstantConditions
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8d);
			goalSelector.addGoal(2, new CustomAttackGoal(this, 1.2d));
			targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, true));
		}

		dataManager.set(FLESH_BLOB_DATA, flag);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		setFleshBlobData(compound.getByte("FleshBlobData"));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("FleshBlobData", getFleshBlobData());
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
		super.livingTick();
		jumpMovementState.updateTick(this);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == jumpMovementState.stateUpdateId) {
			handleRunningEffect();
			jumpMovementState.onEntityStateUpdate();
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
		return getFleshBlobData() == 13 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
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

		public JumpPanicGoal(IJumpMovementMob<?> jumpMoveMob, double speedIn) {
			super((CreatureEntity) jumpMoveMob.getJumpingEntity(), speedIn);
			this.jumpMoveMob = jumpMoveMob;
		}

		public void tick() {
			super.tick();
			jumpMoveMob.getJumpMovementState().setMovementSpeed(jumpMoveMob, speed);
		}
	}

	static class CustomAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
		private final FleshBlobEntity blobEntity;

		public CustomAvoidEntityGoal(FleshBlobEntity blobEntity, Class<T> clazz, float avoidDist, double farSpeed, double nearSpeed) {
			super(blobEntity, clazz, avoidDist, farSpeed, nearSpeed);
			this.blobEntity = blobEntity;
		}

		public boolean shouldExecute() {
			return blobEntity.getFleshBlobData() != 13 && super.shouldExecute();
		}
	}

	public static class FleshBlobData implements ILivingEntityData {
		public final byte flag;

		public FleshBlobData(byte flag) {
			this.flag = flag;
		}
	}
}
