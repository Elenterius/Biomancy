package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.block.base.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.config.PrimalEnergySettings;
import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.MoundGenerator;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.math.IntMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrimordialCradleBlockEntity extends SimpleSyncedBlockEntity implements PrimalEnergyHandler, GeoBlockEntity {

	public static final String SACRIFICE_SYNC_KEY = "SyncSacrificeHandler";
	public static final String SACRIFICE_KEY = "SacrificeHandler";
	public static final String PRIMAL_ENERGY_KEY = "PrimalEnergy";
	public static final String PROC_GEN_VALUES_KEY = "ProcGenValues";

	public static final int DURATION_TICKS = 20 * 4;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final SacrificeHandler sacrificeHandler = new SacrificeHandler();
	private long ticks;
	private int primalEnergy;
	private @Nullable MoundShape.ProcGenValues procGenValues;

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

	@Override
	public void onLoad() {
		super.onLoad();
		if (level instanceof ServerLevel serverLevel) {
			Shape shape = SpatialShapeManager.getOrCreateShape(serverLevel, worldPosition, () -> {
				if (procGenValues != null) {
					return MoundGenerator.constructShape(worldPosition, procGenValues);
				}
				return MoundGenerator.constructShape(level, worldPosition, level.random.nextLong());
			});

			if (shape instanceof MoundShape moundShape) {
				MoundShape.ProcGenValues values = moundShape.getProcGenValues();
				if (!values.equals(procGenValues)) {
					procGenValues = values;
					markChunkAsUnsaved();
				}
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
			markChunkAsUnsaved();
			syncToClient();
			spawnTributeParticles((ServerLevel) level, tribute);
		});
	}

	private void spawnTributeParticles(ServerLevel level, Tribute tribute) {
		BlockPos pos = getBlockPos();

		if (tribute.successModifier() < 0) {
			int n = tribute.successModifier() < -99 ? 2 : 0;
			int particleCount = level.random.nextInt(1 + n, 3 + n);
			sendParticlesToClient(level, pos, ParticleTypes.ANGRY_VILLAGER, particleCount);
		}
		else if (tribute.successModifier() > 0) {
			int n = Mth.clamp(Math.round(tribute.lifeEnergy() / 100f) * 8, 1, 8);
			int particleCount = level.random.nextInt(n, n + 2);
			sendParticlesToClient(level, pos, ModParticleTypes.LIGHT_GREEN_GLOW.get(), particleCount);
		}

		if (tribute.lifeEnergy() > 0) {
			int n = Mth.clamp(Math.round(tribute.lifeEnergy() / 100f) * 8, 1, 8);
			int particleCount = level.random.nextInt(n, n + 2);
			sendParticlesToClient(level, pos, ModParticleTypes.PINK_GLOW.get(), particleCount);
		}

		if (tribute.anomalyModifier() > 0) {
			int n = Mth.clamp(Math.round(tribute.anomalyModifier() / 100f) * 5, 1, 5);
			int particleCount = level.random.nextInt(n, n + 4);
			sendParticlesToClient(level, pos, ModParticleTypes.BIOHAZARD.get(), particleCount);
		}

		if (tribute.hostileModifier() > 0) {
			int n = Mth.clamp(Math.round(tribute.hostileModifier() / 100f) * 5, 1, 5);
			int particleCount = level.random.nextInt(n, n + 4);
			sendParticlesToClient(level, pos, ModParticleTypes.HOSTILE.get(), particleCount);
		}

		if (tribute.diseaseModifier() > 0) {
			int n = Mth.clamp(Math.round(tribute.diseaseModifier() / 100f) * 8, 1, 8);
			int particleCount = level.random.nextInt(n, n + 4);
			sendParticlesToClient(level, pos, ParticleTypes.MYCELIUM, particleCount);
		}
	}

	private void sendParticlesToClient(ServerLevel level, BlockPos pos, ParticleOptions particleOptions, int particleCount) {
		level.sendParticles(particleOptions, pos.getX() + 0.5d, pos.getY() + 0.8d, pos.getZ() + 0.5d, particleCount, 0.25d, 0.25d, 0.25d, 0);
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

	public float getSuccessChance() {
		return sacrificeHandler.getSuccessChance();
	}

	public float getHostileChance() {
		return sacrificeHandler.getHostileChance();
	}

	public float getAnomalyChance() {
		return sacrificeHandler.getAnomalyChance();
	}

	public float getDiseaseChance() {
		return sacrificeHandler.getTumorFactor();
	}

	public boolean hasModifiers() {
		return sacrificeHandler.hasModifiers();
	}

	private void resetState() {
		sacrificeHandler.reset();
		markChunkAsUnsaved();
		syncToClient();
	}

	/**
	 * equivalent to calling #setChanged() but without notifying block neighbors of the change
	 */
	protected void markChunkAsUnsaved() {
		if (level != null && level.hasChunkAt(worldPosition)) {
			level.getChunkAt(worldPosition).setUnsaved(true);
		}
	}

	public void onSacrifice(ServerLevel level) {
		BlockPos pos = getBlockPos();

		float radius = 8f;
		AABB aabb = AABB.ofSize(Vec3.atCenterOf(pos), radius * 2, radius * 2, radius * 2);
		List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, aabb, EntitySelector.NO_SPECTATORS.and(Entity::isAlive));

		if (!nearbyPlayers.isEmpty()) {
			Tribute tribute = SimpleTribute.builder().successModifier(1).hostileModifier(-5).build();
			Tribute specialTribute = SimpleTribute.builder().successModifier(20).hostileModifier(-1000).build();

			final Set<HashCode> HASHES = Set.of(
					HashCode.fromString("20f0bf6814e62bb7297669efb542f0af6ee0be1a9b87d0702853d8cc5aa15dc4"),
					HashCode.fromString("2853ecb1a83a461153a2f8b6a274eab0c4597a9ef7d622673dab419543d486b6")
			);

			for (Player player : nearbyPlayers) {
				for (ItemStack armor : player.getArmorSlots()) {
					if (armor.getItem() instanceof AcolyteArmorItem) {
						sacrificeHandler.addTribute(tribute);
					}
				}

				if (player.isCrouching()) {
					HashCode hashCode = Hashing.sha256().hashString(player.getStringUUID(), StandardCharsets.UTF_8);
					if (HASHES.contains(hashCode)) {
						sacrificeHandler.addTribute(specialTribute);
					}
				}
			}
		}

		float successChance = sacrificeHandler.getSuccessChance();
		float energyMultiplier = sacrificeHandler.getLifeEnergyPct();

		if (level.random.nextFloat() < successChance) {

			if (level.random.nextFloat() < sacrificeHandler.getAnomalyChance()) {
				spawnPrimordialFleshBlob(level, pos, sacrificeHandler);
				addPrimalEnergy(Math.round(4096 * energyMultiplier));
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CRADLE_SPAWN_PRIMORDIAL_MOB);
			}
			else {
				if (sacrificeHandler.getHostileChance() < -4.2f) {
					spawnLegacyFleshBlob(level, pos, sacrificeHandler);
				}
				else {
					spawnFleshBlob(level, pos, sacrificeHandler);
				}

				addPrimalEnergy(Math.round(2048 * energyMultiplier));
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CRADLE_SPAWN_MOB);
			}

			PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.PRIMAL_FLESH.get().defaultBlockState());

			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		}
		else if (successChance > 0.75f) {
			if (sacrificeHandler.getHostileChance() > 0.6f) {
				attackAOE(level, pos);
			}

			addPrimalEnergy(Math.round(4096 * energyMultiplier));

			if (sacrificeHandler.getAnomalyChance() > 0.8f) {
				PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());
				PrimordialEcosystem.spreadMalignantVeinsFromSource(level, pos, PrimordialEcosystem.MAX_CHARGE_SUPPLIER);
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CRADLE_SPAWN_PRIMORDIAL_MOB);
			}
			else {
				PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.POROUS_PRIMAL_FLESH.get().defaultBlockState());
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CRADLE_SPAWN_MOB);
			}
		}
		else {
			if (sacrificeHandler.getHostileChance() + sacrificeHandler.getTumorFactor() > 0.5f) {
				attackAOE(level, pos);
			}

			PrimordialEcosystem.tryToReplaceBlock(level, pos.below(), ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());
			PrimordialEcosystem.spreadMalignantVeinsFromSource(level, pos, PrimordialEcosystem.MAX_CHARGE_SUPPLIER);

			addPrimalEnergy(Math.round(3072 * energyMultiplier));
			SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.FLESH_BLOCK_STEP.get(), 1f, 0.15f + level.random.nextFloat() * 0.5f);
		}

		resetState();
	}

	@Override
	public int getPrimalEnergy() {
		PrimalEnergySettings.SupplyAmount supplyAmount = BiomancyConfig.SERVER.primalEnergySupplyOfCradle.get();
		if (supplyAmount == PrimalEnergySettings.SupplyAmount.UNLIMITED) return Integer.MAX_VALUE;
		if (supplyAmount == PrimalEnergySettings.SupplyAmount.NONE) return 0;

		return primalEnergy;
	}

	private void addPrimalEnergy(int amount) {
		primalEnergy = IntMath.saturatedAdd(primalEnergy, amount);
	}

	@Override
	public int fillPrimalEnergy(int amount) {
		if (amount <= 0) return 0;

		int prevPrimalEnergy = primalEnergy;
		primalEnergy = IntMath.saturatedAdd(primalEnergy, amount);
		int filled = primalEnergy - prevPrimalEnergy;

		setChanged();

		return filled;
	}

	@Override
	public int drainPrimalEnergy(int amount) {
		if (amount <= 0) return 0;

		PrimalEnergySettings.SupplyAmount supplyAmount = BiomancyConfig.SERVER.primalEnergySupplyOfCradle.get();
		if (supplyAmount == PrimalEnergySettings.SupplyAmount.UNLIMITED) return amount;
		if (supplyAmount == PrimalEnergySettings.SupplyAmount.NONE) return 0;

		if (primalEnergy < amount) {
			int prevPrimalEnergy = primalEnergy;
			primalEnergy = 0;
			setChanged();
			return prevPrimalEnergy;
		}

		primalEnergy -= amount;
		setChanged();

		return amount;
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
		broadcastAnimation(Animations.SPIKE_ATTACK);
		SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CRADLE_SPIKE_ATTACK);

		float maxAttackDistance = 1.5f;
		float maxAttackDistanceSqr = maxAttackDistance * maxAttackDistance;
		Vec3 origin = Vec3.atCenterOf(pos);
		AABB aabb = AABB.ofSize(origin, maxAttackDistance * 2, maxAttackDistance * 2, maxAttackDistance * 2);

		List<Entity> victims = level.getEntities((Entity) null, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
		for (Entity entity : victims) {
			float distSqr = (float) entity.distanceToSqr(origin);
			float pct = distSqr / maxAttackDistanceSqr;
			float damage = Mth.clamp(8f * (1 - pct), 0.5f, 8f); //linear damage falloff
			entity.hurt(ModDamageSources.primalSpikes(level, origin), damage);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(SACRIFICE_KEY, sacrificeHandler.serializeNBT());
		tag.putInt(PRIMAL_ENERGY_KEY, primalEnergy);

		if (procGenValues != null) {
			CompoundTag tagProcGen = new CompoundTag();
			procGenValues.writeTo(tagProcGen);
			tag.put(PROC_GEN_VALUES_KEY, tagProcGen);
		}
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		//		tag.put(SACRIFICE_SYNC_KEY, sacrificeHandler.serializeNBT());
		tag.put(SACRIFICE_KEY, sacrificeHandler.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(SACRIFICE_KEY)) {
			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_KEY));
		}
		//		else if (tag.contains(SACRIFICE_SYNC_KEY)) {
		//			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_SYNC_KEY));
		//		}

		primalEnergy = tag.getInt(PRIMAL_ENERGY_KEY);

		if (tag.contains(PROC_GEN_VALUES_KEY)) {
			procGenValues = MoundShape.ProcGenValues.readFrom(tag.getCompound(PROC_GEN_VALUES_KEY));
		}
	}

	protected void broadcastAnimation(TriggerableAnimation animation) {
		triggerAnim(animation.controller(), animation.name());
	}

	protected <T extends PrimordialCradleBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {

		if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
			state.getController().setAnimation(Animations.IDLE.rawAnimation());
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<PrimordialCradleBlockEntity> controller = new AnimationController<>(this, Animations.MAIN_CONTROLLER, 0, this::handleAnimationState);
		Animations.registerTriggerableAnimations(controller);
		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static final class Animations {
		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final String MAIN_CONTROLLER = "main";

		static final TriggerableAnimation IDLE = register(MAIN_CONTROLLER, "idle", RawAnimation.begin().thenPlay("cradle.idle"));
		static final TriggerableAnimation WORK = register(MAIN_CONTROLLER, "work", RawAnimation.begin().thenPlay("cradle.work"));
		static final TriggerableAnimation SPIKE_ATTACK = register(MAIN_CONTROLLER, "spike_attack", RawAnimation.begin().thenPlay("cradle.spike"));

		private Animations() {}

		static TriggerableAnimation register(String controller, String name, RawAnimation rawAnimation) {
			TriggerableAnimation animation = new TriggerableAnimation(controller, name, rawAnimation);
			TRIGGERABLE_ANIMATIONS.add(animation);
			return animation;
		}

		static void registerTriggerableAnimations(AnimationController<?> controller) {
			for (TriggerableAnimation animation : TRIGGERABLE_ANIMATIONS) {
				if (animation.controller().equals(controller.getName())) {
					controller.triggerableAnim(animation.name(), animation.rawAnimation());
				}
			}
		}
	}

}
