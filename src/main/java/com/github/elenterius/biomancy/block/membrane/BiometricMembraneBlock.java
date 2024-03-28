package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BiometricMembraneBlock extends MembraneBlock implements EntityBlock {

	public BiometricMembraneBlock(Properties properties) {
		super(properties, IgnoreEntityCollisionPredicate.IS_VALID_FOR_BLOCK_ENTITY_MEMBRANE);
	}

	public static ItemStack createItem(@Nullable EntityType<?> entityType, @Nullable UUID entityUUID, int[] entityColors, boolean isInverted) {
		CompoundTag tag = new CompoundTag();

		if (entityUUID != null) {
			tag.putUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY, entityUUID);
		}
		else if (entityType != null) {
			tag.putString(BiometricMembraneBlockEntity.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());
		}

		if (entityColors.length == 2 && !(entityColors[0] == 0xFFFF_FFFF && entityColors[1] == 0xFFFF_FFFF)) {
			tag.putIntArray(BiometricMembraneBlockEntity.ENTITY_COLORS_KEY, entityColors);
		}

		if (isInverted) {
			tag.putBoolean(BiometricMembraneBlockEntity.IS_INVERTED_KEY, true);
		}

		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put(BiometricMembraneBlockEntity.MEMBRANE_KEY, tag);
		ItemStack stack = new ItemStack(ModItems.BIOMETRIC_MEMBRANE.get());
		BlockItem.setBlockEntityData(stack, ModBlockEntities.BIOMETRIC_MEMBRANE.get(), compoundTag);
		return stack;
	}

	public static int getTintColor(ItemStack stack, int tintIndex) {
		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		if (compoundTag == null || !compoundTag.contains(BiometricMembraneBlockEntity.MEMBRANE_KEY)) return 0xFFFF_FFFF;
		CompoundTag tag = compoundTag.getCompound(BiometricMembraneBlockEntity.MEMBRANE_KEY);

		if (tag.contains(BiometricMembraneBlockEntity.ENTITY_COLORS_KEY, Tag.TAG_INT_ARRAY)) {
			return tag.getIntArray(BiometricMembraneBlockEntity.ENTITY_COLORS_KEY)[tintIndex];
		}

		return 0xFFFF_FFFF;
	}

	public static int getTintColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
		if (level == null || pos == null) return 0xFFFF_FFFF;

		if (level.getBlockEntity(pos) instanceof BiometricMembraneBlockEntity membrane) {
			return membrane.getColors()[tintIndex];
		}

		return 0xFFFF_FFFF;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.BIOMETRIC_MEMBRANE.get().create(pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);

		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		if (compoundTag == null || !compoundTag.contains(BiometricMembraneBlockEntity.MEMBRANE_KEY)) return;
		CompoundTag tag = compoundTag.getCompound(BiometricMembraneBlockEntity.MEMBRANE_KEY);

		if (tag.hasUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY)) {
			UUID entityUUID = tag.getUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY);
			tooltip.add(ComponentUtil.literal("UUID: " + entityUUID).withStyle(ChatFormatting.GRAY));
		}
		else {
			Optional<EntityType<?>> optional = EntityType.byString(tag.getString(BiometricMembraneBlockEntity.ENTITY_TYPE_KEY));
			optional.ifPresent(entityType -> tooltip.add(ComponentUtil.literal("Type: ").append(entityType.getDescription()).withStyle(ChatFormatting.GRAY)));
		}
	}

}
