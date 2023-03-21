package com.github.elenterius.biomancy.world.block.modularlarynx;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.util.MobSoundUtil;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.item.EssenceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

@Deprecated
public class VoiceBoxBlockEntity extends BlockEntity {

	public static final Predicate<ItemStack> VALID_ITEM = stack -> stack.getItem() instanceof EssenceItem;

	private final BehavioralInventory<?> inventory;

	public VoiceBoxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.VOICE_BOX.get(), pos, state);
		inventory = BehavioralInventory.createServerContents(1, ish -> HandlerBehaviors.filterInput(ish, VALID_ITEM), player -> false, this::setChanged);
	}

	public ItemStack getStoredItemStack() {
		return inventory.getItem(0);
	}

	public void setStoredItemStack(ItemStack stack) {
		inventory.setItem(0, stack);
		setChanged();
	}

	public boolean playVoice(float volume, float pitch) {
		if (level == null || level.isClientSide) return false;

		ItemStack stack = inventory.getItem(0);
		if (!stack.isEmpty() && VALID_ITEM.test(stack)) {
			BlockPos pos = getBlockPos();
			double x = pos.getX() + 0.5d;
			double y = pos.getY() + 0.5d;
			double z = pos.getZ() + 0.5d;
			return playVoice(stack, level, x, y, z, volume, pitch);
		}
		return false;
	}

	public boolean playVoice(ItemStack stack, Level level, double x, double y, double z, float volume, float pitch) {
		CompoundTag tag = stack.getOrCreateTag();
		MobSoundUtil.VoiceType voice = MobSoundUtil.VoiceType.deserialize(tag);
		SoundEvent soundEvent = voice.getSound(tag);
		if (soundEvent != null) {
			level.playSound(null, x, y, z, soundEvent, SoundSource.RECORDS, volume, pitch);
			return true;
		}
		return false;
	}

	//	private void playVoice(ItemStack stack, Level level, double x, double y, double z) {
//		CompoundTag tag = stack.getOrCreateTag();
//		if (!playVoice(stack, level, x, y, z, MobSoundUtil.VoiceType.getVolume(tag), MobSoundUtil.VoiceType.getPitch(tag))) {
//			level.playSound(null, x, y, z, SoundEvents.PLAYER_BREATH, SoundSource.RECORDS, 2f, 1f);
//		}
//	}
//
//	public boolean playVoice(ItemStack stack, Level level, double x, double y, double z, float volume, float pitch, MobSoundUtil.VoiceType voiceType) {
//		CompoundTag tag = stack.getOrCreateTag();
//		SoundEvent soundEvent = voiceType.getSound(tag);
//		if (soundEvent != null) {
//			level.playSound(null, x, y, z, soundEvent, SoundSource.RECORDS, volume, pitch);
//			return true;
//		}
//		return false;
//	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}

	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, inventory);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		inventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		inventory.revive();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			return inventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

}
