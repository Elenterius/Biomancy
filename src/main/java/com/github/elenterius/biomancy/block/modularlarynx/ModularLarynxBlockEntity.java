package com.github.elenterius.biomancy.block.modularlarynx;

import com.github.elenterius.biomancy.block.property.MobSoundType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.inventory.itemhandler.SingleItemStackHandler;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.util.MobSoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class ModularLarynxBlockEntity extends BlockEntity {

	public static final String INVENTORY_TAG = "inventory";
	public static final String SOUND_EVENT_TAG = "sound_event";

	public static final Predicate<ItemStack> VALID_ITEM = stack -> stack.getItem() instanceof EssenceItem;

	private final SingleItemStackHandler inventory;
	private LazyOptional<IItemHandler> optionalItemHandler;

	private SoundEvent soundEvent;

	public ModularLarynxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MODULAR_LARYNX.get(), pos, state);
		inventory = new SingleItemStackHandler() {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			public boolean isItemValid(@NotNull ItemStack stack) {
				return VALID_ITEM.test(stack);
			}

			@Override
			protected void onContentsChanged() {
				updateSounds();
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> inventory);

		soundEvent = MobSoundUtil.getSoundFallbackFor(ModularLarynxBlock.getMobSoundType(state));
	}

	public boolean isInventoryEmpty() {
		return inventory.isEmpty();
	}

	public ItemStack insertItemStack(ItemStack stack) {
		return inventory.insertItem(stack, false);
	}

	protected void updateSounds() {
		ItemStack stack = inventory.getStack();
		MobSoundType mobSoundType = ModularLarynxBlock.getMobSoundType(getBlockState());

		if (stack.getItem() instanceof EssenceItem essenceItem) {
			soundEvent = essenceItem
					.getMobSound(stack, mobSoundType)
					.orElse(MobSoundUtil.getSoundFallbackFor(mobSoundType));
		}
		else {
			soundEvent = MobSoundUtil.getSoundFallbackFor(mobSoundType);
		}
	}

	public boolean playSound(float volume, float pitch) {
		if (level == null || level.isClientSide) return false;

		BlockPos pos = getBlockPos();
		double x = pos.getX() + 0.5d;
		double y = pos.getY() + 0.5d;
		double z = pos.getZ() + 0.5d;
		level.playSound(null, x, y, z, soundEvent, SoundSource.RECORDS, volume, pitch);

		return true;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(INVENTORY_TAG, inventory.serializeNBT());
		tag.putString(SOUND_EVENT_TAG, soundEvent.getLocation().toString());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound(INVENTORY_TAG));
		soundEvent = deserializeSoundEvent(tag.getString(SOUND_EVENT_TAG)).orElseGet(() -> MobSoundUtil.getSoundFallbackFor(ModularLarynxBlock.getMobSoundType(getBlockState())));
	}

	public Optional<SoundEvent> deserializeSoundEvent(String stringKey) {
		ResourceLocation key = ResourceLocation.tryParse(stringKey);
		if (key != null) {
			return Optional.ofNullable(ForgeRegistries.SOUND_EVENTS.getValue(key));
		}
		return Optional.empty();
	}

	public void dropInventoryContents(Level level, BlockPos pos) {
		ItemStack stack = inventory.extractItem(inventory.getMaxAmount(), false);
		if (!stack.isEmpty()) {
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
		}
	}

	public void giveInventoryContentsTo(Level level, BlockPos pos, Player player) {
		ItemStack stack = inventory.extractItem(inventory.getMaxAmount(), false);
		if (!stack.isEmpty() && !player.addItem(stack)) {
			player.drop(stack, false);
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		optionalItemHandler.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		optionalItemHandler = LazyOptional.of(() -> inventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove) {
			return ModCapabilities.ITEM_HANDLER.orEmpty(cap, optionalItemHandler);
		}
		return super.getCapability(cap, side);
	}

}
