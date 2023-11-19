package com.github.elenterius.biomancy.block.cradle;

public interface PrimalEnergyHandler {

	int getPrimalEnergy();

	/**
	 * @param amount
	 * @return the amount that was successfully filled
	 */
	int fillPrimalEnergy(int amount);

	/**
	 * @param amount
	 * @return the amount that was successfully drained
	 */
	int drainPrimalEnergy(int amount);

}
