package com.github.elenterius.biomancy.world.block.cradle;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.network.ISyncableAnimation;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.FleshVeinsBlock;
import com.github.elenterius.biomancy.world.block.entity.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.world.entity.fleshblob.AbstractFleshBlob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

	public static final int DURATION = 20 * 4; //in ticks

	public static final String SACRIFICE_SYNC_KEY = "SyncSacrificeHandler";
	public static final String SACRIFICE_KEY = "SacrificeHandler";
	protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("cradle.anim.idle");
	protected static final AnimationBuilder SPIKE_ANIM = new AnimationBuilder().addAnimation("cradle.anim.spike");
	private final SacrificeHandler sacrificeHandler = new SacrificeHandler();
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	private long ticks;
	private boolean playAttackAnimation = false;

	public PrimordialCradleBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PRIMORDIAL_CRADLE.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PrimordialCradleBlockEntity creator) {
		if (creator.isFull()) {
			creator.ticks++;
			if (creator.ticks > DURATION) {
				creator.onSacrifice((ServerLevel) level);
				creator.ticks = 0;
			}
		}
	}

	public boolean insertItem(ItemStack stack) {
		if (level == null || level.isClientSide() || stack.isEmpty()) return false;
		if (sacrificeHandler.isFull()) return false;

		ItemStack prevStack = stack.copy();
		if (sacrificeHandler.addItem(stack)) {
			setChanged();
			syncToClient();
			visualizeIngredientValidity((ServerLevel) level, prevStack);
			return true;
		}
		return false;
	}

	private void visualizeIngredientValidity(ServerLevel level, ItemStack stack) {
		if (sacrificeHandler.isValidIngredient(stack)) {
			BlockPos pos = getBlockPos();
			if (sacrificeHandler.getSuccessModifier(stack) <= 0) {
				int particleCount = level.random.nextInt(1, 3);
				sendParticlesToClient(level, pos, ParticleTypes.ANGRY_VILLAGER, particleCount);
			}
			int particleCount = level.random.nextInt(6, 9);
			SimpleParticleType particleType = sacrificeHandler.isLifeEnergySource(stack) ? ParticleTypes.GLOW : ParticleTypes.HAPPY_VILLAGER;
			sendParticlesToClient(level, pos, particleType, particleCount);
		}
		else {
			int particleCount = level.random.nextInt(2, 4);
			sendParticlesToClient(level, getBlockPos(), ParticleTypes.ANGRY_VILLAGER, particleCount);
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
		setChanged();
		syncToClient();
	}

	public void onSacrifice(ServerLevel level) {
		BlockPos pos = getBlockPos();
		if (sacrificeHandler.getTumorFactor() < 2f && level.random.nextFloat() < sacrificeHandler.getSuccessChance()) {
			spawnFleshBlob(level, pos, sacrificeHandler);
			SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPAWN_MOB);
			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		}
		else {
			if (sacrificeHandler.getSuccessChance() > -9000) {
				attackAOE(level, pos);
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPIKE_ATTACK);
				spawnFleshBlocks(level, pos, sacrificeHandler);
			}
			else {
				spawnMalignantFleshBlob(level, pos);
				SoundUtil.broadcastBlockSound(level, pos, SoundEvents.TRIDENT_THUNDER, 5f, 0.9f);
				level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
			}
		}

		resetState();
	}

	public void spawnMalignantFleshBlob(ServerLevel level, BlockPos pos) {
		AbstractFleshBlob fleshBlob = ModEntityTypes.MALIGNANT_FLESH_BLOB.get().create(level);
		if (fleshBlob != null) {
			float yaw = PrimordialCradleBlock.getYRotation(getBlockState());
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, yaw, 0);
			fleshBlob.yHeadRot = fleshBlob.getYRot();
			fleshBlob.yBodyRot = fleshBlob.getYRot();
			fleshBlob.randomizeTumors();
			fleshBlob.restrictTo(pos, 32);
			level.addFreshEntity(fleshBlob);
		}
	}

	public void spawnFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		EntityType<? extends AbstractFleshBlob> entityType = level.random.nextFloat() < sacrificeHandler.getHostileChance() ? ModEntityTypes.HUNGRY_FLESH_BLOB.get() : ModEntityTypes.FLESH_BLOB.get();
		spawnFleshBlob(level, pos, sacrificeHandler, entityType);
	}

	public void spawnFleshBlob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler, EntityType<? extends AbstractFleshBlob> fleshBlobType) {
		AbstractFleshBlob fleshBlob = fleshBlobType.create(level);
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

	public void spawnFleshBlocks(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		if (level.random.nextFloat() >= sacrificeHandler.getTumorFactor()) return;

		FleshVeinsBlock veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();
		BlockState state = level.getBlockState(pos);

		if (level.random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
		if (level.random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
		if (level.random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
		if (level.random.nextFloat() < 0.6f) veinsBlock.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
		level.playSound(null, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundSource.BLOCKS, 1f, 0.15f + level.random.nextFloat() * 0.5f);

		for (Direction subDirection : Direction.allShuffled(level.random)) {
			BlockPos neighborPos = pos.relative(subDirection);
			BlockState neighborState = level.getBlockState(neighborPos);
			veinsBlock.increaseCharge(level, neighborPos, neighborState, level.random.nextIntBetweenInclusive(1, 3));
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
			entity.hurt(ModDamageSources.CREATOR_SPIKES, damage);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(SACRIFICE_KEY, sacrificeHandler.serializeNBT());
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

	private PlayState handleAnim(AnimationEvent<PrimordialCradleBlockEntity> event) {
		//		if (fillLevel >= getMaxFillLevel()) {
		//			event.getController().setAnimation(new AnimationBuilder().addAnimation("cradle.anim.work"));
		//		}

		if (playAttackAnimation) {
			event.getController().setAnimation(SPIKE_ANIM);
			if (event.getController().getAnimationState() != AnimationState.Stopped) return PlayState.CONTINUE;
			stopAttackAnimation();
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
