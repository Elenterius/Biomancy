package com.github.elenterius.biomancy.integration.compat.create;

import com.github.elenterius.biomancy.block.IrisDoorBlock;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.SimpleBlockMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

class IrisDoorMovingInteraction extends SimpleBlockMovingInteraction {

	@Override
	protected BlockState handle(@Nullable Player player, Contraption contraption, BlockPos pos, BlockState blockState) {

		blockState = blockState.cycle(IrisDoorBlock.OPEN);

		if (player != null) {
			boolean isDoorOpen = Boolean.TRUE.equals(blockState.getValue(IrisDoorBlock.OPEN));
			SoundEvent soundEvent = isDoorOpen ? ModSoundEvents.FLESH_DOOR_OPEN.get() : ModSoundEvents.FLESH_DOOR_CLOSE.get();
			float pitch = player.level.random.nextFloat() * 0.1f + 0.9f;
			playSound(player, soundEvent, pitch);
		}

		return blockState;
	}

	@Override
	protected boolean updateColliders() {
		return true;
	}

}
