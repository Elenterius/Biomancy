package com.github.elenterius.biomancy.world.block.bioforge;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeMenu;
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
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BioForgeBlockEntity extends BlockEntity implements MenuProvider, Nameable, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int MAX_FUEL = 1_000;
	static final int OPENERS_CHANGE_EVENT = 1;
	protected final int tickOffset = BiomancyMod.GLOBAL_RANDOM.nextInt(20);
	private final BioForgeStateData stateData = new BioForgeStateData();
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
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	protected int ticks = tickOffset;
	private boolean playWorkingAnimation = false;
	private int nearbyTimer = -10;

	public BioForgeBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_FORGE.get(), worldPosition, blockState);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		fuelInventory.setOpenInventoryConsumer(this::startOpen);
		fuelInventory.setCloseInventoryConsumer(this::stopOpen);
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

	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	public void setFuelAmount(int newAmount) {
		stateData.setFuelAmount((short) newAmount);
	}

	protected void serverTick(ServerLevel level) {
		ticks++;
		if (ticks % 8 == 0) {
			refuel();
		}
	}

	public boolean isItemValidFuel(ItemStack stack) {
		return NutrientFuelUtil.isValidFuel(stack);
	}

	public float getItemFuelValue(ItemStack stack) {
		return NutrientFuelUtil.getFuelValue(stack);
	}

	public void refuel() {
		if (getFuelAmount() < getMaxFuelAmount()) {
			ItemStack stack = getStackInFuelSlot();
			if (isItemValidFuel(stack)) {
				ItemStack remainder = addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					setStackInFuelSlot(remainder);
					setChanged();
				}
			}
		}
	}

	public ItemStack addFuel(ItemStack stackIn) {
		if (level == null || level.isClientSide()) return stackIn;

		if (!stackIn.isEmpty() && getFuelAmount() < getMaxFuelAmount()) {
			float itemFuelValue = getItemFuelValue(stackIn);
			if (itemFuelValue <= 0f) return stackIn;

			int itemsNeeded = Mth.floor(Math.max(0, getMaxFuelAmount() - getFuelAmount()) / itemFuelValue);
			int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
			if (consumeAmount > 0) {
				short newFuel = (short) Mth.clamp(getFuelAmount() + itemFuelValue * consumeAmount, 0, getMaxFuelAmount());
				setFuelAmount(newFuel);
				return ItemHandlerHelper.copyStackWithSize(stackIn, stackIn.getCount() - consumeAmount);
			}
		}
		return stackIn;
	}

	public void addFuelAmount(int addAmount) {
		stateData.setFuelAmount((short) (stateData.getFuelAmount() + addAmount));
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
		stateData.serialize(tag);
		tag.put("FuelSlots", fuelInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
	}

	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, fuelInventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			if (side != null && side.getAxis().isHorizontal()) return fuelInventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fuelInventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
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

	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {

		if (event.getController().getCurrentAnimation() == null) { //set default start animation
			event.getController().setAnimation(Animations.FOLDED);
			event.getController().transitionLengthTicks = 0;
			return PlayState.CONTINUE;
		}

		if (playWorkingAnimation) {
			event.getController().setAnimation(Animations.WORKING);
			event.getController().transitionLengthTicks = 10;
			return PlayState.CONTINUE;
		}

		if (nearbyTimer > 0) {
			boolean isUnfoldedOrWorking = Animations.isUnfoldedOrWorking(event.getController());
			event.getController().setAnimation(isUnfoldedOrWorking ? Animations.UNFOLDED : Animations.UNFOLDING);
			event.getController().transitionLengthTicks = 10;
		}
		else {
			if (!Animations.isFolded(event.getController())) {
				event.getController().setAnimation(Animations.FOLDING);
				event.getController().transitionLengthTicks = 10;
			}
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 10, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	protected static class Animations {
		protected static final AnimationBuilder UNFOLDING = new AnimationBuilder().playOnce("bio_forge.unfold").loop("bio_forge.idle");
		protected static final AnimationBuilder UNFOLDED = new AnimationBuilder().loop("bio_forge.idle");
		protected static final AnimationBuilder FOLDING = new AnimationBuilder().playOnce("bio_forge.fold").loop("bio_forge.folded_state");
		protected static final AnimationBuilder FOLDED = new AnimationBuilder().loop("bio_forge.folded_state");
		protected static final AnimationBuilder WORKING = new AnimationBuilder().loop("bio_forge.working");

		private Animations() {}

		protected static boolean isUnfoldedOrWorking(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) return false;
			return animation.animationName.equals("bio_forge.idle") || animation.animationName.equals("bio_forge.working");
		}

		protected static boolean isFolded(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) return false;
			return animation.animationName.equals("bio_forge.folded_state");
		}
	}

}
