package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.block.entity.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.entity.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.network.ISyncableAnimation;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class PrimordialCradleBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable, ISyncableAnimation {

	public static final int DURATION_TICKS = 20 * 4;
	public static final String SACRIFICE_SYNC_KEY = "SyncSacrificeHandler";
	public static final String SACRIFICE_KEY = "SacrificeHandler";
	public static final String PRIMAL_SPREAD_CHARGE_KEY = "PrimalSpreadCharge";
	protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("cradle.idle");
	protected static final AnimationBuilder SPIKE_ANIM = new AnimationBuilder().addAnimation("cradle.spike");
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	private final SacrificeHandler sacrificeHandler = new SacrificeHandler();
	private boolean playAttackAnimation = false;
	private long ticks;
	private int primalSpreadCharge;

	public PrimordialCradleBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PRIMORDIAL_CRADLE.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PrimordialCradleBlockEntity cradle) {
		if (cradle.isFull()) {
			cradle.ticks++;
			if (cradle.ticks > DURATION_TICKS) {
				cradle.onSacrifice((ServerLevel) level);
				cradle.ticks = 0;
			}
		}
	}

	/**
	 * modifies the stack
	 */
	public boolean insertItem(ItemStack stack) {
		if (level == null || level.isClientSide() || stack.isEmpty()) return false;
		if (sacrificeHandler.isFull()) return false;

		return sacrificeHandler.addItem(stack, tribute -> {
			setChangedSilent();
			syncToClient();
			spawnTributeParticles((ServerLevel) level, tribute);
		});
	}

	private void spawnTributeParticles(ServerLevel level, ITribute tribute) {
		BlockPos pos = getBlockPos();

		if (tribute.successModifier() < 0) {
			int n = tribute.successModifier() < -99 ? 2 : 0;
			int particleCount = level.random.nextInt(1 + n, 3 + n);
			sendParticlesToClient(level, pos, ParticleTypes.ANGRY_VILLAGER, particleCount);
		}
		else if (tribute.successModifier() > 0) {
			int particleCount = level.random.nextInt(6, 9);
			SimpleParticleType particleType = ParticleTypes.HAPPY_VILLAGER;
			sendParticlesToClient(level, pos, particleType, particleCount);
		}

		if (tribute.lifeEnergy() > 0) {
			int n = Mth.clamp(Math.round(tribute.lifeEnergy() / 100f) * 5, 1, 5);
			int particleCount = level.random.nextInt(n, n + 4);
			sendParticlesToClient(level, pos, ParticleTypes.GLOW, particleCount);
		}

		if (tribute.diseaseModifier() > 0) {
			int n = Mth.clamp(Math.round(tribute.diseaseModifier() / 100f) * 8, 1, 8);
			int particleCount = level.random.nextInt(n, n + 4);
			sendParticlesToClient(level, pos, ParticleTypes.MYCELIUM, particleCount);
		}
	}

	private void sendParticlesToClient(ServerLevel level, BlockPos pos, ParticleOptions particleOptions, int particleCount) {
		level.sendParticles(particleOptions, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, particleCount, 0.5d, 0.25d, 0.5d, 0);
	}

	public boolean isFull() {
		return sacrificeHandler.isFull();
	}

	public float getBiomassPct() {
		return sacrificeHandler.getBiomassPct();
	}

	public float getLifeEnergyPct() {
		return sacrificeHandler.getLifeEnergyPct();
	}

	public boolean hasModifiers() {
		return sacrificeHandler.hasModifiers();
	}

	private void resetState() {
		sacrificeHandler.reset();
		setChangedSilent();
		syncToClient();
	}

	/**
	 * equivalent to calling #setChanged() but without notifying block neighbors of the change
	 */
	protected void setChangedSilent() {
		if (level != null && level.hasChunkAt(worldPosition)) {
			level.getChunkAt(worldPosition).setUnsaved(true);
		}
	}

	public void onSacrifice(ServerLevel level) {
		BlockPos pos = getBlockPos();
		if (level.random.nextFloat() < sacrificeHandler.getSuccessChance()) {

			if (level.random.nextFloat() < sacrificeHandler.getAnomalyChance()) {
				spawnPrimordialFleshBlob(level, pos, sacrificeHandler);
				primalSpreadCharge += 2048;
				SoundUtil.broadcastBlockSound(level, pos, SoundEvents.FOX_SCREECH, 2f, 0.5f);
			}
			else {
				if (sacrificeHandler.getHostileChance() < -4.2f) {
					spawnLegacyFleshBlob(level, pos, sacrificeHandler);
				}
				else {
					spawnFleshBlob(level, pos, sacrificeHandler);
				}

				primalSpreadCharge += 512;
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPAWN_MOB);
			}

			if (level.random.nextFloat() >= sacrificeHandler.getTumorFactor() / 2) {
				PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.PRIMAL_FLESH.get().defaultBlockState());
			}

			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		}
		else {
			attackAOE(level, pos);
			SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPIKE_ATTACK);
			if (!PrimordialEcosystem.spreadMalignantVeinsFromSource(level, pos, PrimordialEcosystem.MAX_CHARGE_SUPPLIER)) {
				PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.PRIMAL_FLESH.get().defaultBlockState());
			}

			primalSpreadCharge += 1024;
			SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), 1f, 0.15f + level.random.nextFloat() * 0.5f);
		}

		resetState();
	}

	public boolean consumePrimalSpreadCharge(ServerLevel level, int amount) {
		if (amount <= 0) return false;
		if (primalSpreadCharge < amount) return false;

		primalSpreadCharge -= amount;
		setChanged();

		return true;
	}

	public void spawnPrimordialFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		EntityType<? extends FleshBlob> entityType = level.random.nextFloat() < sacrificeHandler.getHostileChance() ? ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get() : ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get();
		spawnPrimordialFleshBlob(level, pos, entityType);
	}

	public void spawnPrimordialFleshBlob(ServerLevel level, BlockPos pos, EntityType<? extends FleshBlob> fleshBlobType) {
		FleshBlob fleshBlob = fleshBlobType.create(level);
		if (fleshBlob != null) {
			float yaw = PrimordialCradleBlock.getYRotation(getBlockState());
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, yaw, 0);
			fleshBlob.yHeadRot = fleshBlob.getYRot();
			fleshBlob.yBodyRot = fleshBlob.getYRot();
			fleshBlob.restrictTo(pos, 32);
			level.addFreshEntity(fleshBlob);
		}
	}

	public void spawnLegacyFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		spawnFleshBlob(level, pos, sacrificeHandler, ModEntityTypes.LEGACY_FLESH_BLOB.get());
	}

	public void spawnFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		EntityType<? extends FleshBlob> entityType = level.random.nextFloat() < sacrificeHandler.getHostileChance() ? ModEntityTypes.HUNGRY_FLESH_BLOB.get() : ModEntityTypes.FLESH_BLOB.get();
		spawnFleshBlob(level, pos, sacrificeHandler, entityType);
	}

	public void spawnFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler, EntityType<? extends FleshBlob> fleshBlobType) {
		FleshBlob fleshBlob = fleshBlobType.create(level);
		if (fleshBlob != null) {
			float yaw = PrimordialCradleBlock.getYRotation(getBlockState());
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, yaw, 0);
			fleshBlob.yHeadRot = fleshBlob.getYRot();
			fleshBlob.yBodyRot = fleshBlob.getYRot();
			fleshBlob.setTumors(sacrificeHandler.getTumorFactor());
			fleshBlob.restrictTo(pos, 24);
			level.addFreshEntity(fleshBlob);
		}
	}

	public void attackAOE() {
		if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
			attackAOE(serverLevel, worldPosition);
		}
	}

	protected void attackAOE(ServerLevel level, BlockPos pos) {
		ModNetworkHandler.sendAnimationToClients(this, 0, 0);

		float maxAttackDistance = 1.5f;
		float maxAttackDistanceSqr = maxAttackDistance * maxAttackDistance;
		Vec3 origin = Vec3.atCenterOf(pos);
		AABB aabb = AABB.ofSize(origin, maxAttackDistance * 2, maxAttackDistance * 2, maxAttackDistance * 2);

		List<Entity> victims = level.getEntities((Entity) null, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
		for (Entity entity : victims) {
			float distSqr = (float) entity.distanceToSqr(origin);
			float pct = distSqr / maxAttackDistanceSqr;
			float damage = Mth.clamp(8f * (1 - pct), 0.5f, 8f); //linear damage falloff
			entity.hurt(ModDamageSources.PRIMORDIAL_SPIKES, damage);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(SACRIFICE_KEY, sacrificeHandler.serializeNBT());
		tag.putInt(PRIMAL_SPREAD_CHARGE_KEY, primalSpreadCharge);
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.put(SACRIFICE_SYNC_KEY, sacrificeHandler.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(SACRIFICE_KEY)) {
			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_KEY));
		}
		else if (tag.contains(SACRIFICE_SYNC_KEY)) {
			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_SYNC_KEY));
		}

		primalSpreadCharge = tag.getInt(PRIMAL_SPREAD_CHARGE_KEY);
	}

	@Override
	public void onAnimationSync(int id, int data) {
		startAttackAnimation();
	}

	public void startAttackAnimation() {
		playAttackAnimation = true;
	}

	public void stopAttackAnimation() {
		playAttackAnimation = false;
	}

	protected PlayState handleAnim(AnimationEvent<PrimordialCradleBlockEntity> event) {
		//		if (fillLevel >= getMaxFillLevel()) {
		//			event.getController().setAnimation(new AnimationBuilder().addAnimation("cradle.anim.work"));
		//		}

		if (event.getAnimatable().playAttackAnimation) {
			event.getController().setAnimation(SPIKE_ANIM);
			if (event.getController().getAnimationState() != AnimationState.Stopped) return PlayState.CONTINUE;
			event.getAnimatable().stopAttackAnimation();
		}

		if (event.getController().getAnimationState() == AnimationState.Stopped) {
			event.getController().setAnimation(IDLE_ANIM);
		}

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

}
