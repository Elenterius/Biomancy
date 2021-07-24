package com.github.elenterius.biomancy.entity.golem;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
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
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BoomlingEntity extends OwnableCreatureEntity {

	private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(BoomlingEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(BoomlingEntity.class, DataSerializers.VARINT);

	public BoomlingEntity(EntityType<? extends BoomlingEntity> entityType, World world) {
		super(entityType, world);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 6.5d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3d)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 1d);
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4f));
		goalSelector.addGoal(4, new MeleeAttackGoal(this, 1f, true));
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8f));

		goalSelector.addGoal(6, new LookRandomlyGoal(this));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8f));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (target) ->
				getOwner().map(owner -> shouldAttackEntity(target, owner)).orElse(true)));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (target) -> {
			if (target instanceof IMob) {
				if (target instanceof IOwnableCreature) {
					Optional<PlayerEntity> owner = getOwner();
					if (owner.isPresent()) {
						return shouldAttackEntity(target, owner.get());
					}
				}
				return true;
			}
			return false;
		}));
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(CLIMBING, (byte) 0);
		dataManager.register(COLOR, 0);
	}

	@Override
	protected PathNavigator createNavigator(World worldIn) {
		return new ClimberPathNavigator(this, worldIn);
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		if (getStoredPotion().isEmpty()) {
			Set<ResourceLocation> keys = ForgeRegistries.POTION_TYPES.getKeys();
			int n = worldIn.getRandom().nextInt(keys.size());
			for (ResourceLocation key : keys) {
				if (--n <= 0) {
					Potion potion = ForgeRegistries.POTION_TYPES.getValue(key);
					if (potion != null && potion != Potions.EMPTY) {
						setStoredPotion(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion));
						break;
					}
				}
			}
		}
		return data;
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isRemote) setBesideClimbableBlock(collidedHorizontally);
	}

	public boolean isBesideClimbableBlock() {
		return (dataManager.get(CLIMBING) & 0x1) != 0;
	}

	public void setBesideClimbableBlock(boolean isClimbing) {
		byte flag = dataManager.get(CLIMBING);
		flag = isClimbing ? (byte) (flag | 0x1) : (byte) (flag & 0xfffffffe);
		dataManager.set(CLIMBING, flag);
	}

	@Override
	public boolean isOnLadder() {
		return isBesideClimbableBlock();
	}

	@Override
	public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn) {
		if (!state.matchesBlock(Blocks.COBWEB)) super.setMotionMultiplier(state, motionMultiplierIn);
	}

	public ItemStack getStoredPotion() {
		return getItemStackFromSlot(EquipmentSlotType.MAINHAND);
	}

	public void setStoredPotion(ItemStack stack) {
		setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
		getDataManager().set(COLOR, stack.isEmpty() ? 0 : PotionUtils.getColor(stack));
	}

	public int getColor() {
		return getDataManager().get(COLOR);
	}

	@Override
	public boolean isPotionApplicable(EffectInstance effectInstance) {
		return false;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		if (!world.isRemote && !cause.isMagicDamage() && !cause.isFireDamage() && !cause.isExplosion()) {
			ItemStack stack = getStoredPotion();
			if (stack.isEmpty()) return;

			Potion potion = PotionUtils.getPotionFromItem(stack);
			List<EffectInstance> effects = PotionUtils.getEffectsFromStack(stack);
			if (potion == Potions.WATER && effects.isEmpty()) {
				causeWaterAOE();
			}
			else if (!effects.isEmpty()) {
				spawnEffectAOE(stack, potion);
			}
		}
	}

	private void spawnEffectAOE(ItemStack stack, Potion potion) {
		Optional<PlayerEntity> owner = getOwner();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		CompoundNBT nbt = stack.getTag();
		int color = PotionUtils.getColor(stack);
		if (nbt != null && nbt.contains("CustomPotionColor", Constants.NBT.TAG_ANY_NUMERIC)) {
			color = nbt.getInt("CustomPotionColor");
		}
		List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(stack);
		spawnEffectAOE(world, shooter, getPositionVec(), potion, effects, color);
	}

	public static void spawnEffectAOE(World world, @Nullable LivingEntity attacker, Vector3d pos, Potion potion, Collection<EffectInstance> effects, int color) {
		AreaEffectCloudEntity aoeCloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
		aoeCloud.setOwner(attacker);
		aoeCloud.setRadius(3f);
		aoeCloud.setRadiusOnUse(-0.5f);
		aoeCloud.setWaitTime(10);
		aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / aoeCloud.getDuration());
		aoeCloud.setColor(color);
		aoeCloud.setPotion(potion);
		for (EffectInstance effect : effects) aoeCloud.addEffect(new EffectInstance(effect));

		world.addEntity(aoeCloud);

		int event = potion.hasInstantEffect() ? Constants.WorldEvents.POTION_IMPACT : Constants.WorldEvents.POTION_IMPACT_INSTANT;
		world.playEvent(event, new BlockPos(pos), color);
	}

	private void causeWaterAOE() {
		Optional<PlayerEntity> owner = getOwner();
		LivingEntity shooter = owner.isPresent() ? owner.get() : this;
		causeWaterAOE(world, shooter);
	}

	public static void causeWaterAOE(World world, Entity attacker) {
		AxisAlignedBB axisalignedbb = attacker.getBoundingBox().grow(4d, 2d, 4d);
		List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, PotionEntity.WATER_SENSITIVE);
		if (!entities.isEmpty()) {
			for (LivingEntity victim : entities) {
				if (attacker.getDistanceSq(victim) < 16d) {
					victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(victim, attacker), 1f);
				}
			}
		}
	}

	@Override
	protected ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
		if (!isOwner(player)) return ActionResultType.PASS;

		if (!player.world.isRemote() && player.isSneaking()) {
			setDead();
			ItemStack stack = ModItems.BOOMLING_GRENADE.get().setPotionItemStack(new ItemStack(ModItems.BOOMLING_GRENADE.get()), getStoredPotion().copy());
			if (hasCustomName()) stack.setDisplayName(getCustomName());
			if (player.addItemStackToInventory(stack)) {
				setStoredPotion(ItemStack.EMPTY);
			}
			else {
				entityDropItem(stack);
			}
		}
		return ActionResultType.func_233537_a_(world.isRemote());
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
			playSound(soundevent, getSoundVolume(), getSoundPitch() * 1.2f);
		}
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		livingSoundTime = -getTalkInterval();
		SoundEvent soundevent = getHurtSound(source);
		if (soundevent != null) {
			playSound(soundevent, getSoundVolume(), getSoundPitch() * 1.2f);
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.1f, 1.2f);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SPIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SLIME_HURT_SMALL;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SLIME_DEATH_SMALL;
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		return false;
	}

}
