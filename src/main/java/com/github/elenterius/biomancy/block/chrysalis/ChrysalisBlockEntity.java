package com.github.elenterius.biomancy.block.chrysalis;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChrysalisBlockEntity extends BlockEntity implements Nameable {

	protected static final String CUSTOM_NAME_KEY = "CustomName";

	protected @Nullable Component name;
	private @Nullable CompoundTag entityTag;

	public ChrysalisBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CHRYSALIS.get(), pos, state);
	}

	public boolean isEmpty() {
		return entityTag == null;
	}

	@Override
	public Component getName() {
		return name != null ? name : ModBlocks.CHRYSALIS.get().getName();
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public @Nullable Component getCustomName() {
		return name;
	}

	public void setCustomName(@Nullable Component name) {
		this.name = name;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (name != null) {
			tag.putString(CUSTOM_NAME_KEY, Component.Serializer.toJson(name));
		}
		if (entityTag != null) {
			tag.put(Chrysalis.ENTITY_KEY, entityTag);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(CUSTOM_NAME_KEY, Tag.TAG_STRING)) {
			name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME_KEY));
		}
		if (tag.contains(Chrysalis.ENTITY_KEY, Tag.TAG_COMPOUND)) {
			entityTag = tag.getCompound(Chrysalis.ENTITY_KEY);
		}
	}

}
