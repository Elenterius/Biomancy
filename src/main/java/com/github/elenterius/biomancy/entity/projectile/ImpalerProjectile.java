package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ImpalerProjectile extends BaseProjectile implements GeoEntity {

	//	private static final double IMPALE_FORCE_THRESHOLD = 3d;
	//	private static final double ARMOR_THRESHOLD = 12d;
	private static final int MAX_IMPALED_MOBS = 3;
	//	private static final float IMPACT_DAMAGE = 8f;

	private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(ImpalerProjectile.class, EntityDataSerializers.BYTE);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private boolean isImpaled = false;
	private Vec3 impaledPosition = null;
	private Vec3 impaledDirection = Vec3.ZERO;

	public ImpalerProjectile(EntityType<? extends ImpalerProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public ImpalerProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.IMPALER_PROJECTILE.get(), level, x, y, z);
	}

	protected void defineSynchedData() {
		entityData.define(PIERCE_LEVEL, (byte) 0);
	}

	private boolean canImpaleToBlock(BlockState blockState, BlockPos pos) {
		// we can't impale to very weak blocks, air, liquid, or replaceable blocks
		return !blockState.isAir() && !blockState.liquid() && !blockState.is(BlockTags.REPLACEABLE) && blockState.getDestroySpeed(level(), pos) >= 3f;
	}

	private void impaleToBlock(BlockHitResult blockHit) {
		isImpaled = true;
		impaledPosition = blockHit.getLocation();
		impaledDirection = getDeltaMovement().normalize();

		setDeltaMovement(Vec3.ZERO);
		setNoGravity(true);
		//setNoPhysics(true);
	}

	private boolean tryToDestroyBlock(BlockState blockState, BlockPos pos) {
		if (!getPassengers().isEmpty()) return false;

		boolean hasResistance = !(blockState.is(BlockTags.REPLACEABLE) || blockState.is(BlockTags.LEAVES));
		float destroySpeed = hasResistance ? blockState.getDestroySpeed(level(), pos) : 0f;

		if (destroySpeed < 0f) return false; // the block is indestructible (bedrock)

		byte pierceLevel = getPierceLevel();

		if (hasResistance && pierceLevel <= 0) return false;

		Vec3 movement = getDeltaMovement();
		double moveSpeed = movement.length();

		if (moveSpeed < 0.1d) return false; // 0.1 blocks per tick = 2 blocks per second

		if (moveSpeed * 2.5f >= destroySpeed) {
			if (!level().isClientSide) {
				level().destroyBlock(pos, true);
			}

			if (hasResistance) {
				// deep slate destroy speed is 4.5
				//				float min = Mth.clamp(pierceLevel / 4f, 0.25f, 0.9f);
				float friction = Mth.clamp((1f - destroySpeed / 4.5f), 0.25f, 0.95f);

				setDeltaMovement(movement.scale(friction));
				setPierceLevel(pierceLevel - 1);
			}

			return true;
		}

		return false;
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos pos = blockPosition();
		if (isImpaled && !canImpaleToBlock(level().getBlockState(pos), pos)) {
			ejectPassengers();
			isImpaled = false;
			discard();
		}
	}

	@Override
	public @Nullable LivingEntity getControllingPassenger() {
		return null;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return getPassengers().size() < 3;
	}

	@Override
	public double getPassengersRidingOffset() {
		return getBbHeight() * 0.5d;
	}

	@Override
	protected void positionRider(Entity passenger, MoveFunction mover) {
		if (hasPassenger(passenger)) {

			int i = getPassengers().indexOf(passenger);
			double offset = 0.5d + (i * 0.75d);
			Vec3 pos = position().add(getDeltaMovement().normalize().scale(-offset));

			double yOffset = getPassengersRidingOffset() - (passenger.getBbHeight() * 0.5f);
			mover.accept(passenger, pos.x, pos.y + yOffset, pos.z);
		}
	}

	@Override
	public float getGravity() {
		double speed = getDeltaMovement().length();
		return Math.max(speed < 1d ? 0.05f : 0.001f, getPassengers().size() * 0.02f);
	}

	@Override
	public float getDrag() {
		return 0.999f;
	}

	@Override
	public float getDamage() {
		return Mth.clamp(super.getDamage() * (float) (getDeltaMovement().length() / ModItems.IMPALER.get().getGunProperties().velocity()), 0f, Integer.MAX_VALUE);
	}

	public void setPierceLevel(int level) {
		entityData.set(PIERCE_LEVEL, (byte) (level * 2));
	}

	public byte getPierceLevel() {
		return this.entityData.get(PIERCE_LEVEL);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("pierce", getPierceLevel());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setPierceLevel(tag.getInt("pierce"));
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void onHit(HitResult result) {
		HitResult.Type type = result.getType();

		if (type == HitResult.Type.ENTITY) {
			onHitEntity((EntityHitResult) result);
			level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, null));
		}
		else if (type == HitResult.Type.BLOCK) {
			BlockHitResult hitResult = (BlockHitResult) result;
			onHitBlock(hitResult);
			BlockPos blockpos = hitResult.getBlockPos();
			level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, level().getBlockState(blockpos)));
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		BlockState blockState = level().getBlockState(result.getBlockPos());
		BlockPos blockPos = result.getBlockPos();

		//		if (!impaledEntities.isEmpty() && !isImpaled) {
		//			if (canImpaleToBlock(hitBlockState, hitBlockPos)) {
		//				impaleToBlock(result);
		//			}
		//			else {
		//				if (!tryToDestroyBlock(hitBlockPos)) {
		//					impaleToBlock(result);
		//				}
		//			}
		//		}
		//		else if (impaledEntities.isEmpty() && !isImpaled) {
		//			if (!canImpaleToBlock(hitBlockState, hitBlockPos) || hitBlockState.is(BlockTags.LEAVES)) {
		//				tryToDestroyBlock(hitBlockPos);
		//			}
		//			else {
		//				impaleToBlock(result);
		//			}
		//		}

		/////////////

		if (!tryToDestroyBlock(blockState, blockPos)) {
			blockState.onProjectileHit(level(), blockState, result, this);

			//playSound(blockState.getSoundType().getHitSound(), 2f, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
			playSound(ModSoundEvents.IMPALER_IMPACT.get(), 1f, 0.8f + random.nextFloat() * 0.3f);
			Vec3 vec = result.getLocation();
			level().addParticle(ParticleTypes.EXPLOSION, vec.x, vec.y, vec.z, 1d, 0d, 0d);

			ejectPassengers();
			discard();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {

		Entity victim = hitResult.getEntity();

		int numberOfImpaledEntities = getPassengers().size();

		if (isImpaled || numberOfImpaledEntities >= MAX_IMPALED_MOBS || !(victim instanceof LivingEntity livingTarget)) {
			return;
		}

		Entity shooter = getOwner();
		if (shooter instanceof LivingEntity livingEntity) {
			livingEntity.setLastHurtMob(victim);
		}

		//		double armorValue = livingTarget.getArmorValue();
		//		double toughness = livingTarget.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
		//		boolean hasHighArmor = armorValue + toughness >= ARMOR_THRESHOLD;
		//
		// Calculate entity mass factor (approximation based on health and size)
		double mass = livingTarget.getMaxHealth() * (livingTarget.getBbWidth() * livingTarget.getBbHeight());
		//		double speed = getDeltaMovement().length();
		//		double impactForce = speed * getDamage();
		//
		boolean hurtSuccess;
		//
		//		if (hasHighArmor || impactForce < IMPALE_FORCE_THRESHOLD) {
		//			hurtSuccess = livingTarget.hurt(ModDamageSources.impalerProjectile(level(), this, getOwner()), IMPACT_DAMAGE);
		//
		//			double knockbackStrength = (getKnockback() / Math.max(1d, mass / 10d)) * Math.max(0d, 1d - livingTarget.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
		//			Vec3 motion = getDeltaMovement().normalize().scale(knockbackStrength);
		//			if (motion.lengthSqr() > 0d) livingTarget.push(motion.x, Math.max(motion.y, 0.1d), motion.z);
		//
		//			addConcussionEffect(livingTarget);
		//		}
		//		else {
		float damage = getDamage();
		hurtSuccess = livingTarget.hurt(ModDamageSources.impalerProjectile(level(), this, getOwner()), damage);
		//		livingTarget.startRiding(this, true);
		//		}

		if (hurtSuccess && !level().isClientSide) {
			if (shooter instanceof LivingEntity livingEntity) {
				doEnchantDamageEffects(livingEntity, victim); //thorn & arthropod damage
			}

			if (damage >= 2f && victim instanceof LivingEntity livingEntity) {
				disableShield(livingEntity, 15 * 20);
			}

			if (!isSilent() && victim != shooter && victim instanceof Player && shooter instanceof ServerPlayer serverPlayer) {
				SoundUtil.Server.sendSoundToClient(serverPlayer, SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
			}
		}

		if (!isImpaled) {
			// Reduce velocity slightly for each hit entity based on its mass
			double slowdownFactor = Math.max(0.7d, 1d - (mass / 100d));
			double gravity = Math.max(MobUtil.getGravity(livingTarget), 0); //we ignore upwards gravity

			setDeltaMovement(
					getDeltaMovement()
							.scale(slowdownFactor)
							.add(0d, gravity + 0.1d, 0d) //give slight upwards motion
			);

			byte pierceLevel = getPierceLevel();
			setPierceLevel(pierceLevel - 1);
			if (pierceLevel <= 0) {
				discard();
			}
		}

		if (hurtSuccess && livingTarget.isDeadOrDying()) {
			playSound(ModSoundEvents.IMPALER_IMPACT.get(), 1f, 0.8f + random.nextFloat() * 0.3f);
			Vec3 vec = hitResult.getLocation();
			level().addParticle(ParticleTypes.EXPLOSION, vec.x, vec.y, vec.z, 1d, 0d, 0d);
		}
		else {
			playSound(ModSoundEvents.IMPALER_HIT.get(), 2f, 0.8f + random.nextFloat() * 0.3f);
		}
	}

	protected void disableShield(LivingEntity victim, int cooldownTicks) {
		ItemStack itemStackInUse = victim.getUseItem();

		if (!itemStackInUse.isEmpty() && victim.isUsingItem() && itemStackInUse.getItem().canPerformAction(itemStackInUse, ToolActions.SHIELD_BLOCK)) {
			if (victim instanceof Player player) player.getCooldowns().addCooldown(itemStackInUse.getItem(), cooldownTicks);
			victim.stopUsingItem();
			victim.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + level().random.nextFloat() * 0.4f);
		}
	}

	@Override
	protected ParticleOptions getParticle() {
		return ParticleTypes.CRIT;
	}

	@Override
	public void spawnParticle(double x, double y, double z) {
		if (random.nextFloat() <= 0.7f) super.spawnParticle(x, y, z);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//do nothing
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

}
