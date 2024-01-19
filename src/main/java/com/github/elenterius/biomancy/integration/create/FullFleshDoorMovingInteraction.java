package com.github.elenterius.biomancy.integration.create;

import com.github.elenterius.biomancy.block.FullFleshDoorBlock;
import com.github.elenterius.biomancy.block.property.Orientation;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

class FullFleshDoorMovingInteraction extends SimpleBlockMovingInteraction {

	@Override
	protected BlockState handle(@Nullable Player player, Contraption contraption, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof FullFleshDoorBlock doorBlock)) return state;

		state = state.cycle(FullFleshDoorBlock.OPEN);
		updateDoorPart(contraption, pos, state, doorBlock);

		setDoubleDoorOpen(contraption, pos, state, doorBlock, doorBlock.isOpen(state));

		if (player != null) {
			SoundEvent soundEvent = doorBlock.isOpen(state) ? ModSoundEvents.FLESH_DOOR_OPEN.get() : ModSoundEvents.FLESH_DOOR_CLOSE.get();
			float pitch = player.level().random.nextFloat() * 0.1f + 0.9f;
			playSound(player, soundEvent, pitch);
		}

		return state;
	}

	private void updateDoorPart(Contraption contraption, BlockPos pos, BlockState state, FullFleshDoorBlock doorBlock) {
		BlockPos offsetPos = doorBlock.isLowerHalf(state) ? pos.above() : pos.below();
		StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(offsetPos);
		BlockState newState = info.state().setValue(FullFleshDoorBlock.OPEN, doorBlock.isOpen(state));
		setContraptionBlockData(contraption.entity, offsetPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
	}

	private void setDoubleDoorOpen(Contraption contraption, BlockPos pos, BlockState state, FullFleshDoorBlock doorBlock, boolean open) {
		DoorHingeSide hinge = state.getValue(FullFleshDoorBlock.HINGE);
		Orientation orientation = state.getValue(FullFleshDoorBlock.ORIENTATION);

		BlockPos otherPos;
		if (orientation.axis == Direction.Axis.X) {
			otherPos = pos.relative(hinge == DoorHingeSide.RIGHT ? Direction.NORTH : Direction.SOUTH);
		}
		else if (orientation.axis == Direction.Axis.Z) {
			otherPos = pos.relative(hinge == DoorHingeSide.RIGHT ? Direction.WEST : Direction.EAST);
		}
		else return;

		StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(otherPos);
		if (!info.state().is(state.getBlock())) return;
		if (info.state().getValue(FullFleshDoorBlock.ORIENTATION) != orientation || info.state().getValue(FullFleshDoorBlock.HINGE) == hinge) return;

		if (doorBlock.isOpen(info.state()) != open) { //only updated connected door if its open state mismatches the targetState
			BlockState newState = info.state().setValue(FullFleshDoorBlock.OPEN, open);
			setContraptionBlockData(contraption.entity, otherPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
			updateDoorPart(contraption, otherPos, newState, doorBlock);
		}
	}

	@Override
	protected boolean updateColliders() {
		return true;
	}

}
