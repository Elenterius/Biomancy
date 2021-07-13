package com.github.elenterius.biomancy.inventory.fluidhandler;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class FluidHandlerDelegator<FH extends IFluidHandler> implements IFluidHandler {

	protected final FH fluidHandler;

	public FluidHandlerDelegator(FH fluidHandlerIn) {
		fluidHandler = fluidHandlerIn;
	}

	@Override
	public int getTanks() {
		return fluidHandler.getTanks();
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return fluidHandler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return fluidHandler.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return fluidHandler.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return fluidHandler.fill(resource, action);
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return fluidHandler.drain(resource, action);
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return fluidHandler.drain(maxDrain, action);
	}

	public static class DenyInput<FH extends IFluidHandler> extends FluidHandlerDelegator<FH> {

		public DenyInput(FH fluidHandlerIn) {
			super(fluidHandlerIn);
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

	}

	public static class FilterInput<FH extends IFluidHandler> extends FluidHandlerDelegator<FH> {

		private final Predicate<Fluid> validFluids;

		public FilterInput(FH fluidHandlerIn, Predicate<Fluid> validFluids) {
			super(fluidHandlerIn);
			this.validFluids = validFluids;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return validFluids.test(stack.getFluid()) && fluidHandler.isFluidValid(tank, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!validFluids.test(resource.getFluid())) return 0;
			return fluidHandler.fill(resource, action);
		}

	}

}
