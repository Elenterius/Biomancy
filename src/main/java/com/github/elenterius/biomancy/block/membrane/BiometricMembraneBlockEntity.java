package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.block.base.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.item.EssenceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BiometricMembraneBlockEntity extends SimpleSyncedBlockEntity implements Membrane {

	public static final String MEMBRANE_KEY = "membrane";
	public static final String ENTITY_TYPE_KEY = "entity_type";
	public static final String ENTITY_UUID_KEY = "entity_uuid";
	public static final String ENTITY_COLORS_KEY = "entity_colors";
	public static final String IS_INVERTED_KEY = "is_inverted";
	public static final int[] DEFAULT_COLORS = {0xffff_ffff, 0xffff_ffff};

	private @Nullable EntityType<?> entityType;
	private @Nullable UUID entityUUID = null;
	private int[] entityColors = DEFAULT_COLORS;
	private boolean isInverted = false;

	public BiometricMembraneBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BIOMETRIC_MEMBRANE.get(), pos, state);
		reRenderBlockOnSync = true;
	}

	public void setEntityFilter(LivingEntity entity) {
		this.entityType = entity.getType();
		entityUUID = entity.getUUID();
		entityColors = EssenceItem.getEssenceColors(entity, 3);

		setChanged();
		syncToClient();
	}

	public void setEntityFilter(EntityType<?> entityType) {
		this.entityType = entityType;
		entityColors = EssenceItem.getEssenceColors(entityType);

		setChanged();
		syncToClient();
	}

	public boolean setEntityFilter(ItemStack stack, EssenceItem essenceItem) {
		Optional<EntityType<?>> optional = essenceItem.getEntityType(stack);
		if (optional.isEmpty()) return false;

		entityType = optional.orElse(null);
		entityUUID = essenceItem.getEntityUUID(stack).orElse(null);
		entityColors = essenceItem.getColors(stack);

		setChanged();
		syncToClient();

		return true;
	}

	public void invertFilters() {
		isInverted = !isInverted;

		setChanged();
		syncToClient();
	}

	@Override
	public boolean shouldIgnoreEntityCollisionAt(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {

		if (entityUUID != null) {
			boolean isSameEntityUUID = entity.getUUID().equals(entityUUID);
			return isInverted != isSameEntityUUID;
		}

		if (entityType != null) {
			boolean isSameEntityType = entity.getType() == entityType;
			return isInverted != isSameEntityType;
		}

		return false;
	}

	@Override
	protected void saveForSyncToClient(CompoundTag compoundTag) {
		save(compoundTag);
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);
		save(compoundTag);
	}

	protected void save(CompoundTag compoundTag) {
		CompoundTag tag = new CompoundTag();

		if (entityType != null) {
			tag.putString(ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());
		}

		if (entityUUID != null) {
			tag.putUUID(ENTITY_UUID_KEY, entityUUID);
		}

		if (entityColors != DEFAULT_COLORS && !(entityColors[0] == 0xFFFF_FFFF && entityColors[1] == 0xFFFF_FFFF)) {
			tag.putIntArray(ENTITY_COLORS_KEY, entityColors);
		}

		if (isInverted) tag.putBoolean(IS_INVERTED_KEY, true);

		compoundTag.put(MEMBRANE_KEY, tag);
	}

	@Override
	public void load(CompoundTag compoundTag) {
		super.load(compoundTag);

		CompoundTag tag = compoundTag.getCompound(MEMBRANE_KEY);

		entityType = EntityType.byString(tag.getString(ENTITY_TYPE_KEY)).orElse(null);
		entityUUID = tag.hasUUID(ENTITY_UUID_KEY) ? tag.getUUID(ENTITY_UUID_KEY) : null;
		entityColors = tag.contains(ENTITY_COLORS_KEY, Tag.TAG_INT_ARRAY) ? tag.getIntArray(ENTITY_COLORS_KEY) : DEFAULT_COLORS;
		isInverted = tag.getBoolean(IS_INVERTED_KEY);
	}

	public int[] getColors() {
		return entityColors;
	}

}
