package com.github.elenterius.biomancy.integration.create;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.simibubi.create.content.contraptions.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public final class CreateCompat {

	private CreateCompat() {}

	public static void onPostSetup() {
		InteractionBehaviors.register();
		OpenEndedPipe.registerEffectHandler(new AcidEffectHandler());
	}

	private static class AcidEffectHandler implements OpenEndedPipe.IEffectHandler {

		@Override
		public boolean canApplyEffects(OpenEndedPipe openEndedPipe, FluidStack fluidStack) {
			return fluidStack.getFluid().isSame(ModFluids.ACID.get());
		}

		@Override
		public void applyEffects(OpenEndedPipe openEndedPipe, FluidStack fluidStack) {
			Level level = openEndedPipe.getWorld();
			if (level.getGameTime() % 5 == 0) {
				List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, openEndedPipe.getAOE(), livingEntity -> !CombatUtil.hasAcidEffect(livingEntity));
				for (LivingEntity mob : mobs) {
					CombatUtil.applyAcidEffect(mob, 4);
				}

				BlockPos.betweenClosedStream(openEndedPipe.getAOE()).forEach(pos -> corrodeCopper(level, pos));
			}
		}

		private void corrodeCopper(Level level, BlockPos pos) {
			if (level.random.nextFloat() >= 0.057f) return;

			BlockState blockState = level.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
				weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, state));
			}
		}
	}
}
