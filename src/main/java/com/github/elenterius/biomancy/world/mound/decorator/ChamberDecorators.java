package com.github.elenterius.biomancy.world.mound.decorator;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.Chamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ChamberDecorators {

	public static final ChamberDecorator EMPTY = new ChamberDecorator() {
		@Override
		public PartOfDecorationResult isBlockPartOfDecoration(Chamber chamber, Level level, BlockPos pos, BlockState state) {
			//consider everything as a decoration except full primal and malignant flesh blocks
			//this allows all blocks placed inside the room to survive and not be destroyed
			boolean materialValid = !PrimordialEcosystem.SOLID_FLESH_BLOCKS.contains(state.getBlock());
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

	public static final ChamberDecorator PRIMAL_FLESH_PILLARS = new PillarsDecorator(ModBlocks.PRIMAL_FLESH.get().defaultBlockState(), ModBlocks.SMOOTH_PRIMAL_FLESH.get().defaultBlockState());
	public static final ChamberDecorator MALIGNANT_FLESH_PILLARS = new PillarsDecorator(ModBlocks.MALIGNANT_FLESH.get().defaultBlockState(), ModBlocks.POROUS_PRIMAL_FLESH.get().defaultBlockState());
	public static final ChamberDecorator PRIMAL_ORIFICE_PILLARS = new PillarsDecorator(ModBlocks.PRIMAL_ORIFICE.get().defaultBlockState(), ModBlocks.PRIMAL_ORIFICE.get().defaultBlockState(), ModBlocks.PRIMAL_FLESH.get().defaultBlockState());
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

	public static ChamberDecorator getRandomDefault(RandomSource random) {
		return RANDOM_DEFAULTS.getRandomValue(random).orElse(PRIMAL_FLESH_PILLARS);
	}

}
