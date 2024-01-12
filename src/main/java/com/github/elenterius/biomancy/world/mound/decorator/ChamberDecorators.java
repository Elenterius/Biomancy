package com.github.elenterius.biomancy.world.mound.decorator;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.mound.Chamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public final class ChamberDecorators {

	public static final ChamberDecorator EMPTY = new ChamberDecorator() {
		@Override
		public PartOfDecorationResult isBlockPartOfDecoration(Chamber chamber, Level level, BlockPos pos, BlockState state) {
			//consider everything as a decoration except full primal and malignant flesh blocks
			//this allows all blocks placed inside the room to survive and not be destroyed
			boolean materialValid = !state.is(ModBlocks.PRIMAL_FLESH.get()) && !state.is(ModBlocks.MALIGNANT_FLESH.get());
			return PartOfDecorationResult.of(true, materialValid);
		}

		@Override
		public boolean canPlace(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
			return false;
		}

		@Override
		public boolean place(Chamber chamber, Level level, BlockPos pos, Direction axisDirection) {
			return false;
		}
	};

	public static final ChamberDecorator PRIMAL_FLESH_PILLARS = new PillarsDecorator(ModBlocks.PRIMAL_FLESH.get().defaultBlockState());
	public static final ChamberDecorator MALIGNANT_FLESH_PILLARS = new PillarsDecorator(ModBlocks.MALIGNANT_FLESH.get().defaultBlockState());
	public static final ChamberDecorator PRIMAL_ORIFICE_PILLARS = new PillarsDecorator(ModBlocks.PRIMAL_ORIFICE.get().defaultBlockState());
	public static final ChamberDecorator PRIMAL_ORIFICE_COMBS = new HangingCombsDecorator(ModBlocks.PRIMAL_ORIFICE.get().defaultBlockState());
	public static final ChamberDecorator BONE_PILLARS = new PillarsDecorator(Blocks.BONE_BLOCK.defaultBlockState());

	public static final SimpleWeightedRandomList<ChamberDecorator> RANDOM_DEFAULTS = SimpleWeightedRandomList.<ChamberDecorator>builder()
			.add(EMPTY, 10)
			.add(PRIMAL_FLESH_PILLARS, 25)
			.add(MALIGNANT_FLESH_PILLARS, 15)
			.add(PRIMAL_ORIFICE_PILLARS, 5)
			.add(PRIMAL_ORIFICE_COMBS, 7)
			.add(BONE_PILLARS, 17)
			.build();

	private ChamberDecorators() {}

	public static ChamberDecorator getRandomDefault(Random random) {
		return RANDOM_DEFAULTS.getRandomValue(random).orElse(PRIMAL_FLESH_PILLARS);
	}

}
