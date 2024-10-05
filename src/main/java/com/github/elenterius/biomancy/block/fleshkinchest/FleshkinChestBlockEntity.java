package com.github.elenterius.biomancy.block.fleshkinchest;

import com.github.elenterius.biomancy.block.ownable.OwnableContainerBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.inventory.ItemHandlerUtil;
import com.github.elenterius.biomancy.menu.FleshkinChestMenu;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class FleshkinChestBlockEntity extends OwnableContainerBlockEntity implements GeoBlockEntity {

	public static final int SLOTS = 6 * 7;

	private final InventoryHandler inventory;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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
				return menu.getInventory() == FleshkinChestBlockEntity.this.inventory;
			}
			return false;
		}
	};

	private boolean lidShouldBeOpen = false;
	private boolean lidIsOpen = false;

	public FleshkinChestBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLESHKIN_CHEST.get(), pos, state);

		inventory = InventoryHandlers.standard(SLOTS, this::onInventoryChanged);
	}

	protected void onInventoryChanged() {
		if (level != null && !level.isClientSide) setChanged();
	}

	public void startOpen(Player player) {
		if (isRemoved() || player.isSpectator()) return;
		openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	public void stopOpen(Player player) {
		if (isRemoved() || player.isSpectator()) return;
		openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	public void recheckOpen() {
		if (isRemoved()) return;
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
			BlockPos pos = getBlockPos();
			if (target != null) {
				target.hurt(ModDamageSources.chestBite(level, pos), 4f);
			}
			attackAtPosition(serverLevel, pos.relative(direction), target);
		}
	}

	protected void attackAtPosition(ServerLevel level, BlockPos pos, @Nullable LivingEntity excludedEntity) {
		broadcastAnimation(Animations.BITE);

		AABB aabb = new AABB(pos).inflate(0.25f);
		List<Entity> victims = level.getEntities(excludedEntity, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
		for (Entity entity : victims) {
			entity.hurt(ModDamageSources.chestBite(level, pos), 2f);
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
		return FleshkinChestMenu.createServerMenu(containerId, playerInventory, this);
	}

	public InventoryHandler getInventory() {
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
		ItemHandlerUtil.dropContents(level, pos, inventory);
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

	protected void broadcastAnimation(TriggerableAnimation animation) {
		triggerAnim(animation.controller(), animation.name());
	}

	private <T extends FleshkinChestBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {

		if (lidShouldBeOpen) {
			state.setAnimation(Animations.OPENING);
			lidIsOpen = true;
			return PlayState.CONTINUE;
		}
		else if (lidIsOpen) {
			state.setAnimation(Animations.CLOSING);
			if (state.getController().getAnimationState() != AnimationController.State.STOPPED) return PlayState.CONTINUE;
			lidIsOpen = false;
		}

		if (!state.isCurrentAnimation(Animations.BITE.rawAnimation())) {
			state.getController().setAnimation(Animations.CLOSED);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<FleshkinChestBlockEntity> controller = new AnimationController<>(this, Animations.MAIN_CONTROLLER, 0, this::handleAnimationState);
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

		static final TriggerableAnimation BITE = register(MAIN_CONTROLLER, "bite", RawAnimation.begin().thenPlay("fleshkin_chest.bite"));
		static final RawAnimation OPENING = RawAnimation.begin().thenPlay("fleshkin_chest.open").thenPlay("fleshkin_chest.opened");
		static final RawAnimation CLOSING = RawAnimation.begin().thenPlay("fleshkin_chest.close");
		static final RawAnimation CLOSED = RawAnimation.begin().thenPlay("fleshkin_chest.closed");

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
