package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.entity.misc.GasCloud;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class GrenadeProjectile extends ThrowableItemProjectile {

	protected static final byte ITEM_BREAK_EVENT = 3;
	protected static final EntityDataAccessor<Integer> DELAY_DATA = SynchedEntityData.defineId(GrenadeProjectile.class, EntityDataSerializers.INT);

	public GrenadeProjectile(EntityType<? extends GrenadeProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public GrenadeProjectile(Level level, LivingEntity thrower) {
		super(ModEntityTypes.GRENADE_PROJECTILE.get(), thrower, level);
	}

	public GrenadeProjectile(Level level, double x, double y, double z) {
		super(ModEntityTypes.GRENADE_PROJECTILE.get(), x, y, z, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DELAY_DATA, 0);
	}

	public int getExplodeDelayTicks() {
		return entityData.get(DELAY_DATA);
	}

	public void setExplodeDelay(int ticks) {
		entityData.set(DELAY_DATA, ticks);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		hitResult.getEntity().hurt(damageSources().thrown(this, getOwner()), 0F);
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);

		if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {

			serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.2f, 1f);

			Item item = getItem().getItem();

			if (item == ModItems.TOXIN_GRENADE.get()) {
				GasCloud cloud = new GasCloud(serverLevel, getImpactPos(hitResult));
				cloud.setRadius(3.5f);
				cloud.setDuration(15 * 20);
				cloud.addEffect(new MobEffectInstance(ModMobEffects.TOXIN.get(), 8 * 20));
				serverLevel.addFreshEntity(cloud);
			}
			else if (item == ModItems.ACID_GRENADE.get()) {
				serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0d, 0d, 0d, 1d);
				ModBlocks.ACID_SPLATTER.get().propagateAcidSplatters(serverLevel, getImpactPos(hitResult), 4, random);
			}
			else if (item == ModItems.DECAY_GRENADE.get()) {
				ExplosionUtil.explodeDecay(serverLevel, this, getX(), getY(), getZ(), 4.5f, Level.ExplosionInteraction.TNT);
			}
			else if (item == ModItems.INCENDIARY_GRENADE.get()) {
				ExplosionUtil.explodeIncendiary(serverLevel, this, getX(), getY(), getZ(), 3.5f, Level.ExplosionInteraction.TNT);
			}

			serverLevel.broadcastEntityEvent(this, ITEM_BREAK_EVENT);
			discard();
		}

	}

	protected BlockPos getImpactPos(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
			return blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
		}
		return blockPosition();
	}

	@Override
	public void handleEntityEvent(byte eventId) {
		if (eventId == 3) {
			ItemStack item = getItem();
			double scale = 0.08D;
			for (int i = 0; i < 8; i++) {
				double xSpeed = (random.nextDouble() - 0.5d) * scale;
				double ySpeed = (random.nextDouble() - 0.5d) * scale;
				double zSpeed = (random.nextDouble() - 0.5d) * scale;
				level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), getX(), getY(), getZ(), xSpeed, ySpeed, zSpeed);
			}
		}
	}

	protected Item getDefaultItem() {
		return ModItems.TOXIN_GRENADE.get();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setExplodeDelay(tag.getInt("explode_delay_ticks"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("explode_delay_ticks", getExplodeDelayTicks());
	}

}
