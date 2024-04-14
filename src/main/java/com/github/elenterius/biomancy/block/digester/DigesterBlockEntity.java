package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.block.base.MachineBlock;
import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.client.util.ClientLoopingSoundHelper;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.menu.DigesterMenu;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ILoopingSoundHelper;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.FluidFuelConsumerHandler;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

import java.util.Objects;

public class DigesterBlockEntity extends MachineBlockEntity<DigestingRecipe, Container, DigesterStateData> implements MenuProvider, GeoBlockEntity {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 2;

	public static final int MAX_FUEL = 1_000;

	public static final RegistryObject<SimpleRecipeType.ItemStackRecipeType<DigestingRecipe>> RECIPE_TYPE = ModRecipes.DIGESTING_RECIPE_TYPE;
	protected static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("digester.working");
	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("digester.idle");

	private final DigesterStateData stateData;
	private final FuelHandler fuelHandler;
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private ILoopingSoundHelper loopingSoundHelper = ILoopingSoundHelper.NULL;

	private LazyOptional<IFluidHandler> optionalFluidConsumer;

	public DigesterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DIGESTER.get(), pos, state);
		inputInventory = BehavioralInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);

		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		fuelHandler = FuelHandler.createNutrientFuelHandler(MAX_FUEL, this::setChanged);

		stateData = new DigesterStateData(fuelHandler);
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	@Override
	public void onLoad() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper = new ClientLoopingSoundHelper();
		}
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", Objects.requireNonNull(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(getType())).getPath());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DigesterMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected DigesterStateData getStateData() {
		return stateData;
	}

	@Override
	protected Container getInputInventory() {
		return inputInventory;
	}

	@Override
	protected IFuelHandler getFuelHandler() {
		return fuelHandler;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getItem(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setItem(0, stack);
	}

	@Override
	protected boolean doesRecipeResultFitIntoOutputInv(DigestingRecipe craftingGoal, ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(stackToCraft);
	}

	@Override
	protected @Nullable DigestingRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.get().getRecipeFromContainer(level, inputInventory).orElse(null);
	}

	@Override
	protected boolean doesRecipeMatchInput(DigestingRecipe recipeToTest, Level level) {
		return recipeToTest.matches(inputInventory, level);
	}

	@Override
	protected boolean craftRecipe(DigestingRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.assemble(inputInventory, level.registryAccess());

		if (!result.isEmpty() && doesRecipeResultFitIntoOutputInv(recipeToCraft, result)) {
			ItemStack craftingRemainder = getCraftingRemainder();

			inputInventory.removeItem(0, 1); //consume input
			outputInventory.insertItemStack(result); //output result

			if (!craftingRemainder.isEmpty()) {
				outputInventory.insertItemStack(craftingRemainder);
			}

			SoundUtil.broadcastBlockSound((ServerLevel) level, getBlockPos(), ModSoundEvents.DIGESTER_CRAFTING_COMPLETED);

			setChanged();
			return true;
		}

		return false;
	}

	private ItemStack getCraftingRemainder() {
		ItemStack stack = inputInventory.getItem(0);

		if (stack.hasCraftingRemainingItem()) {
			return stack.getCraftingRemainingItem();
		}

		if (stack.getItem() instanceof BowlFoodItem) {
			return new ItemStack(Items.BOWL);
		}

		return ItemStack.EMPTY;
	}

	public ItemStack getInputSlotStack() {
		return inputInventory.getItem(0);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		tag.put("Fuel", fuelHandler.serializeNBT());
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		fuelHandler.deserializeNBT(tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(tag.getCompound("InputSlots"));
		outputInventory.deserializeNBT(tag.getCompound("OutputSlots"));
	}

	@Override
	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, fuelInventory);
		Containers.dropContents(level, pos, inputInventory);
		Containers.dropContents(level, pos, outputInventory);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (remove) return super.getCapability(cap, side);

		if (cap == ModCapabilities.ITEM_HANDLER) {
			if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemHandler().cast();
			if (side == Direction.UP) return inputInventory.getOptionalItemHandler().cast();
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
		inputInventory.invalidate();
		outputInventory.invalidate();
		optionalFluidConsumer.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		inputInventory.revive();
		outputInventory.revive();
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	@Override
	public void setChanged() {
		syncToClient();
		super.setChanged();
	}

	//client side only
	private <E extends DigesterBlockEntity> PlayState handleAnimationState(AnimationState<E> event) {
		Boolean isCrafting = getBlockState().getValue(MachineBlock.CRAFTING);

		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(WORKING_ANIM);
			loopingSoundHelper.startLoop(this, ModSoundEvents.DIGESTER_CRAFTING.get(), 0.65f);
		}
		else {
			event.getController().setAnimation(IDLE_ANIM);
			loopingSoundHelper.stopLoop();
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::handleAnimationState));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void setRemoved() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper.clear();
		}
		super.setRemoved();
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.put("InputSlots", inputInventory.serializeNBT());
		return tag;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void syncToClient() {
		if (level != null && !level.isClientSide) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
		}
	}

}
