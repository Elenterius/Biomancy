package com.github.elenterius.biomancy.entity.misc;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.mixin.accessor.EntityAccessor;
import com.github.elenterius.biomancy.world.DynamicGasVolume;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class GasCloud extends Entity implements TraceableEntity, HitboxDebugInfo {

	public static final Predicate<Entity> NO_SPECTATOR_BUT_ALIVE_AND_AFFECTED_BY_POTIONS = entity -> entity.isAlive() && !entity.isSpectator() && entity instanceof LivingEntity livingEntity && livingEntity.isAffectedByPotions();
	public static final float DEFAULT_RADIUS = 3f;
	public static final float MIN_RADIUS = 0.5f;
	public static final float MAX_RADIUS = 8f;
	public static final int DEFAULT_PROPAGATION_DURATION = 20 * 3;

	protected static final EntityDataAccessor<Float> RADIUS_DATA = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.FLOAT);
	protected static final EntityDataAccessor<Integer> PROPAGATION_DURATION_DATA = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Boolean> IS_IDLE_DATA = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<ParticleOptions> PARTICLE_DATA = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.PARTICLE);

	private final List<MobEffectInstance> effects = new ArrayList<>();
	private final Object2IntMap<Entity> reapplyCooldowns = new Object2IntOpenHashMap<>();
	private GasInteractionType gasInteractionType = GasInteractionType.INHALATION;

	private int durationTicks = 20 * 30;
	private int effectReapplyCooldown = 20;
	private int durationModificationPerProc;
	private float radiusModificationPerProc;
	private float radiusModificationPerTick;

	private @Nullable DynamicGasVolume dynamicGasVolume;
	private @Nullable LivingEntity owner;
	private @Nullable UUID ownerUUID;

	public GasCloud(EntityType<? extends GasCloud> entityType, Level level) {
		super(entityType, level);
		noPhysics = true;
	}

	public GasCloud(Level level, Vec3 pos) {
		this(ModEntityTypes.GAS_CLOUD.get(), level);
		setPos(pos);
	}

	public GasCloud(Level level, BlockPos pos) {
		this(level, pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
	}

	public GasCloud(Level level, double x, double y, double z) {
		this(ModEntityTypes.GAS_CLOUD.get(), level);
		setPos(x, y, z);
	}

	protected static @Nullable ParticleOptions readParticleData(CompoundTag data, String key) {
		if (data.contains(key, Tag.TAG_STRING)) {
			String rawString = data.getString(key);
			try {
				return ParticleArgument.readParticle(new StringReader(rawString), BuiltInRegistries.PARTICLE_TYPE.asLookup());
			}
			catch (CommandSyntaxException e) {
				BiomancyMod.LOGGER.warn("Couldn't load custom particle {}", rawString, e);
			}
		}

		return null;
	}

	protected static void writeParticleData(CompoundTag data, String key, ParticleOptions particleOptions) {
		data.putString(key, particleOptions.writeToString());
	}

	@Override
	protected void defineSynchedData() {
		getEntityData().define(RADIUS_DATA, DEFAULT_RADIUS);
		getEntityData().define(PROPAGATION_DURATION_DATA, DEFAULT_PROPAGATION_DURATION);
		getEntityData().define(IS_IDLE_DATA, true);
		getEntityData().define(PARTICLE_DATA, ParticleTypes.CLOUD);
	}

	public float getRadius() {
		return getEntityData().get(RADIUS_DATA);
	}

	public void setRadius(float radius) {
		getEntityData().set(RADIUS_DATA, Mth.clamp(radius, 0f, MAX_RADIUS));
	}

	public int getDuration() {
		return durationTicks;
	}

	public void setDuration(int ticks) {
		durationTicks = ticks;
	}

	public int getPropagationDuration() {
		return getEntityData().get(PROPAGATION_DURATION_DATA);
	}

	public void setPropagationDuration(int durationTicks) {
		getEntityData().set(PROPAGATION_DURATION_DATA, Math.max(durationTicks, 0));
	}

	public GasInteractionType getInteractionType() {
		return gasInteractionType;
	}

	public void setInteractionType(GasInteractionType type) {
		gasInteractionType = type;
	}

	public int getEffectReapplyCooldown() {
		return effectReapplyCooldown;
	}

	public void setEffectReapplyCooldown(int effectReapplyCooldown) {
		this.effectReapplyCooldown = effectReapplyCooldown;
	}

	public void setDurationModificationPerProc(int durationModificationPerProc) {
		this.durationModificationPerProc = durationModificationPerProc;
	}

	public void setRadiusModificationPerTick(float radiusModificationPerTick) {
		this.radiusModificationPerTick = radiusModificationPerTick;
	}

	public void setRadiusModificationPerProc(float radiusModificationPerProc) {
		this.radiusModificationPerProc = radiusModificationPerProc;
	}

	public void addEffect(MobEffectInstance effectInstance) {
		effects.add(effectInstance);
	}

	public ParticleOptions getParticle() {
		return getEntityData().get(PARTICLE_DATA);
	}

	public void setParticle(ParticleOptions particleOptions) {
		getEntityData().set(PARTICLE_DATA, particleOptions);
	}

	@Override
	public void tick() {
		boolean firstTick = this.firstTick;

		super.tick();

		if (level().isClientSide) {
			clientTick();
		}
		else {
			serverTick();
		}

		spreadGas(firstTick);
	}

	protected void serverTick() {
		int propagationDurationTicks = getPropagationDuration();

		if (tickCount >= propagationDurationTicks + durationTicks) {
			discard();
			return;
		}

		//wait until propagation is complete
		if (tickCount < propagationDurationTicks) return;

		float radius = getRadius();

		if (radiusModificationPerTick != 0f) {
			radius += radiusModificationPerTick;
			if (radius < 0.5f) {
				discard();
				return;
			}
			setRadius(radius);
		}

		if (tickCount % 5 != 0) return;

		if (effects.isEmpty()) {
			reapplyCooldowns.clear();
			return;
		}

		if (dynamicGasVolume == null) return;

		reapplyCooldowns.object2IntEntrySet().removeIf(entry -> tickCount >= entry.getIntValue());

		List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox(), NO_SPECTATOR_BUT_ALIVE_AND_AFFECTED_BY_POTIONS);
		if (entities.isEmpty()) return;

		for (LivingEntity livingEntity : entities) {
			if (reapplyCooldowns.containsKey(livingEntity)) continue;

			if (gasInteractionType.canInteract(dynamicGasVolume, livingEntity)) {
				reapplyCooldowns.put(livingEntity, tickCount + effectReapplyCooldown);

				for (MobEffectInstance effectInstance : effects) {
					if (effectInstance.getEffect().isInstantenous()) {
						effectInstance.getEffect().applyInstantenousEffect(this, getOwner(), livingEntity, effectInstance.getAmplifier(), 0.5d);
					}
					else {
						livingEntity.addEffect(new MobEffectInstance(effectInstance), this);
					}
				}

				if (radiusModificationPerProc != 0f) {
					radius += radiusModificationPerProc;
					if (radius < MIN_RADIUS) {
						discard();
						return;
					}
					setRadius(radius);
				}

				if (durationModificationPerProc != 0) {
					durationTicks += durationModificationPerProc;
					if (durationTicks <= 0) {
						discard();
						return;
					}
				}
			}
		}
	}

	protected void clientTick() {
		if (tickCount % 4 != 0) return;
		if (dynamicGasVolume == null || dynamicGasVolume.isEmpty()) return;

		boolean propagationIsNotDone = tickCount < getPropagationDuration();
		LongSet packedPositions = dynamicGasVolume.getPackedPositions();

		ParticleOptions particleoptions = getParticle();
		double x, y, z, xSpeed, ySpeed, zSpeed;

		for (long packedPos : packedPositions) {
			if (propagationIsNotDone) {
				if (random.nextFloat() < 0.35f) continue;
				xSpeed = 0.d;
				ySpeed = 0.d;
				zSpeed = 0.d;
			}
			else {
				if (random.nextFloat() < 0.5f) continue;
				xSpeed = (0.5d - random.nextDouble()) * 0.15d;
				ySpeed = 0.01d;
				zSpeed = (0.5d - random.nextDouble()) * 0.15d;
			}

			x = BlockPos.getX(packedPos) + random.nextDouble();
			y = BlockPos.getY(packedPos) + (0.75d - random.nextDouble());
			z = BlockPos.getZ(packedPos) + random.nextDouble();

			level().addAlwaysVisibleParticle(particleoptions, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}

	private void spreadGas(boolean firstTick) {
		if (isRemoved()) return;

		int propagationDuration = getPropagationDuration();
		int tickInterval = tickCount < propagationDuration ? 5 : 20;

		if (!firstTick && tickCount % tickInterval != 0) return;

		float radius = getRadius();
		if (radius < MIN_RADIUS) {
			if (dynamicGasVolume != null) dynamicGasVolume.clear();
			return;
		}

		if (!isEstimatedChunkAreaLoaded(radius * 1.33f)) return;

		if (dynamicGasVolume == null) {
			long seed = uuid.getMostSignificantBits(); //use the same seed on server and client side
			dynamicGasVolume = new DynamicGasVolume(RandomSource.create(seed));
		}

		if (tickCount < propagationDuration) {
			float t = Mth.clamp(tickCount / (float) propagationDuration, 0f, 1f);
			t = t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2f) / 2f; //easeInOutQuad
			dynamicGasVolume.update(level(), blockPosition(), radius, t);
		}
		else {
			dynamicGasVolume.update(level(), blockPosition(), radius, 1f);
		}

		reapplyPosition();
	}

	private boolean isEstimatedChunkAreaLoaded(float estimatedRadius) {
		double x = getX(), y = getY(), z = getZ();
		int minX = Mth.floor(x - estimatedRadius), minY = Mth.floor(y - estimatedRadius), minZ = Mth.floor(z - estimatedRadius);
		int maxX = Mth.floor(x + estimatedRadius), maxY = Mth.floor(y + estimatedRadius), maxZ = Mth.floor(z + estimatedRadius);

		return level().hasChunksAt(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (RADIUS_DATA.equals(key)) {
			refreshDimensions();
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0f + 0.5f;
	}

	@Override
	public void refreshDimensions() {
		EntityDimensions dimensions = getDimensions(Pose.STANDING);

		EntityAccessor accessor = (EntityAccessor) this;
		accessor.biomancy$setDimensions(dimensions);
		accessor.biomancy$setEyeHeight(getEyeHeight(Pose.STANDING, dimensions));

		reapplyPosition();
	}

	@Override
	protected AABB makeBoundingBox() {
		if (dynamicGasVolume == null) {
			return new AABB(blockPosition());
		}

		return dynamicGasVolume.toAABB();
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return EntityDimensions.scalable(getRadius() * 2f, getRadius() * 2f);
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public @Nullable Entity getOwner() {
		if (owner == null && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
			if (serverLevel.getEntity(ownerUUID) instanceof LivingEntity livingEntity) {
				owner = livingEntity;
			}
		}

		return owner;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		ownerUUID = owner == null ? null : owner.getUUID();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag data) {
		tickCount = data.getInt("ticks");
		setPropagationDuration(data.getInt("propagation_ticks"));
		durationTicks = data.getInt("duration_ticks");
		gasInteractionType = GasInteractionType.fromId(data.getByte("interaction_type"));
		effectReapplyCooldown = data.getInt("effect_reapply_cooldown");
		durationModificationPerProc = data.getInt("duration_mod_per_proc");
		radiusModificationPerProc = data.getFloat("radius_mod_per_proc");
		radiusModificationPerTick = data.getFloat("radius_per_tick");
		setRadius(data.getFloat("radius"));

		if (data.contains("effects", Tag.TAG_LIST)) {
			ListTag list = data.getList("effects", Tag.TAG_COMPOUND);
			effects.clear();

			for (int i = 0; i < list.size(); i++) {
				MobEffectInstance effectInstance = MobEffectInstance.load(list.getCompound(i));
				if (effectInstance != null) {
					addEffect(effectInstance);
				}
			}
		}

		ParticleOptions particleOptions = readParticleData(data, "particle");
		if (particleOptions != null) {
			setParticle(particleOptions);
		}

		if (data.hasUUID("owner")) {
			ownerUUID = data.getUUID("owner");
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag data) {
		data.putInt("ticks", tickCount);
		data.putInt("propagation_ticks", getPropagationDuration());
		data.putInt("duration_ticks", durationTicks);
		data.putByte("interaction_type", gasInteractionType.toId());
		data.putInt("effect_reapply_cooldown", effectReapplyCooldown);
		data.putInt("duration_mod_per_proc", durationModificationPerProc);
		data.putFloat("radius_mod_per_proc", radiusModificationPerProc);
		data.putFloat("radius_per_tick", radiusModificationPerTick);
		data.putFloat("radius", getRadius());
		writeParticleData(data, "particle", getParticle());

		if (!effects.isEmpty()) {
			ListTag list = new ListTag();
			for (MobEffectInstance effectInstance : effects) {
				list.add(effectInstance.save(new CompoundTag()));
			}

			data.put("effects", list);
		}

		if (ownerUUID != null) {
			data.putUUID("owner", ownerUUID);
		}
	}

	@Override
	public void renderHitboxInfo(EntityRenderDispatcher renderDispatcher, PoseStack poseStack, MultiBufferSource multiBuffer, int packedLight, float partialTicks) {
		if (dynamicGasVolume == null || dynamicGasVolume.getPackedPositions().isEmpty()) return;

		//		LongSet volume = new LongOpenHashSet(dynamicGasVolume.getPackedPositions());

		double distanceSqr = renderDispatcher.distanceToSqr(this);
		if (distanceSqr >= 64 * 64) return;

		float radius = getRadius();
		int desiredVolume = Mth.floor((4f / 3f) * Mth.PI * (radius * radius * radius * 0.8f));
		int actualVolume = dynamicGasVolume.getPackedPositions().size();

		double xOffset = -Mth.lerp(partialTicks, xOld, getX());
		double yOffset = -Mth.lerp(partialTicks, yOld, getY());
		double zOffset = -Mth.lerp(partialTicks, zOld, getZ());

		poseStack.pushPose();
		AABB aabb = getBoundingBox().move(xOffset, yOffset, zOffset);
		poseStack.translate(aabb.minX + aabb.getXsize() / 2d, aabb.minY + aabb.getYsize(), aabb.minZ + aabb.getZsize() / 2d);
		renderLabel(renderDispatcher, poseStack, multiBuffer, packedLight, "%d / %d".formatted(actualVolume, desiredVolume), 0.25f, 0x00FFFF, false);
		poseStack.popPose();

		//		for (DynamicGasVolume.Voxel voxel : dynamicGasVolume.getVoxels()) {
		//			poseStack.pushPose();
		//			double x = xOffset + voxel.x() + 0.5d;
		//			double y = yOffset + voxel.y() + 0.5d;
		//			double z = zOffset + voxel.z() + 0.5d;
		//			poseStack.translate(x, y, z);
		//			renderLabel(renderDispatcher, poseStack, multiBuffer, packedLight, "%.2f".formatted(voxel.cost()), 0.25f, 0xFFFF00, false);
		//			renderLabel(renderDispatcher, poseStack, multiBuffer, packedLight, "%d".formatted(voxel.depth()), -0.25f, 0xffffff, false);
		//			poseStack.popPose();
		//		}

		for (long packedPos : dynamicGasVolume.getPackedPositions()) {
			int x = BlockPos.getX(packedPos);
			int y = BlockPos.getY(packedPos);
			int z = BlockPos.getZ(packedPos);

			// float green = 0.5f + 0.5f * Mth.clamp((1 - voxel.cost()) / radius * radius, 0f, 1f);
			// float red = voxel.cost() > 1f ? Math.min(1f, 0.125f * voxel.cost()) : 0.125f;
			float red = 0.25f;
			float green = 1f;

			poseStack.pushPose();
			poseStack.translate(xOffset + x, yOffset + y, zOffset + z);
			LevelRenderer.renderLineBox(poseStack, multiBuffer.getBuffer(RenderType.lines()), 0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f, red, green, 0f, 1f);
			poseStack.popPose();
		}
	}

	protected void renderLabel(EntityRenderDispatcher renderDispatcher, PoseStack poseStack, MultiBufferSource multiBuffer, int packedLight, String text, float y, int color, boolean translucentBackground) {
		poseStack.pushPose();

		poseStack.translate(0f, y, 0f);
		poseStack.mulPose(renderDispatcher.cameraOrientation());
		poseStack.scale(-0.025f, -0.025f, 0.025f);

		int backgroundColor = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255f) << 24;
		Font font = Minecraft.getInstance().font;
		float x = -font.width(text) / 2f;

		Matrix4f matrix4f = poseStack.last().pose();
		font.drawInBatch(text, x, y, 0x20_ffffff, false, matrix4f, multiBuffer, translucentBackground ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, backgroundColor, packedLight);
		font.drawInBatch(text, x, y, 0xFF_000000 | color, false, matrix4f, multiBuffer, Font.DisplayMode.NORMAL, 0, packedLight);

		poseStack.popPose();
	}

	public enum GasInteractionType {
		TOUCH((volume, livingEntity) -> volume.intersects(livingEntity.getBoundingBox())),
		INHALATION((volume, livingEntity) -> volume.contains(livingEntity.getEyePosition()));

		private final BiPredicate<DynamicGasVolume, LivingEntity> predicate;

		GasInteractionType(BiPredicate<DynamicGasVolume, LivingEntity> predicate) {
			this.predicate = predicate;
		}

		public static GasInteractionType fromId(byte id) {
			return values()[id];
		}

		public boolean canInteract(DynamicGasVolume gasVolume, LivingEntity livingEntity) {
			return predicate.test(gasVolume, livingEntity);
		}

		public byte toId() {
			return (byte) ordinal();
		}
	}

}
