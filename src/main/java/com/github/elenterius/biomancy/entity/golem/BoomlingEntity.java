package com.github.elenterius.biomancy.entity.golem;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BoomlingEntity extends OwnableCreatureEntity {

	private static final DataParameter<Byte> CLIMBING = EntityDataManager.defineId(BoomlingEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE = EntityDataManager.defineId(BoomlingEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BoomlingEntity.class, DataSerializers.INT);
	private int prevFuseTimer;
	private int fuseTimer;
	private short maxFuseTimer = 22;

	public BoomlingEntity(EntityType<? extends BoomlingEntity> entityType, World world) {
		super(entityType, world);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 6.5d)
				.add(Attributes.MOVEMENT_SPEED, 0.3d)
				.add(Attributes.ATTACK_DAMAGE, 1d);
	}

	public static void spawnEffectAOE(World world, @Nullable LivingEntity attacker, Vector3d pos, Potion potion, Collection<EffectInstance> effects, int color) {
		AreaEffectCloudEntity aoeCloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
		aoeCloud.setOwner(attacker);
		aoeCloud.setRadius(3f);
		aoeCloud.setRadiusOnUse(-0.5f);
		aoeCloud.setWaitTime(10);
		aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / aoeCloud.getDuration());
		aoeCloud.setFixedColor(color);
		aoeCloud.setPotion(potion);
		for (EffectInstance effect : effects) aoeCloud.addEffect(new EffectInstance(effect));

		world.addFreshEntity(aoeCloud);

		int event = potion.hasInstantEffects() ? Constants.WorldEvents.POTION_IMPACT : Constants.WorldEvents.POTION_IMPACT_INSTANT;
		world.levelEvent(event, new BlockPos(pos), color);
	}

	public static void causeWaterAOE(World world, Entity attacker) {
		AxisAlignedBB axisalignedbb = attacker.getBoundingBox().inflate(4d, 2d, 4d);
		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, axisalignedbb, PotionEntity.WATER_SENSITIVE);
		if (!entities.isEmpty()) {
			for (LivingEntity victim : entities) {
				if (attacker.distanceToSqr(victim) < 16d) {
					victim.hurt(DamageSource.indirectMagic(victim, attacker), 1f);
				}
			}
		}
	}

	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new ExplodeGoal());
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, OcelotEntity.class, 6f, 1d, 1.2d));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, CatEntity.class, 6f, 1d, 1.2d));
		goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4f));
		goalSelector.addGoal(5, new MeleeAttackGoal(this, 1f, true));
		goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.8f));
		goalSelector.addGoal(7, new LookRandomlyGoal(this));
		goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8f));

		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, -1, true, false,
				target -> getOwner().map(owner -> shouldAttackEntity(target, owner)).orElse(true)));
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
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putShort("MaxFuseTime", maxFuseTimer);
		nbt.putBoolean("Ignited", isIgnited());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("MaxFuseTime", Constants.NBT.TAG_ANY_NUMERIC)) {
			maxFuseTimer = nbt.getShort("MaxFuseTime");
		}
		setIgnited(nbt.getBoolean("Ignited"));
	}

	@Override
	protected PathNavigator createNavigation(World worldIn) {
		return new ClimberPathNavigator(this, worldIn);
	}

	@Nullable
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		if (getStoredPotion().isEmpty()) {
			Set<ResourceLocation> keys = ForgeRegistries.POTION_TYPES.getKeys();
			int n = worldIn.getRandom().nextInt(keys.size());
			for (ResourceLocation key : keys) {
				if (--n <= 0) {
					Potion potion = ForgeRegistries.POTION_TYPES.getValue(key);
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
	public boolean doHurtTarget(Entity entityIn) {
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
	public void makeStuckInBlock(BlockState state, Vector3d motionMultiplierIn) {
		if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, motionMultiplierIn);
	}

	public ItemStack getStoredPotion() {
		return getItemBySlot(EquipmentSlotType.MAINHAND);
	}

	public void setStoredPotion(ItemStack stack) {
		setItemSlot(EquipmentSlotType.MAINHAND, stack);
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
	public boolean canBeAffected(EffectInstance effectInstance) {
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
		List<EffectInstance> effects = PotionUtilExt.getMobEffects(stack);
		if (potion == Potions.WATER && effects.isEmpty()) {
			causeWaterAOE();
			remove();
		}
		else if (!effects.isEmpty()) {
			spawnEffectAOE(stack, potion);
			remove();
		}
	}

	private void spawnEffectAOE(ItemStack stack, Potion potion) {
		Optional<PlayerEntity> owner = getOwner();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		CompoundNBT nbt = stack.getTag();
		int color = PotionUtilExt.getColor(stack);
		if (nbt != null && nbt.contains("CustomPotionColor", Constants.NBT.TAG_ANY_NUMERIC)) {
			color = nbt.getInt("CustomPotionColor");
		}
		List<EffectInstance> effects = PotionUtilExt.getCustomEffects(stack);
		spawnEffectAOE(level, shooter, position(), potion, effects, color);
	}

	private void causeWaterAOE() {
		Optional<PlayerEntity> owner = getOwner();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		causeWaterAOE(level, shooter);
	}

	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		if (!isOwner(player)) return ActionResultType.PASS;

		if (!player.level.isClientSide() && player.isShiftKeyDown()) {
			removeAfterChangingDimensions();
			ItemStack stack = PotionUtilExt.setPotionOfHost(new ItemStack(ModItems.BOOMLING.get()), getStoredPotion().copy());
			if (hasCustomName()) stack.setHoverName(getCustomName());
			if (!player.addItem(stack)) {
				spawnAtLocation(stack);
			}
		}
		return ActionResultType.sidedSuccess(level.isClientSide());
	}

	@Override
	protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
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

	@OnlyIn(Dist.CLIENT)
	public float getFuseFlashIntensity(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevFuseTimer, fuseTimer) / (maxFuseTimer - 2f);
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

		public ExplodeGoal() {
			setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return !isIdle() || getTarget() != null && distanceToSqr(getTarget()) < 9d;
		}

		@Override
		public void start() {
			navigation.stop();
		}

		@Override
		public void tick() {
			if (getTarget() == null || distanceToSqr(getTarget()) > 49d) {
				setIdle(true);
			}
			else {
				setIdle(!getSensing().canSee(getTarget()));
			}
		}
	}

}
