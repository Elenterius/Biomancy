package com.github.elenterius.biomancy.world.block.fleshkinchest;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.network.ISyncableAnimation;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.ownable.OwnableContainerBlockEntity;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.FleshkinChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public class FleshkinChestBlockEntity extends OwnableContainerBlockEntity implements IAnimatable, ISyncableAnimation {

	public static final int SLOTS = 6 * 7;

	private final BehavioralInventory<?> inventory;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, ModSoundEvents.FLESHKIN_CHEST_OPEN.get());
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, ModSoundEvents.FLESHKIN_CHEST_CLOSE.get());
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int prevOpenCount, int openCount) {
			if (openCount != prevOpenCount) {
				level.blockEvent(FleshkinChestBlockEntity.this.worldPosition, state.getBlock(), 1, openCount);
			}
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof FleshkinChestMenu menu) {
				Container container = menu.getContainer();
				return container == FleshkinChestBlockEntity.this.inventory;
			}
			return false;
		}
	};
	private boolean lidShouldBeOpen = false;
	private boolean playAttackAnimation = false;
	private boolean lidIsOpen = false;

	public FleshkinChestBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLESHKIN_CHEST.get(), pos, state);
		inventory = BehavioralInventory.createServerContents(SLOTS, HandlerBehaviors::denyItemWithFilledInventory, this::canPlayerOpenContainer, this::setChanged);
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
			lidShouldBeOpen = type > 0;
			return true;
		}
		return super.triggerEvent(id, type);
	}

	public void attack(Direction direction, @Nullable LivingEntity target) {
		if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
			if (target != null) target.hurt(ModDamageSources.CHEST_BITE, 4f);
			attackAtPosition(serverLevel, getBlockPos().relative(direction), target);
		}
	}

	protected void attackAtPosition(ServerLevel level, BlockPos pos, @Nullable LivingEntity excludedEntity) {
		ModNetworkHandler.sendAnimationToClients(this, 0, 0);

		AABB aabb = new AABB(pos).inflate(0.25f);
		List<Entity> victims = level.getEntities(excludedEntity, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
		for (Entity entity : victims) {
			entity.hurt(ModDamageSources.CHEST_BITE, 2f);
		}
	}

	private void playSound(Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, sound, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
	}

	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "fleshkin_chest");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return FleshkinChestMenu.createServerMenu(containerId, playerInventory, inventory);
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
		//		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
		//			return inventory.getOptionalItemHandler().cast();
		//		}
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

	private PlayState handleIdleAnim(AnimationEvent<FleshkinChestBlockEntity> event) {
		if (playAttackAnimation) {
			event.getController().setAnimation(Animations.BITE);
			if (event.getController().getAnimationState() != AnimationState.Stopped) return PlayState.CONTINUE;
			stopAttackAnimation();
		}

		if (lidShouldBeOpen) {
			event.getController().setAnimation(Animations.OPENING);
			lidIsOpen = true;
			return PlayState.CONTINUE;
		}
		else if (lidIsOpen) {
			event.getController().setAnimation(Animations.CLOSING);
			if (event.getController().getAnimationState() != AnimationState.Stopped) return PlayState.CONTINUE;
			lidIsOpen = false;
		}

		event.getController().setAnimation(Animations.CLOSED);
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleIdleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	protected static class Animations {
		protected static final AnimationBuilder BITE = new AnimationBuilder().addAnimation("fleshkin_chest.bite");
		protected static final AnimationBuilder OPENING = new AnimationBuilder().addAnimation("fleshkin_chest.open").addAnimation("fleshkin_chest.opened");
		protected static final AnimationBuilder CLOSING = new AnimationBuilder().addAnimation("fleshkin_chest.close");
		protected static final AnimationBuilder CLOSED = new AnimationBuilder().addAnimation("fleshkin_chest.closed");

		private Animations() {}
	}

}
