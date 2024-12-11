package com.github.elenterius.biomancy.integration.overweightfarming;

import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.orcinus.overweightfarming.blocks.CropFullBlock;
import net.orcinus.overweightfarming.init.OFBlocks;
import net.orcinus.overweightfarming.util.OverweightGrowthManager;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class OverweightFarmingIntegration {

	private OverweightFarmingIntegration() {}

	public static void init(Consumer<OverweightFarmingHelper> helperSetter) {
		helperSetter.accept(new OverweightFarmingHelperImpl());
	}

	public static void onPostSetup() {
		Tribute goldenApple = Tributes.getTribute(Items.GOLDEN_APPLE.getDefaultInstance());
		int multiplier = 10;
		Tributes.register(OFBlocks.OVERWEIGHT_GOLDEN_APPLE.get().asItem(), new SimpleTribute(
				goldenApple.biomass() * multiplier,
				goldenApple.lifeEnergy() * multiplier,
				goldenApple.successModifier() * multiplier,
				goldenApple.diseaseModifier() * multiplier,
				goldenApple.hostileModifier() * multiplier,
				goldenApple.anomalyModifier() * multiplier
		));
	}

	private static Block getBlock(String name) {
		ResourceLocation id = new ResourceLocation("overweight_farming", name);
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(id));
	}

	private static Item getItem(String name) {
		ResourceLocation id = new ResourceLocation("overweight_farming", name);
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id));
	}

	static final class OverweightFarmingHelperImpl implements OverweightFarmingHelper {

		private final OverweightGrowthManager growthManager = new OverweightGrowthManager(RandomSource.create());
		private Set<Block> validCrops;
		private Set<Block> overweightCrops;

		@Override
		public boolean isOverweightBlock(Block block) {
			if (overweightCrops == null) {
				overweightCrops = OFBlocks.BLOCKS.getEntries().stream()
						.map(RegistryObject::get)
						.filter(CropFullBlock.class::isInstance)
						.collect(Collectors.toSet());
			}
			return overweightCrops.contains(block);
		}

		@Override
		public boolean canGrowOverweight(Level level, BlockPos pos, BlockState state) {
			if (validCrops == null) {
				validCrops = growthManager.getOverweightMap().keySet();
			}
			return validCrops.contains(state.getBlock()) && level.isStateAtPosition(pos.above(), DripstoneUtils::isEmptyOrWater);
		}

		@Override
		public void growOverweight(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
			growthManager.growOverweightCrops(level, pos, state, random);
		}

	}

}
