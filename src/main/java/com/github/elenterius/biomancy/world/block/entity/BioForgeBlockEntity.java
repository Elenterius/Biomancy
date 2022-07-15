package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import com.github.elenterius.biomancy.world.block.entity.state.BioForgeStateData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;

public class BioForgeBlockEntity extends BlockEntity implements MenuProvider, Nameable, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int MAX_FUEL = 1_000;
	public static final RecipeTypeImpl.ItemStackRecipeType<BioForgeRecipe> RECIPE_TYPE = ModRecipes.BIO_FORGING_RECIPE_TYPE;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	private final BioForgeStateData stateData = new BioForgeStateData();
	private final BehavioralInventory<?> fuelInventory;

	public BioForgeBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_FORGE.get(), worldPosition, blockState);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return BioForgeMenu.createServerMenu(containerId, playerInventory, fuelInventory, stateData);
	}

	public BioForgeStateData getStateData() {
		return stateData;
	}

	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	protected final int tickOffset = BiomancyMod.GLOBAL_RANDOM.nextInt(20);
	protected int ticks = tickOffset;

	public static void serverTick(Level level, BlockPos pos, BlockState state, BioForgeBlockEntity entity) {
		entity.serverTick((ServerLevel) level);
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

	public void setFuelAmount(int newAmount) {
		stateData.setFuelAmount((short) newAmount);
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
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("placeholder.anim.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
