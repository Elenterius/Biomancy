package com.github.elenterius.biomancy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

	public final ForgeConfigSpec.BooleanValue doBioForgeRecipeProgression;
	public final ForgeConfigSpec.BooleanValue addTradesToVillagers;
	public final ForgeConfigSpec.BooleanValue addTradesToWanderingTrader;
	public final ForgeConfigSpec.EnumValue<PrimalEnergySettings.SupplyAmount> primalEnergySupplyOfCradle;

	public ServerConfig(ForgeConfigSpec.Builder builder) {
		builder.push("recipes");

		doBioForgeRecipeProgression = builder
				.comment("Determines if the BioForge recipes need to be unlocked to be able to craft them")
				.define("doBioForgeRecipeProgression", false);

		builder.pop();

		builder.push("trades");

		addTradesToVillagers = builder
				.comment("Determines if villagers will sell biomancy items")
				.define("addTradesToVillagers", true);

		addTradesToWanderingTrader = builder
				.comment("Determines if wandering traders will sell biomancy items")
				.define("addTradesToWanderingTrader", true);

		builder.pop();

		builder.push("flesh-growth");

		primalEnergySupplyOfCradle = builder
				.comment("Determines how much primal energy the Cradle can supply to nearby malignant flesh veins")
				.defineEnum("primalEnergySupplyOfCradle", PrimalEnergySettings.SupplyAmount.LIMITED);

		builder.pop();
	}

}
