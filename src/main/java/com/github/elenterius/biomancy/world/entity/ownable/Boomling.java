package com.github.elenterius.biomancy.world.entity.ownable;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Boomling extends OwnableMob implements IAnimatable {

	private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(Boomling.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Boomling.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Boomling.class, EntityDataSerializers.INT);

	private static final double MAX_TRIGGER_DIST_SQ = 3.5d * 3.5d;

	private int prevFuseTimer;
	private int fuseTimer;
	private short maxFuseTimer = 22;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public Boomling(EntityType<? extends Boomling> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 7.5d)
				.add(Attributes.MAX_HEALTH, 6.5d)
				.add(Attributes.MOVEMENT_SPEED, 0.3d)
				.add(Attributes.ATTACK_DAMAGE, 1d);
	}

	public static void spawnEffectAOE(Level world, @Nullable LivingEntity attacker, Vec3 pos, Potion potion, Collection<MobEffectInstance> effects, int color) {
		AreaEffectCloud aoeCloud = new AreaEffectCloud(world, pos.x, pos.y, pos.z);
		aoeCloud.setOwner(attacker);
		aoeCloud.setRadius(3f);
		aoeCloud.setRadiusOnUse(-0.5f);
		aoeCloud.setWaitTime(10);
		aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / aoeCloud.getDuration());
		aoeCloud.setFixedColor(color);
		aoeCloud.setPotion(potion);
		for (MobEffectInstance effect : effects) aoeCloud.addEffect(new MobEffectInstance(effect));

		world.addFreshEntity(aoeCloud);

		int event = potion.hasInstantEffects() ? LevelEvent.PARTICLES_SPELL_POTION_SPLASH : LevelEvent.PARTICLES_INSTANT_POTION_SPLASH;
		world.levelEvent(event, new BlockPos(pos), color);
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new ExplodeGoal());
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6f, 1d, 1.2d));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6f, 1d, 1.2d));
		goalSelector.addGoal(4, new MeleeAttackGoal(this, 1d, false));
		goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.4f));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false,
				target -> getOwnerAsPlayer().map(owner -> shouldAttackEntity(target, owner)).orElse(true)));
		targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(CLIMBING, (byte) 0);
		entityData.define(COLOR, 0);
		entityData.define(STATE, (byte) Flags.setFlag(0, Flags.IDLE));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putShort("MaxFuseTime", maxFuseTimer);
		nbt.putBoolean("Ignited", isIgnited());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("MaxFuseTime", Tag.TAG_ANY_NUMERIC)) {
			maxFuseTimer = nbt.getShort("MaxFuseTime");
		}
		setIgnited(nbt.getBoolean("Ignited"));
		ItemStack storedPotion = getStoredPotion();
		if (!storedPotion.isEmpty()) {
			getEntityData().set(COLOR, PotionUtilExt.getColor(storedPotion)); //restore color
		}
	}

	@Override
	public PathNavigation getNavigation() {
		return new WallClimberNavigation(this, level);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
		if (getStoredPotion().isEmpty()) {
			Set<ResourceLocation> keys = ForgeRegistries.POTIONS.getKeys();
			int n = level.getRandom().nextInt(keys.size());
			for (ResourceLocation key : keys) {
				if (--n <= 0) {
					Potion potion = ForgeRegistries.POTIONS.getValue(key);
					if (potion != null && potion != Potions.EMPTY) {
						setStoredPotion(PotionUtilExt.setPotion(new ItemStack(Items.POTION), potion));
						break;
					}
				}
			}
		}
		return data;
	}

	@Override
	public void tick() {
		if (isAlive()) {
			prevFuseTimer = fuseTimer;
			if (isIgnited()) {
				setIdle(false);
			}

			if (!isIdle()) {
				if (fuseTimer == 0) playSound(SoundEvents.CREEPER_PRIMED, 1f, 0.5f);
				if (++fuseTimer >= maxFuseTimer) {
					fuseTimer = maxFuseTimer;
					explode();
				}
			}
			else {
				if (--fuseTimer < 0) fuseTimer = 0;
			}
		}

		super.tick();
		if (!level.isClientSide) setBesideClimbableBlock(horizontalCollision);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		//disables melee attack
		return true;
	}

	public boolean isBesideClimbableBlock() {
		return (entityData.get(CLIMBING) & 0x1) != 0;
	}

	public void setBesideClimbableBlock(boolean isClimbing) {
		byte flag = entityData.get(CLIMBING);
		flag = isClimbing ? (byte) (flag | 0x1) : (byte) (flag & 0xfffffffe);
		entityData.set(CLIMBING, flag);
	}

	@Override
	public boolean onClimbable() {
		return isBesideClimbableBlock();
	}

	@Override
	public void makeStuckInBlock(BlockState state, Vec3 motionMultiplierIn) {
		if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, motionMultiplierIn);
	}

	public ItemStack getStoredPotion() {
		return getItemBySlot(EquipmentSlot.MAINHAND);
	}

	public void setStoredPotion(ItemStack stack) {
		setItemSlot(EquipmentSlot.MAINHAND, stack);
		getEntityData().set(COLOR, stack.isEmpty() ? 0 : PotionUtilExt.getColor(stack));
	}

	public byte getState() {
		return getEntityData().get(STATE);
	}

	public boolean isIdle() {
		return Flags.isFlagSet(getEntityData().get(STATE), Flags.IDLE);
	}

	public void setIdle(boolean bool) {
		setStateFlag(Flags.IDLE, bool);
	}

	public boolean isIgnited() {
		return Flags.isFlagSet(getEntityData().get(STATE), Flags.IGNITED);
	}

	public void setIgnited(boolean bool) {
		setStateFlag(Flags.IGNITED, bool);
	}

	public void setStateFlag(Flags flag, boolean bool) {
		byte value = getEntityData().get(STATE);
		byte flags = (byte) (bool ? Flags.setFlag(value, flag) : Flags.unsetFlag(value, flag));
		getEntityData().set(STATE, flags);
	}

	public short getMaxFuseTimer() {
		return maxFuseTimer;
	}

	public void setMaxFuseTimer(short maxFuseTimer) {
		this.maxFuseTimer = maxFuseTimer;
	}

	public int getColor() {
		return getEntityData().get(COLOR);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effectInstance) {
		return false;
	}

	@Override
	public void die(@Nonnull DamageSource cause) {
		super.die(cause);
		if (!level.isClientSide && !cause.isMagic() && !cause.isFire() && !cause.isExplosion()) {
			explode();
		}
	}

	private void explode() {
		if (level.isClientSide) return;

		ItemStack stack = getStoredPotion();
		if (stack.isEmpty()) return;

		Potion potion = PotionUtilExt.getPotion(stack);
		List<MobEffectInstance> effects = PotionUtilExt.getMobEffects(stack);
		if (potion == Potions.WATER && effects.isEmpty()) {
			causeWaterAOE();
			discard();
		}
		else if (!effects.isEmpty()) {
			spawnEffectAOE(stack, potion);
			discard();
		}
	}

	private void spawnEffectAOE(ItemStack stack, Potion potion) {
		Optional<Player> owner = getOwnerAsPlayer();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		CompoundTag nbt = stack.getTag();
		int color = PotionUtilExt.getColor(stack);
		if (nbt != null && nbt.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)) {
			color = nbt.getInt("CustomPotionColor");
		}
		List<MobEffectInstance> effects = PotionUtilExt.getCustomEffects(stack);
		spawnEffectAOE(level, shooter, position(), potion, effects, color);
	}

	private void causeWaterAOE() {
		Optional<Player> owner = getOwnerAsPlayer();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		MobUtil.performWaterAOE(level, shooter, 4d);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (!isOwner(player)) return InteractionResult.PASS;

		if (!player.level.isClientSide() && player.isShiftKeyDown()) {
			removeAfterChangingDimensions();
			ItemStack stack = PotionUtilExt.setPotionOfHost(new ItemStack(ModItems.BOOMLING.get()), getStoredPotion().copy());
			if (hasCustomName()) stack.setHoverName(getCustomName());
			if (!player.addItem(stack)) {
				spawnAtLocation(stack);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	protected float getStandingEyeHeight(@Nonnull Pose pose, @Nonnull EntityDimensions dim) {
		return 0.16f;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4f;
	}

	@Override
	public void playAmbientSound() {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getVoicePitch() * 1.2f);
		}
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		ambientSoundTime = -getAmbientSoundInterval();
		SoundEvent soundevent = getHurtSound(source);
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getVoicePitch() * 1.2f);
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		playSound(SoundEvents.SPIDER_STEP, 0.1f, 1.2f);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SPIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.SLIME_HURT_SMALL;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SLIME_DEATH_SMALL;
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		return false;
	}

	public float getExplodeProgress(float partialTicks) {
		return Mth.lerp(partialTicks, prevFuseTimer, fuseTimer) / (maxFuseTimer - 2f);
	}

	private <E extends IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;

		if (!isIdle() && getExplodeProgress(event.getPartialTick()) > 0) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("boomling.anim.explode"));
			return PlayState.CONTINUE;
		}

		if (event.isMoving()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("boomling.anim.armed"));
			return PlayState.CONTINUE;
		}

		event.getController().transitionLengthTicks = 5;
		event.getController().setAnimation(new AnimationBuilder().addAnimation("boomling.anim.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	public enum Flags {
		IGNITED, IDLE;

		private final int bitPosition = 1 << ordinal();

		public static boolean isFlagSet(int value, Flags flag) {
			return (value & flag.bitPosition) != 0;
		}

		public static int setFlag(int value, Flags flag) {
			return value | flag.bitPosition;
		}

		public static int unsetFlag(int value, Flags flag) {
			return value & ~flag.bitPosition;
		}

	}

	class ExplodeGoal extends Goal {

		@Override
		public boolean canUse() {
			return getTarget() != null && distanceToSqr(getTarget()) < MAX_TRIGGER_DIST_SQ;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity target = getTarget();
			if (target == null) {
				setIdle(true);
			}
			else {
				if (distanceToSqr(target) > MAX_TRIGGER_DIST_SQ) {
					setIdle(true);
				}
				else {
					setIdle(!getSensing().hasLineOfSight(target));
				}
			}
		}
	}

}
