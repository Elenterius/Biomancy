package com.github.elenterius.biomancy.block.bioforge;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.menu.BioForgeMenu;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.fuel.FluidFuelConsumerHandler;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BioForgeBlockEntity extends BlockEntity implements MenuProvider, Nameable, GeoBlockEntity {

	public static final int FUEL_SLOTS = 1;
	public static final int MAX_FUEL = 1_000;
	static final int OPENERS_CHANGE_EVENT = 1;
	protected final int tickOffset = BiomancyMod.GLOBAL_RANDOM.nextInt(20);
	private final BioForgeStateData stateData;
	private final FuelHandler fuelHandler;
	private final BehavioralInventory<?> fuelInventory;

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {

		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			//play sound
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			//play sound
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int prevOpenCount, int openCount) {
			if (openCount != prevOpenCount) {
				level.blockEvent(BioForgeBlockEntity.this.worldPosition, state.getBlock(), OPENERS_CHANGE_EVENT, openCount);
			}
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof BioForgeMenu menu) {
				return menu.getStateData() == stateData;
			}
			return false;
		}
	};
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	protected int ticks = tickOffset;
	private boolean playWorkingAnimation = false;
	private int nearbyTimer = -10;

	private LazyOptional<IFluidHandler> optionalFluidConsumer;

	public BioForgeBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_FORGE.get(), worldPosition, blockState);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		fuelInventory.setOpenInventoryConsumer(this::startOpen);
		fuelInventory.setCloseInventoryConsumer(this::stopOpen);

		fuelHandler = FuelHandler.createNutrientFuelHandler(MAX_FUEL, this::setChanged);
		stateData = new BioForgeStateData(fuelHandler);
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BioForgeBlockEntity entity) {
		entity.serverTick((ServerLevel) level);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, BioForgeBlockEntity entity) {
		entity.clientTick(level);
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == OPENERS_CHANGE_EVENT) {
			playWorkingAnimation = type > 0;
			return true;
		}
		return super.triggerEvent(id, type);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	private void startOpen(Player player) {
		if (remove || player.isSpectator()) return;
		openersCounter.incrementOpeners(player, Objects.requireNonNull(getLevel()), getBlockPos(), getBlockState());
	}

	private void stopOpen(Player player) {
		if (remove || player.isSpectator()) return;
		openersCounter.decrementOpeners(player, Objects.requireNonNull(getLevel()), getBlockPos(), getBlockState());
	}

	public void recheckOpen() {
		if (remove) return;
		openersCounter.recheckOpeners(Objects.requireNonNull(getLevel()), getBlockPos(), getBlockState());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return BioForgeMenu.createServerMenu(containerId, playerInventory, fuelInventory, stateData, ContainerLevelAccess.create(level, getBlockPos()));
	}

	public BioForgeStateData getStateData() {
		return stateData;
	}

	protected void serverTick(ServerLevel level) {
		ticks++;
		if (ticks % 8 == 0) {
			refuel();
		}
	}

	protected IFuelHandler getFuelHandler() {
		return fuelHandler;
	}

	public void refuel() {
		if (getFuelHandler().getFuelAmount() < getFuelHandler().getMaxFuelAmount()) {
			ItemStack stack = getStackInFuelSlot();
			if (getFuelHandler().isValidFuel(stack)) {
				ItemStack remainder = getFuelHandler().addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					setStackInFuelSlot(remainder);
					setChanged();
				}
			}
		}
	}

	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getItem(0);
	}

	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setItem(0, stack);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Fuel", fuelHandler.serializeNBT());
		tag.put("FuelSlots", fuelInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		fuelHandler.deserializeNBT(tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
	}

	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, fuelInventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (remove) return super.getCapability(cap, side);

		if (cap == ModCapabilities.ITEM_HANDLER && side != null && side.getAxis().isHorizontal()) {
			return fuelInventory.getOptionalItemHandler().cast();
		}

		if (cap == ModCapabilities.FLUID_HANDLER) {
			return optionalFluidConsumer.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fuelInventory.invalidate();
		optionalFluidConsumer.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", "bio_forge");
	}

	protected void clientTick(Level level) {
		if (playWorkingAnimation) return;

		BlockPos pos = getBlockPos();
		Player player = level.getNearestPlayer(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 4.5d, false);
		nearbyTimer = Mth.clamp(nearbyTimer + (player != null ? 1 : -1), -10, 10);
	}

	private <T extends BioForgeBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {

		if (playWorkingAnimation) {
			return state.setAndContinue(Animations.WORKING);
		}

		if (nearbyTimer > 0) {
			if (state.isCurrentAnimation(Animations.WORKING)) return state.setAndContinue(Animations.UNFOLDED);

			if (!state.isCurrentAnimation(Animations.UNFOLDED) && !state.isCurrentAnimation(Animations.UNFOLDING)) {
				return state.setAndContinue(Animations.UNFOLDING);
			}

			return state.setAndContinue(Animations.UNFOLDED);
		}
		else {
			if (!state.isCurrentAnimation(Animations.FOLDED) && !state.isCurrentAnimation(Animations.FOLDING)) {
				return state.setAndContinue(Animations.FOLDING);
			}

			return state.setAndContinue(Animations.FOLDED);
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<BioForgeBlockEntity> controller = new AnimationController<>(this, Animations.MAIN_CONTROLLER, 10, this::handleAnimationState);
		controller.setAnimation(Animations.FOLDED);
		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static class Animations {
		protected static final String MAIN_CONTROLLER = "main";
		protected static final RawAnimation UNFOLDING = RawAnimation.begin().thenPlay("bio_forge.unfold").thenLoop("bio_forge.idle");
		protected static final RawAnimation UNFOLDED = RawAnimation.begin().thenLoop("bio_forge.idle");
		protected static final RawAnimation FOLDING = RawAnimation.begin().thenPlay("bio_forge.fold").thenLoop("bio_forge.folded_state");
		protected static final RawAnimation FOLDED = RawAnimation.begin().thenLoop("bio_forge.folded_state");
		protected static final RawAnimation WORKING = RawAnimation.begin().thenLoop("bio_forge.working");

		private Animations() {}

	}

}
