package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.google.common.collect.ImmutableSet;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PlayMessages;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class ProjectileEntityType<T extends BaseProjectile> extends EntityType<T> {

	// all these default values are derived from vanilla arrow
	public static final float DEFAULT_AIR_DRAG = 0.01f;
	public static final float DEFAULT_WATER_DRAG = 0.4f;
	public static final float DEFAULT_GRAVITY = 0.05f;

	private final float airDrag;
	private final float waterDrag;
	private final float gravity;

	private ProjectileEntityType(
			EntityType.EntityFactory<T> factory, boolean serialize, boolean summon,
			boolean fireImmune, ImmutableSet<Block> immuneTo,
			EntityDimensions dimensions,
			int clientTrackingRange, int updateInterval, FeatureFlagSet requiredFeatures,
			final Predicate<EntityType<?>> velocityUpdateSupplier, final ToIntFunction<EntityType<?>> trackingRangeSupplier, final ToIntFunction<EntityType<?>> updateIntervalSupplier,
			@Nullable final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory,
			float airDrag, float waterDrag, float gravity
	) {
		super(factory, MobCategory.MISC, serialize, summon, fireImmune, true, immuneTo, dimensions, clientTrackingRange, updateInterval, requiredFeatures, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
		if (airDrag < 0f || airDrag > 1f) throw new IllegalArgumentException("Drag is outside of valid range");
		if (waterDrag < 0f || waterDrag > 1f) throw new IllegalArgumentException("WaterDrag is outside of valid range");

		this.airDrag = airDrag;
		this.waterDrag = waterDrag;
		this.gravity = gravity;
	}

	public float getAirDrag() {
		return airDrag;
	}

	public float getWaterDrag() {
		return waterDrag;
	}

	public float getGravity() {
		return gravity;
	}

	public interface Factory<T extends BaseProjectile> {
		T create(ProjectileEntityType<T> entityType, Level level);
	}

	public static class Builder<T extends BaseProjectile> {
		public static final Predicate<EntityType<?>> DEFAULT_UPDATE_PREDICATE = entityType -> true;

		private static final int DEFAULT_TRACKING_RANGE = 5;
		private static final ToIntFunction<EntityType<?>> DEFAULT_TRACKING_RANGE_FUNC = entityType -> DEFAULT_TRACKING_RANGE;

		private static final int DEFAULT_UPDATE_INTERVAL = 3;
		private static final ToIntFunction<EntityType<?>> DEFAULT_UPDATE_INTERVAL_FUNC = entityType -> DEFAULT_UPDATE_INTERVAL;

		private final EntityType.EntityFactory<T> factory;
		private @Nullable BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;
		private boolean serialize = true;
		private boolean summon = true;

		private int clientTrackingRange = DEFAULT_TRACKING_RANGE;
		private int updateInterval = DEFAULT_UPDATE_INTERVAL;
		private Predicate<EntityType<?>> velocityUpdateSupplier = DEFAULT_UPDATE_PREDICATE;
		private ToIntFunction<EntityType<?>> trackingRangeSupplier = DEFAULT_TRACKING_RANGE_FUNC;
		private ToIntFunction<EntityType<?>> updateIntervalSupplier = DEFAULT_UPDATE_INTERVAL_FUNC;

		private boolean fireImmune;
		private ImmutableSet<Block> immuneTo = ImmutableSet.of();

		private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);

		private float airDrag = DEFAULT_AIR_DRAG;
		private float waterDrag = DEFAULT_WATER_DRAG;
		private float gravity = DEFAULT_GRAVITY;

		private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

		private Builder(EntityType.EntityFactory<T> factory) {
			this.factory = factory;
		}

		public static <T extends BaseProjectile> Builder<T> of(Factory<T> factory) {
			return new Builder<>((entityType, level) -> factory.create((ProjectileEntityType<T>) entityType, level));
		}

		public Builder<T> sized(float width, float height) {
			this.dimensions = EntityDimensions.scalable(width, height);
			return this;
		}

		public Builder<T> noSummon() {
			this.summon = false;
			return this;
		}

		public Builder<T> noSave() {
			this.serialize = false;
			return this;
		}

		public Builder<T> fireImmune() {
			this.fireImmune = true;
			return this;
		}

		public Builder<T> immuneTo(Block... blocks) {
			this.immuneTo = ImmutableSet.copyOf(blocks);
			return this;
		}

		public Builder<T> requiredFeatures(FeatureFlag... requiredFeatures) {
			this.requiredFeatures = FeatureFlags.REGISTRY.subset(requiredFeatures);
			return this;
		}

		public Builder<T> updateInterval(int interval) {
			updateInterval = interval;
			updateIntervalSupplier = t -> interval;
			return this;
		}

		public Builder<T> trackingRange(int range) {
			clientTrackingRange = range;
			trackingRangeSupplier = t -> range;
			return this;
		}

		public Builder<T> shouldReceiveVelocityUpdates(boolean value) {
			velocityUpdateSupplier = t -> value;
			return this;
		}

		/// By default, entities are spawned clientside via [EntityType#create(Level)].
		/// If you need finer control over the spawning process, use this to get read access to the spawn packet.
		public Builder<T> customClientFactory(BiFunction<PlayMessages.SpawnEntity, Level, T> factory) {
			customClientFactory = factory;
			return this;
		}

		public Builder<T> airDrag(float airDrag) {
			this.airDrag = airDrag;
			return this;
		}

		public Builder<T> waterDrag(float waterDrag) {
			this.waterDrag = waterDrag;
			return this;
		}

		public Builder<T> gravity(float gravity) {
			this.gravity = gravity;
			return this;
		}

		public ProjectileEntityType<T> build(String key) {
			if (serialize) {
				Util.fetchChoiceType(References.ENTITY_TREE, key);
			}

			return new ProjectileEntityType<>(
					factory, serialize, summon,
					fireImmune, immuneTo, dimensions,
					clientTrackingRange, updateInterval, requiredFeatures,
					velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory,
					airDrag, waterDrag, gravity
			);
		}
	}

}
