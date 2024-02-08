package com.github.elenterius.biomancy.entity.mob.fleshblob;

import com.github.elenterius.biomancy.entity.mob.Fleshkin;
import com.github.elenterius.biomancy.entity.mob.JukeboxDancer;
import com.github.elenterius.biomancy.entity.mob.JumpMoveHelper;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BiConsumer;

public abstract class FleshBlob extends PathfinderMob implements Fleshkin, JumpMoveHelper.JumpingPathfinderMob, JukeboxDancer, GeoEntity {

	public static final byte MAX_SIZE = 10;
	public static final byte MIN_SIZE = 1;
	public static final byte JUMPING_STATE_ID = 61;
	protected static final RawAnimation ON_GROUND_ANIMATION = RawAnimation.begin().thenLoop("ground.loop");
	protected static final RawAnimation JUMP_START_ANIMATION = RawAnimation.begin().thenPlay("jump.startup").thenPlayAndHold("jump.air.loop");
	protected static final RawAnimation JUMP_IN_AIR_ANIMATION = RawAnimation.begin().thenLoop("jump.air.loop");
	protected static final RawAnimation JUMP_LAND_ANIMATION = RawAnimation.begin().thenPlay("jump.impact");
	protected static final RawAnimation EATING_ANIMATION = RawAnimation.begin().thenLoop("eating.loop");
	protected static final RawAnimation DANCE_ANIMATION = RawAnimation.begin().thenPlay("dancing.loop");
	protected static final EntityDataAccessor<Byte> BLOB_SIZE = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Byte> TUMORS = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Boolean> IS_DANCING = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BOOLEAN);

	protected final JumpMoveHelper<FleshBlob> jumpMoveHelper = new JumpMoveHelper<>(this, JUMPING_STATE_ID);
	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final DynamicGameEventListener<JukeboxListener> dynamicJukeboxListener;
	protected int pettingDelay = 0;
	private @Nullable BlockPos jukeboxPos;

	protected FleshBlob(EntityType<? extends FleshBlob> entityType, Level level) {
		super(entityType, level);
		dynamicJukeboxListener = new DynamicGameEventListener<>(new JukeboxListener(new EntityPositionSource(this, getEyeHeight()), GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		if (spawnData instanceof FleshBlobSpawnData data) {
			setTumorFlags(data.tumorFlags());
		}
		else {
			final byte tumorFlags = TumorFlag.randomFlags(random);
			setTumorFlags(tumorFlags);
			spawnData = new FleshBlobSpawnData.Tumors(tumorFlags);
		}

		setBlobSize((byte) 1, true); //refreshes mob dimensions, etc. This also makes sure the path navigator uses the correct bounding box size

		if (reason == MobSpawnType.SPAWNER) {
			xpReward = 0;
		}

		return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(BLOB_SIZE, (byte) 1);
		entityData.define(TUMORS, (byte) 0);
		entityData.define(IS_DANCING, false);
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

	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> listener) {
		if (level() instanceof ServerLevel serverlevel) {
			listener.accept(dynamicJukeboxListener, serverlevel);
		}
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(getBlobScale());
	}

	public float getBlobScale() {
		return 0.5f + getBlobSize() * 0.25f;
	}

	@Override
	public int getMaxHeadXRot() {
		return 0;
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
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return size.height * 0.5f;
	}

	public static byte clamp(byte v, byte min, byte max) {
		return (byte) Math.min(Math.max(v, min), max);
	}

	public void setBlobSize(byte size, boolean resetHealth) {
		size = clamp(size, MIN_SIZE, MAX_SIZE);
		entityData.set(BLOB_SIZE, size);

		reapplyPosition();
		refreshDimensions();

		updateBaseAttributes(size);

		if (resetHealth) setHealth(getMaxHealth());

		xpReward = size;
	}

	protected abstract void updateBaseAttributes(byte size);

	public byte getBlobSize() {
		return entityData.get(BLOB_SIZE);
	}

	public void randomizeTumors() {
		entityData.set(TUMORS, (byte) random.nextInt(Byte.MAX_VALUE + 1));
	}

	public void setTumors(float tumorFactor) {
		int flags = 0;
		if (tumorFactor > 0) {
			for (TumorFlag flag : TumorFlag.values()) {
				if (level().random.nextFloat() < tumorFactor) flags = TumorFlag.setFlag(flags, flag);
			}
		}
		entityData.set(TUMORS, (byte) flags);
	}

	public byte getTumorFlags() {
		return entityData.get(TUMORS);
	}

	public void setTumorFlags(byte flags) {
		entityData.set(TUMORS, flags);
	}

	public boolean isDancing() {
		return entityData.get(IS_DANCING);
	}

	public void setDancing(boolean dancing) {
		if (!level().isClientSide) entityData.set(IS_DANCING, dancing);
	}

	@Nullable
	@Override
	public BlockPos getJukeboxPos() {
		return jukeboxPos;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putByte("Size", getBlobSize());
		tag.putByte("Tumors", getTumorFlags());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setBlobSize(tag.getByte("Size"), false);
		setTumorFlags(tag.getByte("Tumors"));
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

	@Override
	protected void jumpFromGround() {
		super.jumpFromGround();
		jumpMoveHelper.onJumpFromGround();
	}

	@Override
	public void aiStep() {
		super.aiStep();
		jumpMoveHelper.onAiStep();

		if (tickCount % 20 == 0 && isDancing() && shouldStopDancing()) {
			setDancing(false);
			jukeboxPos = null;
		}

		if (pettingDelay > 0) {
			pettingDelay--;
		}
	}

	protected boolean shouldStopDancing() {
		boolean wasHurt = lastHurtByPlayer != null || getLastHurtByMob() != null;
		if (wasHurt) return true;

		return jukeboxPos == null || !jukeboxPos.closerToCenterThan(position(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !level().getBlockState(jukeboxPos).is(Blocks.JUKEBOX);
	}

	@Override
	protected void customServerAiStep() {
		jumpMoveHelper.onCustomServerAiStep();
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (jumpMoveHelper.handleEntityEvent(id)) return;
		super.handleEntityEvent(id);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.isEmpty()) {
			if (!level().isClientSide && pettingDelay <= 0) {
				pettingDelay = random.nextIntBetweenInclusive(10, 20);

				if (this instanceof Enemy) {
					playSound(ModSoundEvents.FLESH_BLOB_GROWL.get(), getSoundVolume(), getVoicePitch());
					double offset = getBbWidth() * 0.5d;
					((ServerLevel) level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, getX(), getY(1d) - 0.2d, getZ(), 3, offset, 0.1d, offset, 1);
				}
				else {
					playSound(ModSoundEvents.FLESH_BLOB_MEW_PURR.get(), getSoundVolume(), getVoicePitch());
					double offset = getBbWidth() * 0.5d;
					((ServerLevel) level()).sendParticles(ParticleTypes.HEART, getX(), getY(1d) - 0.2d, getZ(), 3, offset, 0.1d, offset, 1);
				}
			}

			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public float getVoicePitch() {
		return (random.nextFloat() - random.nextFloat()) * 0.4f + 0.8f;
	}

	@Override
	public void spawnJumpParticle() {
		spawnSprintParticle();
	}

	@Override
	public boolean isJumping() {
		return jumping;
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping && !isInWaterOrBubble()) {
			playSound(getJumpSound(), getSoundVolume(), ((float) random.nextGaussian() * 0.2f + 1f) * 0.8f);
		}
	}

	@Override
	public SoundEvent getJumpSound() {
		return ModSoundEvents.FLESH_BLOB_JUMP.get();
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModSoundEvents.FLESH_BLOB_HURT.get();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.FLESH_BLOB_DEATH.get();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return ModSoundEvents.FLESH_BLOB_AMBIENT.get();
	}

	@Override
	public boolean canSpawnSprintParticle() {
		return false;
	}

	public void setJukeboxPlaying(BlockPos pos, boolean isJukeboxPlaying) {
		if (isJukeboxPlaying) {
			boolean wasHurt = lastHurtByPlayer != null || getLastHurtByMob() != null;
			if (!isDancing() && !wasHurt) {
				jukeboxPos = pos;
				setDancing(true);
			}
		}
		else if (pos.equals(jukeboxPos) || jukeboxPos == null) {
			jukeboxPos = null;
			setDancing(false);
		}
	}

	protected <T extends FleshBlob> PlayState handleJumpAnimation(AnimationState<T> event) {
		float jumpPct = jumpMoveHelper.getJumpCompletionPct(event.getPartialTick());
		if (jumpPct > 0) {
			event.getController().transitionLength(0);
			if (jumpPct <= 0.28f) {
				event.getController().setAnimation(JUMP_START_ANIMATION);
			}
			else if (jumpPct < 0.72f) {
				event.getController().setAnimation(JUMP_IN_AIR_ANIMATION);
			}
			else {
				event.getController().setAnimation(JUMP_LAND_ANIMATION);
			}
		}
		else {
			event.getController().transitionLength(10);
			event.getController().setAnimation(ON_GROUND_ANIMATION);
		}
		return PlayState.CONTINUE;
	}

	protected <T extends FleshBlob> PlayState handleDanceAnimation(AnimationState<T> state) {
		if (isDancing()) {
			state.getController().setAnimation(DANCE_ANIMATION);
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "jump", 0, this::handleJumpAnimation));
		controllers.add(new AnimationController<>(this, "dance", 10, this::handleDanceAnimation));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected class JukeboxListener implements GameEventListener {
		private final PositionSource listenerSource;
		private final int listenerRadius;

		public JukeboxListener(PositionSource positionSource, int radius) {
			listenerSource = positionSource;
			listenerRadius = radius;
		}

		@Override
		public PositionSource getListenerSource() {
			return listenerSource;
		}

		@Override
		public int getListenerRadius() {
			return listenerRadius;
		}

		@Override
		public boolean handleGameEvent(ServerLevel level, GameEvent gameEvent, GameEvent.Context context, Vec3 pos) {
			if (gameEvent == GameEvent.JUKEBOX_PLAY) {
				setJukeboxPlaying(BlockPos.containing(pos), true);
				return true;
			}
			else if (gameEvent == GameEvent.JUKEBOX_STOP_PLAY) {
				setJukeboxPlaying(BlockPos.containing(pos), false);
				return true;
			}

			return false;
		}
	}
}
