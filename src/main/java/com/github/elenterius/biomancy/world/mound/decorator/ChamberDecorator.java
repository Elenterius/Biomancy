package com.github.elenterius.biomancy.world.mound.decorator;

import com.github.elenterius.biomancy.world.mound.Chamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ChamberDecorator {
	PartOfDecorationResult isBlockPartOfDecoration(Chamber chamber, Level level, BlockPos pos, BlockState state);

	boolean canPlace(Chamber chamber, Level level, BlockPos pos, Direction axisDirection);

	boolean place(Chamber chamber, Level level, BlockPos pos, Direction axisDirection);

	public enum PartOfDecorationResult {
		POSITION_AND_MATERIAL_ARE_VALID(true, true),
		POSITION_AND_MATERIAL_ARE_INVALID(false, false),
		POSITION_IS_VALID_AND_MATERIAL_IS_INVALID(true, false),
		POSITION_IS_INVALID_AND_MATERIAL_IS_VALID(false, true);

		static final PartOfDecorationResult[] sortedValues;

		static {
			sortedValues = new PartOfDecorationResult[values().length];
			for (PartOfDecorationResult result : values()) {
				int index = getIndex(result.positionIsValid, result.materialIsValid);
				sortedValues[index] = result;
			}
		}

		public final boolean positionIsValid;
		public final boolean materialIsValid;

		PartOfDecorationResult(boolean positionIsValid, boolean materialIsValid) {
			this.positionIsValid = positionIsValid;
			this.materialIsValid = materialIsValid;
		}

		public int index() {
			return getIndex(positionIsValid, materialIsValid);
		}

		public static PartOfDecorationResult of(boolean positionValid, boolean materialValid) {
			int index = getIndex(positionValid, materialValid);
			return sortedValues[index];
		}

		private static int getIndex(boolean positionValid, boolean materialValid) {
			return (positionValid ? 1 : 0) << 1 | (materialValid ? 1 : 0);
		}
	}
}
