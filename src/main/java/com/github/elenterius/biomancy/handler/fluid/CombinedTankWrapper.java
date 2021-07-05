/*
 * MIT License
 *
 * Copyright (c) 2019 simibubi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.elenterius.biomancy.handler.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

/**
 * Combines multiple IFluidHandlers into one interface (See CombinedInvWrapper
 * for items)
 * <p>
 * copied on 2021/07/05 from https://github.com/Creators-of-Create/Create/blob/d4a4e245bd8b6c8346f4bf29add51b92acc57de0/src/main/java/com/simibubi/create/foundation/fluid/CombinedTankWrapper.java
 */
public class CombinedTankWrapper implements IFluidHandler {

	protected final IFluidHandler[] itemHandler;
	protected final int[] baseIndex;
	protected final int tankCount;
	protected boolean enforceVariety;

	public CombinedTankWrapper(IFluidHandler... fluidHandlers) {
		this.itemHandler = fluidHandlers;
		this.baseIndex = new int[fluidHandlers.length];
		int index = 0;
		for (int i = 0; i < fluidHandlers.length; i++) {
			index += fluidHandlers[i].getTanks();
			baseIndex[i] = index;
		}
		this.tankCount = index;
	}

	public CombinedTankWrapper enforceVariety() {
		enforceVariety = true;
		return this;
	}

	@Override
	public int getTanks() {
		return tankCount;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		tank = getSlotFromIndex(tank, index);
		return handler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.getTankCapacity(localSlot);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.isFluidValid(localSlot, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return 0;

		int filled = 0;
		resource = resource.copy();

		boolean fittingHandlerFound = false;
		boolean[] trueAndFalse = new boolean[]{true, false};
		Outer:
		for (boolean searchPass : trueAndFalse) {
			for (IFluidHandler iFluidHandler : itemHandler) {

				for (int i = 0; i < iFluidHandler.getTanks(); i++)
					if (searchPass && iFluidHandler.getFluidInTank(i).isFluidEqual(resource))
						fittingHandlerFound = true;

				if (searchPass && !fittingHandlerFound)
					continue;

				int filledIntoCurrent = iFluidHandler.fill(resource, action);
				resource.shrink(filledIntoCurrent);
				filled += filledIntoCurrent;

				if (resource.isEmpty() || fittingHandlerFound || enforceVariety && filledIntoCurrent != 0)
					break Outer;
			}
		}

		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return resource;

		FluidStack drained = FluidStack.EMPTY;
		resource = resource.copy();

		for (IFluidHandler iFluidHandler : itemHandler) {
			FluidStack drainedFromCurrent = iFluidHandler.drain(resource, action);
			int amount = drainedFromCurrent.getAmount();
			resource.shrink(amount);

			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(), drainedFromCurrent.getTag());
			if (resource.isEmpty())
				break;
		}

		return drained;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack drained = FluidStack.EMPTY;

		for (IFluidHandler iFluidHandler : itemHandler) {
			FluidStack drainedFromCurrent = iFluidHandler.drain(maxDrain, action);
			int amount = drainedFromCurrent.getAmount();
			maxDrain -= amount;

			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(), drainedFromCurrent.getTag());
			if (maxDrain == 0)
				break;
		}

		return drained;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0) return -1;
		for (int i = 0; i < baseIndex.length; i++) {
			if (slot - baseIndex[i] < 0) return i;
		}
		return -1;
	}

	protected IFluidHandler getHandlerFromIndex(int index) {
		if (index < 0 || index >= itemHandler.length)
			return (IFluidHandler) EmptyHandler.INSTANCE;

		return itemHandler[index];
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length)
			return slot;

		return slot - baseIndex[index - 1];
	}
}
