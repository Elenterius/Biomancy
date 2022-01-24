package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.MachineBlock;
import com.github.elenterius.biomancy.world.block.entity.state.DecomposerStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.DecomposerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecomposerBlockEntity extends MachineBlockEntity<DecomposerRecipe, DecomposerStateData> implements MenuProvider, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = DecomposerRecipe.MAX_OUTPUTS;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 5;
	public static final RecipeTypeImpl.ItemStackRecipeType<DecomposerRecipe> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;

	private final DecomposerStateData stateData = new DecomposerStateData();
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;

	private final Set<BlockPos> subEntities = new HashSet<>();

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public DecomposerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DECOMPOSER.get(), pos, state);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		inputInventory = BehavioralInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", "decomposer");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DecomposerMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, DecomposerBlockEntity decomposer) {
		decomposer.serverTick((ServerLevel) level);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected DecomposerStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		stateData.setFuelAmount(newAmount);
	}

	@Override
	public void addFuelAmount(int addAmount) {
		stateData.setFuelAmount(stateData.getFuelAmount() + addAmount);
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	@Override
	public int getFuelCost() {
		return FUEL_COST;
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
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(stackToCraft);
	}

	@Override
	protected boolean craftRecipe(DecomposerRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.assemble(inputInventory);
		if (!result.isEmpty() && outputInventory.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputInventory.getContainerSize(); idx++) {
				inputInventory.removeItem(idx, recipeToCraft.getIngredientCount()); //consume input
			}

			//primary output result
			for (VariableProductionOutput output : recipeToCraft.getOutputs()) {
				int count = output.getCount(level.random);
				if (count > 0) {
					ItemStack stack = output.getItemStack();
					stack.setCount(count);
					for (int idx = 0; idx < outputInventory.getContainerSize(); idx++) {
						stack = outputInventory.insertItemStack(idx, stack); //update stack with remainder
						if (stack.isEmpty()) break;
					}
				}
			}

			//gland output result
			List<VariableProductionOutput> byproducts = recipeToCraft.getByproducts();
			List<ItemStack> outputs = new ArrayList<>(byproducts.size());
			for (VariableProductionOutput byproduct : byproducts) {
				int count = byproduct.getCount(level.random);
				if (count > 0) {
					ItemStack stack = byproduct.getItemStack();
					stack.setCount(count);
					outputs.add(stack);
				}
			}

			if (!outputs.isEmpty()) {
				for (BlockPos pos : subEntities) {
					for (int i = 0; i < outputs.size(); i++) {
						ItemStack stack = outputs.get(i);
						if (!stack.isEmpty() && level.getBlockEntity(pos) instanceof GlandBlockEntity gland) {
							outputs.set(i, gland.insertItemStack(stack));
						}
					}
				}
			}

			setChanged();
			return true;
		}
		return false;
	}

	@Override
	protected @Nullable DecomposerRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.getRecipeFromContainer(level, inputInventory).orElse(null);
	}

	public void addSubEntity(BlockPos pos) {
		subEntities.add(pos);
		setChanged();
	}

	public void addSubEntity(GlandBlockEntity gland) {
		addSubEntity(gland.getBlockPos());
	}

	public void removeSubEntity(GlandBlockEntity gland) {
		removeSubEntity(gland.getBlockPos());
	}

	public void removeSubEntity(BlockPos pos) {
		subEntities.remove(pos);
		setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());

		if (!subEntities.isEmpty()) {
			ListTag listNBT = new ListTag();
			for (BlockPos pos : subEntities) {
				listNBT.add(LongTag.valueOf(pos.asLong()));
			}
			tag.put("SubEntitiesPos", listNBT);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(tag.getCompound("InputSlots"));
		outputInventory.deserializeNBT(tag.getCompound("OutputSlots"));

		subEntities.clear();
		if (tag.contains("SubEntitiesPos")) {
			ListTag list = tag.getList("SubEntitiesPos", Tag.TAG_LONG);
			for (Tag entry : list) {
				if (entry instanceof LongTag longTag) {
					subEntities.add(BlockPos.of(longTag.getAsLong()));
				}
			}
		}
	}

	@Override
	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, fuelInventory);
		Containers.dropContents(level, pos, inputInventory);
		Containers.dropContents(level, pos, outputInventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemHandler().cast();
			if (side == Direction.UP) return inputInventory.getOptionalItemHandler().cast();
			return fuelInventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fuelInventory.invalidate();
		inputInventory.invalidate();
		outputInventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		inputInventory.revive();
		outputInventory.revive();
	}

	private <E extends BlockEntity & IAnimatable> PlayState handleIdleAnim(AnimationEvent<E> event) {
		Boolean isCrafting = event.getAnimatable().getBlockState().getValue(MachineBlock.CRAFTING);
		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("decomposer.anim.working", true));
		}
		else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("decomposer.anim.idle.normal", true));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "idle_controller", 10, this::handleIdleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
