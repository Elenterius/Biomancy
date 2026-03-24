package com.github.elenterius.biomancy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

	public final ForgeConfigSpec.BooleanValue doBioForgeRecipeProgression;
	public final ForgeConfigSpec.BooleanValue doTransliteration;
	public final ForgeConfigSpec.BooleanValue addTradesToVillagers;
	public final ForgeConfigSpec.BooleanValue addTradesToWanderingTrader;
	public final ForgeConfigSpec.EnumValue<PrimalEnergySettings.SupplyAmount> primalEnergySupplyOfCradle;

	public final ForgeConfigSpec.DoubleValue absorptionMaxHearts;
	public final ForgeConfigSpec.DoubleValue absorptionHearts;

	public final ForgeConfigSpec.DoubleValue pehkuiMaxScale;
	public final ForgeConfigSpec.DoubleValue pehkuiMinScale;
	public final ForgeConfigSpec.DoubleValue pehkuiScaleIncrement;
	public final ForgeConfigSpec.DoubleValue pehkuiScaleDecrement;

	public ServerConfig(ForgeConfigSpec.Builder builder) {
		builder.push("recipes");
		doBioForgeRecipeProgression = builder
				.comment("Determines if the BioForge recipes need to be unlocked to be able to craft them")
				.define("doBioForgeRecipeProgression", true);
		builder.pop();

		builder.push("transliteration");
		doTransliteration = builder
				.comment(
						"Determines if clients (afflicted by primordial infestation) transliterate non-latin characters before sending text (chat messages, sign editing, item renaming) to the server.",
						"Disabling this feature makes only sense if you use a resource pack that expands the 'Caro Invitica' font with the missing non-latin characters from your language."
				)
				.define("doTransliteration", true);
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

		builder.push("absorption-serum");
		absorptionMaxHearts = builder
				.comment("Maximum number of absorption hearts.")
				.defineInRange("maxHearts", 10d, 0.5d, 1000d);
		absorptionHearts = builder
				.comment("How many absorption hearts to add on each injection.")
				.defineInRange("heartsIncrement", 2d, 0.5d, 100d);
		builder.pop();

		builder.push("pehkui-integration");
		builder.push("enlargement-serum");
		pehkuiMaxScale = builder
				.comment("Maximum scale a mob/player can reach. A value of 2.0 doubles the size of the mob/player.")
				.defineInRange("maxScale", 2d, 0.01d, 100d);
		pehkuiScaleIncrement = builder
				.comment("How much to add to the scale on each injection.")
				.defineInRange("scaleStep", 0.25d, 0.01d, 100d);
		builder.pop();
		builder.push("shrinking-serum");
		pehkuiMinScale = builder
				.comment("Minimum scale a mob/player can reach. A value of 0.5 half's the size of the mob/player.")
				.defineInRange("minScale", 0.25d, 0.01d, 100d);
		pehkuiScaleDecrement = builder
				.comment("How much to subtract from the scale on each injection.")
				.defineInRange("scaleStep", 0.25d, 0.01d, 100d);
		builder.pop();
		builder.pop();
	}

}
