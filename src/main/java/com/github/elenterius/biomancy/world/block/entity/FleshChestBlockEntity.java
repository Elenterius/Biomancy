package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.menu.FleshChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class FleshChestBlockEntity extends CustomContainerBlockEntity implements IAnimatable {

	public static final int SLOTS = 9 * 6;

	private final SimpleInventory inventory;
	private boolean isLidOpen = false;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, SoundEvents.CHEST_OPEN);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, SoundEvents.CHEST_CLOSE);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int prevOpenCount, int openCount) {
			level.blockEvent(FleshChestBlockEntity.this.worldPosition, state.getBlock(), 1, openCount);
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof FleshChestMenu menu) {
				Container container = menu.getContainer();
				return container == FleshChestBlockEntity.this.inventory;
			}
			return false;
		}
	};

	public FleshChestBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLESH_CHEST.get(), pos, state);
		inventory = SimpleInventory.createServerContents(SLOTS, this::canPlayerOpenContainer, this::setChanged);
		inventory.setOpenInventoryConsumer(this::startOpen);
		inventory.setCloseInventoryConsumer(this::stopOpen);
	}

	private void startOpen(Player player) {
		if (remove || player.isSpectator()) return;
		openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	private void stopOpen(Player player) {
		if (remove || player.isSpectator()) return;
		openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	public void recheckOpen() {
		if (remove) return;
		openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == 1) {
			isLidOpen = type > 0;
			return true;
		}
		return super.triggerEvent(id, type);
	}

	private void playSound(Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, sound, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
	}

	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "flesh_chest");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return FleshChestMenu.createServerMenu(containerId, playerInventory, inventory);
	}

	public Container getInventory() {
		return inventory;
	}

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

	@Override
	public void dropContainerContents(Level level, BlockPos pos) {
//		Containers.dropContents(level, pos, inventory);
	}

	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
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

	private <E extends BlockEntity & IAnimatable> PlayState handleIdleAnim(AnimationEvent<E> event) {
		if (isLidOpen) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("chest.anim.opened"));
		}
		else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("chest.anim.closed"));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 10, this::handleIdleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
