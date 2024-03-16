package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModPlantTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class FleshBlock extends Block {

	protected final PlantType supportedPlantType;

	public FleshBlock(Properties properties) {
		super(properties);
		supportedPlantType = ModPlantTypes.FLESH;
	}

	public FleshBlock(Properties properties, PlantType supportedPlantType) {
		super(properties);
		this.supportedPlantType = supportedPlantType;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return type == supportedPlantType;
	}

}
